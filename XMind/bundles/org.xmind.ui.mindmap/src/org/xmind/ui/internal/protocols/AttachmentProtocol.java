/* ******************************************************************************
 * Copyright (c) 2006-2012 XMind Ltd. and others.
 * 
 * This file is a part of XMind 3. XMind releases 3 and
 * above are dual-licensed under the Eclipse Public License (EPL),
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 * and the GNU Lesser General Public License (LGPL), 
 * which is available at http://www.gnu.org/licenses/lgpl.html
 * See http://www.xmind.net/license.html for details.
 * 
 * Contributors:
 *     XMind Ltd. - initial API and implementation
 *******************************************************************************/
package org.xmind.ui.internal.protocols;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.xmind.core.Core;
import org.xmind.core.IFileEntry;
import org.xmind.core.INamed;
import org.xmind.core.ITitled;
import org.xmind.core.IWorkbook;
import org.xmind.core.event.ICoreEventListener;
import org.xmind.core.event.ICoreEventSource2;
import org.xmind.core.util.HyperlinkUtils;
import org.xmind.ui.internal.MindMapMessages;
import org.xmind.ui.internal.editor.MME;
import org.xmind.ui.mindmap.IMindMapImages;
import org.xmind.ui.mindmap.IProtocol;
import org.xmind.ui.mindmap.MindMapUI;
import org.xmind.ui.util.MindMapUtils;

public class AttachmentProtocol implements IProtocol {

    private static class AttachmentAction extends Action {

        private IWorkbenchWindow window;

        private IWorkbook workbook;

        private String path;

        private String fileName;

        public AttachmentAction(IWorkbenchWindow window, IWorkbook workbook,
                String path, String fileName) {
            this.window = window;
            this.workbook = workbook;
            this.path = path;
            this.fileName = fileName;
        }

        public void run() {
            String hiberLoc = workbook.getTempLocation();
            if (hiberLoc == null)
                return;

            File hiberDir = new File(hiberLoc);
            if (!hiberDir.isDirectory())
                return;

            File attFile = new File(hiberDir, path);
            if (!attFile.exists())
                return;

            MME.launch(window, attFile.getAbsolutePath(), fileName);

            if (workbook instanceof ICoreEventSource2) {
                ((ICoreEventSource2) workbook).registerOnceCoreEventListener(
                        Core.WorkbookPreSaveOnce, ICoreEventListener.NULL);
            }
        }

    }

    private Map<IWorkbook, Map<String, IAction>> actions = null;

    public IAction createOpenHyperlinkAction(Object context, String uri) {
        if (uri == null)
            return null;

        String path = HyperlinkUtils.toAttachmentPath(uri);
        if (path == null)
            return null;

        IWorkbook workbook = MindMapUtils.findWorkbook(context);
        if (workbook == null)
            return null;

        if (actions == null)
            actions = new HashMap<IWorkbook, Map<String, IAction>>();
        Map<String, IAction> wbActions = actions.get(workbook);
        if (wbActions == null) {
            wbActions = new HashMap<String, IAction>();
            actions.put(workbook, wbActions);
        }
        IAction action = wbActions.get(uri);
        if (action == null) {
            action = createOpenAttachmentAction(getWindow(context), workbook,
                    path, getFileName(context));
            wbActions.put(uri, action);
        }
        return action;

    }

    private IAction createOpenAttachmentAction(IWorkbenchWindow window,
            IWorkbook workbook, String path, String fileName) {
        IAction action = new AttachmentAction(window, workbook, path, fileName);
        action.setText(MindMapMessages.OpenAttachment_text);
        action.setToolTipText(fileName);
        ImageDescriptor image = MindMapUI.getImages().getFileIcon(path, true);
        if (image == null) {
            IFileEntry e = workbook.getManifest().getFileEntry(path);
            if (e != null && e.isDirectory()) {
                image = MindMapUI.getImages().get(IMindMapImages.OPEN, true);
            } else {
                image = MindMapUI.getImages().get(IMindMapImages.UNKNOWN_FILE,
                        true);
            }
        }
        action.setImageDescriptor(image);
        return action;
    }

    private static String getFileName(Object context) {
        if (context instanceof IAdaptable) {
            Object adapter = ((IAdaptable) context).getAdapter(ITitled.class);
            if (adapter == null) {
                adapter = ((IAdaptable) context).getAdapter(INamed.class);
            }
            if (adapter != null) {
                context = adapter;
            }
        }
        if (context instanceof ITitled)
            return ((ITitled) context).getTitleText();
        if (context instanceof INamed)
            return ((INamed) context).getName();
        return null;
    }

    private static IWorkbenchWindow getWindow(Object context) {
        if (context instanceof IAdaptable) {
            Object adapter = ((IAdaptable) context)
                    .getAdapter(IWorkbenchWindow.class);
            if (adapter == null) {
                adapter = ((IAdaptable) context).getAdapter(IEditorPart.class);
                if (adapter == null) {
                    adapter = ((IAdaptable) context)
                            .getAdapter(IWorkbenchPart.class);
                }
                if (adapter instanceof IWorkbenchPart)
                    adapter = ((IWorkbenchPart) adapter).getSite()
                            .getWorkbenchWindow();
            }
            if (adapter instanceof IWorkbenchWindow)
                return (IWorkbenchWindow) adapter;
        }
        if (context instanceof IWorkbenchWindow)
            return (IWorkbenchWindow) context;
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    public String getProtocolName() {
        return HyperlinkUtils.getAttachmentProtocolName();
    }

    public boolean isHyperlinkModifiable(Object source, String uri) {
        return false;
    }

}
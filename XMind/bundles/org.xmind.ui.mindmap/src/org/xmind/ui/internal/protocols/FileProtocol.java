package org.xmind.ui.internal.protocols;

import java.io.File;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.xmind.core.IWorkbook;
import org.xmind.ui.internal.MindMapMessages;
import org.xmind.ui.internal.editor.MME;
import org.xmind.ui.mindmap.IMindMapImages;
import org.xmind.ui.mindmap.IProtocol;
import org.xmind.ui.mindmap.MindMapUI;
import org.xmind.ui.util.MindMapUtils;

public class FileProtocol implements IProtocol {

    private static class OpenFileAction extends Action {

        private IWorkbenchWindow window;

        private String path;

        public OpenFileAction(IWorkbenchWindow window, String path) {
            this.window = window;
            this.path = path;
        }

        public void run() {
            MME.launch(window, path, new File(path).getName());
        }

    }

    public FileProtocol() {
    }

    public IAction createOpenHyperlinkAction(Object context, final String uri) {
        IWorkbenchWindow window = getWindow(context);
        String path = FilePathParser.toPath(uri);
        String absolutePath = getAbsolutePath(context, path);
        File file = new File(absolutePath);
        ImageDescriptor image = MindMapUI.getImages().getFileIcon(absolutePath,
                true);
        if (image == null) {
            if (file.isDirectory()) {
                image = MindMapUI.getImages().get(IMindMapImages.OPEN, true);
            } else {
                image = MindMapUI.getImages().get(IMindMapImages.UNKNOWN_FILE,
                        true);
            }
        }
        String text;
        if (file.isDirectory()) {
            text = MindMapMessages.FileProtocol_OpenFolder_text;
        } else {
            text = MindMapMessages.FileProtocol_OpenFile_text;
        }
        OpenFileAction action = new OpenFileAction(window, absolutePath);
        action.setText(text);
        action.setImageDescriptor(image);
        action.setToolTipText(absolutePath);
        return action;
    }

    public static String getAbsolutePath(Object context, String path) {
        if (FilePathParser.isPathRelative(path)) {
            IWorkbook workbook = MindMapUtils.findWorkbook(context);
            if (workbook != null) {
                String base = workbook.getFile();
                if (base != null) {
                    base = new File(base).getParent();
                    if (base != null) {
                        return FilePathParser.toAbsolutePath(base, path);
                    }
                }
            }
            return FilePathParser.toAbsolutePath(
                    FilePathParser.ABSTRACT_FILE_BASE, path);
        }
        return path;
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

    public boolean isHyperlinkModifiable(Object source, String uri) {
        return true;
    }
}

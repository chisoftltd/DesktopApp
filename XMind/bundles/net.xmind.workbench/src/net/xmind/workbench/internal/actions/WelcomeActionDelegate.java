package net.xmind.workbench.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Show welcome information to XMind users.
 * 
 * @author Frank Shaka
 * @deprecated Functions moved to org.xmind.cathy and menu declaration moved to
 *             org.xmind.ui.menus.
 */
public class WelcomeActionDelegate implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        this.window = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.
     * IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if (window == null)
            return;
//
//        IAccountInfo accountInfo = XMindNet.getAccountInfo();
//        if (accountInfo != null) {
//            XMindNet.gotoURL(XMindNetWorkbench.URL_WELCOME_USER,
//                    accountInfo.getUser(), accountInfo.getAuthToken());
//        } else {
//            XMindNet.gotoURL(XMindNetWorkbench.URL_WELCOME);
//        }
//        URL url = FileLocator.find(Platform
//                .getBundle(XMindNetWorkbench.PLUGIN_ID), new Path(
//                "$nl$/resource/Welcome to XMind.xmind"), null); //$NON-NLS-1$
//        try {
//            InputStream inputStream = url.openStream();
//            IEditorInput input = MME.createTemplatedEditorInput(
//                    Messages.WelcomeToXMind_editorTitle, inputStream);
//            window.getActivePage().openEditor(input,
//                    MindMapUI.MINDMAP_EDITOR_ID);
//        } catch (Exception e) {
//            XMindNetWorkbench.log(e, null);
//        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
     * .IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        // do nothing
    }

}

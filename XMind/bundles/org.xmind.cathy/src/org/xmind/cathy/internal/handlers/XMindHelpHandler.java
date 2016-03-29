package org.xmind.cathy.internal.handlers;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xmind.cathy.internal.CathyPlugin;

import net.xmind.signin.XMindNet;

public class XMindHelpHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        String internalURL = findInternalHelpURL();
        if (internalURL != null) {
            XMindNet.gotoURL(internalURL);
        } else {
            XMindNet.gotoURL(true, CathyPlugin.ONLINE_HELP_URL);
        }

        return null;
    }

    private String findInternalHelpURL() {
        Bundle helpBundle = Platform.getBundle("org.xmind.ui.help"); //$NON-NLS-1$
        if (helpBundle != null) {
            URL url = FileLocator.find(helpBundle,
                    new Path("$nl$/contents/index.html"), null); //$NON-NLS-1$
            if (url != null) {
                try {
                    url = FileLocator.toFileURL(url);
                    return url.toExternalForm();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}

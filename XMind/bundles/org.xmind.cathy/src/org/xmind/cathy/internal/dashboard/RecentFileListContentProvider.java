package org.xmind.cathy.internal.dashboard;

import java.net.URI;
import java.util.ArrayList;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.xmind.ui.IEditorHistory;
import org.xmind.ui.IEditorHistory.IEditorHistoryListener;
import org.xmind.ui.mindmap.MindMapUI;

public class RecentFileListContentProvider extends EventManager
        implements IStructuredContentProvider, IEditorHistoryListener,
        IPropertyChangeListener {

    private static final int DEFAULT_ITEM_COUNT = 10;

    private IEditorHistory history = null;

    private IPreferenceStore preferenceStore = null;

    public void dispose() {
        clearListeners();
        if (preferenceStore != null) {
            preferenceStore.removePropertyChangeListener(this);
            preferenceStore = null;
        }
        if (history != null) {
            history.removeEditorHistoryListener(this);
            history = null;
        }
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput != history) {
            if (history != null) {
                history.removeEditorHistoryListener(this);
            }
            history = (newInput instanceof IEditorHistory)
                    ? (IEditorHistory) newInput : null;
            if (history != null) {
                history.addEditorHistoryListener(this);
            }

            if (history != null) {
                if (preferenceStore == null) {
                    preferenceStore = WorkbenchPlugin.getDefault()
                            .getPreferenceStore();
                    preferenceStore.addPropertyChangeListener(this);
                }
            } else {
                if (preferenceStore != null) {
                    preferenceStore.removePropertyChangeListener(this);
                    preferenceStore = null;
                }
            }
        }
    }

    public Object[] getElements(Object inputElement) {
        if (inputElement != history)
            return new Object[0];
        return getRecentInputURIs();
    }

    public URI[] getRecentInputURIs() {
        if (history == null)
            return new URI[0];
        int itemsToShow = getItemCount();
        if (itemsToShow <= 0)
            return new URI[0];

        URI[] recentInputURIs = history
                .getRecentInputURIs(history.getAllInputURIs().length);
        URI[] sortedInput = sort(recentInputURIs);
        itemsToShow = Math.max(0,
                Math.min(itemsToShow, recentInputURIs.length));
        URI[] content = new URI[itemsToShow];
        System.arraycopy(sortedInput, 0, content, 0, itemsToShow);
        return content;
    }

    private URI[] sort(URI[] source) {
        int pinLocation = 0;
        ArrayList<URI> result = new ArrayList<URI>(source.length);

        IEditorHistory eh = MindMapUI.getEditorHistory();
        for (URI uri : source) {
            if (eh.isPin(uri)) {
                result.add(pinLocation++, uri);
            } else {
                result.add(uri);
            }
        }
        return result.toArray(new URI[source.length]);
    }

    private int getItemCount() {
        if (preferenceStore == null)
            return DEFAULT_ITEM_COUNT;
        return preferenceStore.getInt(IPreferenceConstants.RECENT_FILES);
    }

    private void fireContentChanged() {
        Object[] listeners = getListeners();
        for (int i = 0; i < listeners.length; i++) {
            ((Runnable) listeners[i]).run();
        }
    }

    public void editorHistoryChanged() {
        fireContentChanged();
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (IPreferenceConstants.RECENT_FILES.equals(event.getProperty())) {
            fireContentChanged();
        }
    }

    public void addContentChangeListener(Runnable listener) {
        addListenerObject(listener);
    }

    public void removeContentChangeListener(Runnable listener) {
        removeListenerObject(listener);
    }

}

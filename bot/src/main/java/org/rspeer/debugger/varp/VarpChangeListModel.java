package org.rspeer.debugger.varp;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Yasper
 * <p>
 * Custom list model to allow filtering from a JList. Sorts the entries by their time of creation.
 */
public final class VarpChangeListModel extends AbstractListModel<VarpChange> {

    private final List<VarpChange> hidden = new ArrayList<>();
    private final List<VarpChange> visible = new ArrayList<>();

    private String filter = "";

    public void setFilter(String filter) {
        this.filter = filter;
        hide();
    }

    public void add(int index, VarpChange change) {
        visible.add(index, change);
        hide();
    }

    private void sort() {
        Collections.sort(visible);
        fireContentsChanged(this, 0, visible.size());
    }

    private void hide() {
        List<VarpChange> toRemove = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            VarpChange change = getElementAt(i);
            if (change != null && !matches(filter, change)) {
                hidden.add(change);
                toRemove.add(change);
            }
        }

        visible.removeAll(toRemove);

        toRemove = new ArrayList<>();
        for (VarpChange hiddenVarp : hidden) {
            if (matches(filter, hiddenVarp)) {
                visible.add(hiddenVarp);
                toRemove.add(hiddenVarp);
            }
        }
        hidden.removeAll(toRemove);

        sort();
    }

    private boolean matches(String filter, VarpChange change) {
        String varpString = String.valueOf(change.getIndex());
        return varpString.contains(filter);
    }

    @Override
    public int getSize() {
        return visible.size();
    }

    @Override
    public VarpChange getElementAt(int index) {
        return index >= 0 && index < visible.size() ? visible.get(index) : null;
    }
}

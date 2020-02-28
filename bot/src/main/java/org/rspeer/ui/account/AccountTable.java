package org.rspeer.ui.account;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public final class AccountTable extends JTable implements ComponentListener {

    private static final double[] COLUMN_WIDTH_WEIGHTS = {24.0, 19.0, 13.0, 23.0, 21.0};

    AccountTable(AccountTableModel model) {
        super(model);
        setRowHeight(32);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(Object.class, center);

        JTextField field = new JTextField();
        field.setBorder(null);

        // Render password when editing
        PasswordCellEditor editor = new PasswordCellEditor(field);
        getColumnModel().getColumn(1).setCellEditor(editor);
        resize();
        addComponentListener(this);
    }

    private void resize() {
        TableColumn column;
        TableColumnModel model = getColumnModel();
        int count = model.getColumnCount();
        for (int i = 0; i < count; i++) {
            column = model.getColumn(i);
            int weightedWidth = (int) Math.round(COLUMN_WIDTH_WEIGHTS[i] * getWidth() / 100.0);
            column.setPreferredWidth(weightedWidth);
        }
    }

    @Override
    public AccountTableModel getModel() {
        return (AccountTableModel) super.getModel();
    }

    JScrollPane getWrappedTable() {
        return new JScrollPane(this);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        resize();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}

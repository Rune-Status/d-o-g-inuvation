package org.rspeer.ui.account;

import org.rspeer.api.commons.GameAccount;

import javax.swing.*;
import java.awt.*;

public final class PasswordCellEditor extends DefaultCellEditor {

    PasswordCellEditor(JTextField field) {
        super(field);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        GameAccount account = ((AccountTable) table).getModel().getAccount(row);
        return super.getTableCellEditorComponent(table, account.getPassword(), isSelected, row, column);
    }
}

package org.rspeer.ui.account;

import org.rspeer.api.commons.GameAccount;

import javax.swing.table.AbstractTableModel;

public final class AccountTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Username", "Password", "Bank pin"};

    private final XorSerializedAccountList accounts;

    AccountTableModel() {
        this.accounts = new XorSerializedAccountList();
    }

    void addEmptyRow() {
        int size = accounts.size();
        accounts.add(new GameAccount("", ""));
        fireTableRowsInserted(size, size);
    }

    void addRow(GameAccount account) {
        int size = accounts.size();
        accounts.add(account);
        fireTableRowsInserted(size, size);
        accounts.serialize();
    }

    void removeRow(int index) {
        accounts.remove(index);
        accounts.serialize();
        fireTableRowsDeleted(index, index);
    }

    GameAccount getAccount(int row) {
        return accounts.get(row);
    }

    void saveAllAccounts() {
        accounts.serialize();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        if (column >= COLUMN_NAMES.length || column < 0) {
            return "";
        }

        return COLUMN_NAMES[column];
    }

    @Override
    public int getRowCount() {
        return accounts.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount() || columnIndex >= getColumnCount()) {
            return;
        }

        GameAccount current = accounts.get(rowIndex);
        if (current == null) {
            return;
        }

        GameAccount replace;
        if (columnIndex == 0) {
            replace = new GameAccount((String) aValue, current.getPassword(), current.getPin());
        } else if (columnIndex == 1) {
            replace = new GameAccount(current.getUsername(), (String) aValue, current.getPin());
        } else {
            int pin = safeConvertPin((String) aValue, current);
            replace = new GameAccount(current.getUsername(), current.getPassword(), pin);
        }

        accounts.set(rowIndex, replace);
        accounts.serialize();
    }

    private int safeConvertPin(String pin, GameAccount old) {
        if (pin.length() != 4) {
            return old.getPin();
        }

        try {
            return Integer.parseInt(pin);
        } catch (NumberFormatException exc) {
            return old.getPin();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        GameAccount account = accounts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return account.getUsername();

            case 1:
                return account.getPassword().replaceAll("(?s).", "*");

            default:
                return account.getPin() == -1 ? "" : account.getPin();
        }
    }
}

package org.rspeer.ui.account;

import org.rspeer.Inuvation;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.game.api.Game;
import org.rspeer.ui.SwingResources;
import org.rspeer.ui.commons.IconButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountManager extends JPanel {

    private AccountTableModel tableModel;
    private AccountTable accountTable;

    public AccountManager() {
        setPreferredSize(new Dimension(500, 420));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        ImageIcon[] plusIcons = SwingResources.loadButtonImages(Inuvation.class.getResourceAsStream("/org/rspeer/ui/account/green-plus.png"), 17, 17, .6f);
        IconButton plus = new IconButton(plusIcons[0]);
        plus.setToolTipText("Add empty row");
        plus.setRolloverIcon(plusIcons[1]);
        plus.addActionListener(act -> {
            if (tableModel == null) {
                return;
            }

            tableModel.addEmptyRow();
        });

        ImageIcon[] minusIcons = SwingResources.loadButtonImages(Inuvation.class.getResourceAsStream("/org/rspeer/ui/account/red-minus.png"), 17, 17, .6f);
        IconButton minus = new IconButton(minusIcons[0]);
        minus.setToolTipText("Remove row");
        minus.setRolloverIcon(minusIcons[1]);
        minus.addActionListener(act -> {
            if (tableModel == null) {
                return;
            }

            int selected = accountTable.getSelectedRow();
            if (selected == -1) {
                return;
            }

            tableModel.removeRow(selected);
        });

        ImageIcon[] clipboardIcons = SwingResources.loadButtonImages(Inuvation.class.getResourceAsStream("/org/rspeer/ui/account/clipboard.png"), 17, 17, .6f);
        IconButton clipboard = new IconButton(clipboardIcons[0]);
        clipboard.setToolTipText("Add current logged in account");
        clipboard.setRolloverIcon(clipboardIcons[1]);
        clipboard.addActionListener(act -> {
            if (!Game.isLoggedIn()) {
                return;
            }

            String username = Game.getClient().getUsername();
            String password = Game.getClient().getPassword();
            tableModel.addRow(new GameAccount(username, password));
        });

        buttons.add(clipboard);
        buttons.add(plus);
        buttons.add(minus);

        buttons.setAlignmentX(LEFT_ALIGNMENT);
        buttons.setBorder(new EmptyBorder(0, 0, 3, 0));
        add(buttons);

        JScrollPane actTable = buildAccountTable();
        actTable.setAlignmentX(LEFT_ALIGNMENT);
        add(actTable);

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 5, 5, 5),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createRaisedSoftBevelBorder(),
                                BorderFactory.createEmptyBorder(2, 2, 2, 2)
                        )
                )
        );
    }

    public void saveAllAccounts() {
        tableModel.saveAllAccounts();
    }

    private JScrollPane buildAccountTable() {
        tableModel = new AccountTableModel();
        accountTable = new AccountTable(tableModel);
        JScrollPane pane = accountTable.getWrappedTable();
        pane.setBorder(BorderFactory.createEmptyBorder());
        return pane;
    }
}

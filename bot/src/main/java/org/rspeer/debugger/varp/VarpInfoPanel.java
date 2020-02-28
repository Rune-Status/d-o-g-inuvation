package org.rspeer.debugger.varp;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * @author Yasper
 * <p>
 * The info panel that shows all info on a varp change. This info panel also shows varp lookup changes by hiding
 * some data that should not be present for a simple lookup.
 */
public final class VarpInfoPanel extends JPanel {

    private static final String[] HEADER = {
            "31", "30", "29", "28", "27", "26", "25", "24", "23", "22", "21", "20",
            "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8",
            "7", "6", "5", "4", "3", "2", "1", "0"
    };

    public VarpInfoPanel(VarpChange change) {
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setPreferredSize(new Dimension(600, 160));

        boolean lookup = change instanceof VarpLookup;
        String oldBinaryString = Integer.toBinaryString(change.getOldValue());
        String newBinaryString = Integer.toBinaryString(change.getNewValue());
        String[][] rows = createBinaryRows(lookup, oldBinaryString, newBinaryString);

        DefaultTableModel model = new DefaultTableModel(rows, HEADER);
        JTable bitsTable = new JTable(model);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);
        bitsTable.setDefaultRenderer(Object.class, centerRenderer);

        JLabel indexLabel = new JLabel("Varp: " + change.getIndex());
        indexLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        indexLabel.setAlignmentX(LEFT_ALIGNMENT);
        this.add(indexLabel);

        JLabel oldValueLabel = new JLabel();
        if (lookup) {
            oldValueLabel.setText("Value: " + change.getOldValue());
        } else {
            oldValueLabel.setText("Old value: " + change.getOldValue());
        }
        oldValueLabel.setAlignmentX(LEFT_ALIGNMENT);
        this.add(oldValueLabel);

        int diff = change.getNewValue() - change.getOldValue();
        JLabel newValueLabel = new JLabel("New value: " + change.getNewValue() + " (" + (diff >= 0 ? "+" : "") + diff + ")");
        newValueLabel.setAlignmentX(LEFT_ALIGNMENT);
        newValueLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        if (!lookup) {
            this.add(newValueLabel);
        }

        JLabel tableLabel = new JLabel("Bit table:");
        tableLabel.setAlignmentX(LEFT_ALIGNMENT);
        tableLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        this.add(tableLabel);

        JScrollPane bitsTableContainer = new JScrollPane(bitsTable);
        bitsTableContainer.setAlignmentX(LEFT_ALIGNMENT);
        bitsTableContainer.setBorder(new EmptyBorder(1, 1, 10, 1));
        this.add(bitsTableContainer);
        createBitChangesPanel(change);
    }

    private String[][] createBinaryRows(boolean lookup, String oldBinaryString, String newBinaryString) {
        String[][] rows = lookup ? new String[1][32] : new String[2][32];
        for (int i = 0; i < 32; i++) {
            char oldChar = i + 1 > oldBinaryString.length() ? '0' : oldBinaryString.charAt(oldBinaryString.length() - 1 - i);
            char newChar = i + 1 > newBinaryString.length() ? '0' : newBinaryString.charAt(newBinaryString.length() - 1 - i);

            StringBuilder html = new StringBuilder("<html>");
            if (oldChar != newChar) {
                html.append("<b><font color='orange'>");
            }

            html.append("%c");

            if (oldChar != newChar) {
                html.append("</font></b>");
            }
            html.append("</div></html>");

            String built = html.toString();
            if (!lookup) {
                rows[1][31 - i] = String.format(built, newChar);
                rows[0][31 - i] = String.format(built, oldChar);
            } else {
                rows[0][31 - i] = String.format(built, newChar);
            }
        }
        return rows;
    }

    private void createBitChangesPanel(VarpChange change) {
        if (change.getChanges() != null && change.getChanges().length > 0) {
            JLabel varpbitChangesLabel = new JLabel(String.format("<html><b>Varpbit changes (%d):</b></html>", change.getChanges().length));
            varpbitChangesLabel.setAlignmentX(LEFT_ALIGNMENT);
            varpbitChangesLabel.setBorder(new EmptyBorder(0, 5, 5, 0));
            this.add(varpbitChangesLabel);

            JPanel varpbitChangesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            varpbitChangesPanel.setBorder(new EmptyBorder(0, 10, 0, 20));
            for (VarpbitChange vbChange : change.getChanges()) {
                VarpbitInfoPanel vbInfo = new VarpbitInfoPanel(vbChange);
                varpbitChangesPanel.add(vbInfo);
            }

            varpbitChangesPanel.setAlignmentX(LEFT_ALIGNMENT);
            this.add(varpbitChangesPanel);
        }
    }
}

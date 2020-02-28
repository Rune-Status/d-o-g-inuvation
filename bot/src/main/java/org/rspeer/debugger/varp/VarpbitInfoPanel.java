package org.rspeer.debugger.varp;

import org.rspeer.game.providers.RSVarpBit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @author Yasper
 * <p>
 * The small info panels on the varp info panel that display potential varpbit changes.
 */
public final class VarpbitInfoPanel extends JPanel {

    public VarpbitInfoPanel(VarpbitChange change) {
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        RSVarpBit changed = change.getBit();

        JLabel idLabel = new JLabel(String.format("Varpbit %d", changed.getId()));
        idLabel.setBorder(new EmptyBorder(2, 0, 4, 0));
        idLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(idLabel);

        JLabel prevLabel = new JLabel(String.format("%d -> %d", change.getPrevious(), change.getCurrent()));
        prevLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(prevLabel);
    }
}

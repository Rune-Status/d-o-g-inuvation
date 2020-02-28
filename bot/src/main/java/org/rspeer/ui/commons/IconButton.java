package org.rspeer.ui.commons;

import javax.swing.*;

public final class IconButton extends JButton {

    public IconButton(ImageIcon icon) {
        super(icon);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder());
        setRolloverEnabled(true);
    }
}

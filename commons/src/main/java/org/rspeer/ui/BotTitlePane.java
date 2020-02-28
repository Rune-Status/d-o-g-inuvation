package org.rspeer.ui;

import org.rspeer.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public final class BotTitlePane extends JMenuBar {

    private BotTitlePane(Window owner) {
        SwingResources.setStrictSize(this, getWidth(), 30);

        add(Box.createHorizontalStrut(5));
        JLabel title = new JLabel(getTitle(owner) + " ");
        title.setFont(SwingResources.OPEN_SANS_BOLD);
        add(title);

        if (getTitle(owner).equals(Configuration.APPLICATION_NAME)) {
            JLabel icon = new JLabel(SwingResources.LIGHTBULB);
            icon.setFont(SwingResources.getFontAwesome(14));
            add(icon);
        }

        JButton close = new JButton(SwingResources.CLOSE_ICON);
        close.setFont(SwingResources.getFontAwesome(16));
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setBorder(null);
        close.setFocusPainted(false);
        close.setMargin(new Insets(5, 10, 5, 10));

        close.addActionListener(e -> owner.dispatchEvent(new WindowEvent(owner, WindowEvent.WINDOW_CLOSING)));

        JButton maximise = new JButton(SwingResources.MAXIMISE_ICON);
        maximise.setFont(SwingResources.getFontAwesome(16));
        maximise.setContentAreaFilled(false);
        maximise.setBorderPainted(false);
        maximise.setBorder(null);
        maximise.setFocusPainted(false);
        maximise.setMargin(new Insets(5, 10, 5, 10));

        maximise.addActionListener(e -> {
            if (owner instanceof JFrame) {
                JFrame f = (JFrame) owner;
                if ((f.getExtendedState() & 0x6) != 0) {
                    f.setExtendedState(f.getExtendedState() & 0xfffffff9);
                } else {
                    f.setExtendedState(f.getExtendedState() | 0x6);
                }
            }
        });

        JButton iconify = new JButton(SwingResources.ICONIFY_ICON);
        iconify.setFont(SwingResources.getFontAwesome(16));
        iconify.setContentAreaFilled(false);
        iconify.setBorderPainted(false);
        iconify.setBorder(null);
        iconify.setFocusPainted(false);
        iconify.setMargin(new Insets(5, 10, 5, 10));

        iconify.addActionListener(e -> {
            if (owner instanceof JFrame) {
                JFrame f = (JFrame) owner;
                f.setState(Frame.ICONIFIED);
            }
        });


        addHoverEffect(iconify, maximise, close);
        add(Box.createHorizontalGlue());
        add(iconify);

        if (owner instanceof JFrame && ((JFrame) owner).isResizable()) {
            add(maximise);
        }

        add(close);
    }

    /**
     * Applies the title pane decoration to the given Window
     *
     * @param window The window to decorate
     */
    public static void decorate(Window window) {
        EventQueue.invokeLater(() -> {
            JRootPane root = null;
            if (window instanceof JFrame) {
                JFrame f = (JFrame) window;
                root = f.getRootPane();
            }

            if (window instanceof JDialog) {
                JDialog d = (JDialog) window;
                root = d.getRootPane();
            }

            if (root == null) {
                throw new IllegalStateException("Invalid window");
            }

        });
    }

    private void addHoverEffect(JButton... buttons) {
        for (JButton button : buttons) {
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setForeground(button.getForeground().darker());
                }

                public void mouseExited(MouseEvent e) {
                    button.setForeground(button.getForeground().brighter());
                }
            });
        }
    }

    private String getTitle(Window window) {
        String title = Configuration.APPLICATION_NAME;
        if (window instanceof JFrame) {
            title = ((JFrame) window).getTitle();
        }

        if (window instanceof JDialog) {
            title = ((JDialog) window).getTitle();
        }
        return title;
    }
}

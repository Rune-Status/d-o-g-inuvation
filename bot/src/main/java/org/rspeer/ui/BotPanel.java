package org.rspeer.ui;

import org.rspeer.Configuration;
import org.rspeer.loader.ClientApplet;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

public final class BotPanel extends JPanel {

    private final JFrame parent;
    private Applet applet;
    private String message;
    private Color fontColor = Color.WHITE;

    public void setMessage(String message) {
        this.fontColor = Color.WHITE;
        this.message = message;
        validate();
        repaint();
    }


    public void setError(String message) {
        this.fontColor = SwingResources.ERROR_FONT_COLOR;
        this.message = message;
        validate();
        repaint();
    }

    public String getMessage() {
        return message;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        validate();
        repaint();
    }

    public BotPanel(JFrame parent) {
        this.parent = parent;
        setSize(ClientApplet.DEFAULT_SIZE);
        setMinimumSize(ClientApplet.DEFAULT_SIZE);
        setPreferredSize(ClientApplet.DEFAULT_SIZE);
        setLayout(new BorderLayout());
        setBackground(SwingResources.DEFAULT_BACKGROUND_COLOR);
    }

    public void setApplet(Applet applet) {
        this.applet = applet;
        applet.setLayout(null);
        applet.setSize(ClientApplet.DEFAULT_SIZE);
        EventQueue.invokeLater(() -> {
            add(applet, BorderLayout.CENTER);
            parent.pack();
        });
    }

    private void stopApplet() {
        EventQueue.invokeLater(() -> {
            applet.stop();
            applet.destroy();
        });
    }

    public void removeApplet() {
        EventQueue.invokeLater(() -> {
            stopApplet();
            removeAll();
            revalidate();
            parent.revalidate();
        });
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setColor(SwingResources.DEFAULT_BACKGROUND_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.drawImage(BotWindow.ICON.getImage(), 325, 200, null);
        g2d.setColor(fontColor);
        Font font = SwingResources.OPEN_SANS;
        if(font != null) {
            g2d.setFont(font.deriveFont(24f));
        }
        g2d.drawString("Welcome To " + Configuration.APPLICATION_NAME, 245, 155);
        int x = 275;
        int y = 405;
        for (String line : message.split("\n"))
            g2d.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
}

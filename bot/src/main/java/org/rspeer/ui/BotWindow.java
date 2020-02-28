package org.rspeer.ui;

import org.rspeer.Configuration;
import org.rspeer.Inuvation;
import org.rspeer.OperatingSystem;
import org.rspeer.api.commons.Time;
import org.rspeer.bot.Bot;
import org.rspeer.ui.paint.PaintDialog;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class BotWindow implements BotView {

    public static final ImageIcon ICON = new ImageIcon(Inuvation.class.getResource("/logo.png"));

    private final Bot bot;
    private final JFrame frame;
    private final BotPanel panel;

    private final BotMenuBar menuBar;
    private PaintDialog paintDialog;

    public BotWindow(Bot bot) {
        this.bot = bot;

        frame = new JFrame(Configuration.APPLICATION_NAME);
        frame.setMinimumSize(new Dimension(900, 740));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(menuBar = new BotMenuBar(this), BorderLayout.NORTH);
        frame.add(panel = new BotPanel(frame), BorderLayout.CENTER);
        frame.setIconImage(ICON.getImage());

        if (OperatingSystem.get() == OperatingSystem.WINDOWS) {
            paintDialog = new PaintDialog(this, bot.getEventDispatcher());
            Point location = panel.getLocationOnScreen();
            paintDialog.setLocation(location.x, location.y);
        }

        Adapter adapter = new Adapter();
        frame.addComponentListener(adapter);
        frame.addWindowListener(adapter);
        frame.pack();
    }

    @Override
    public Bot getBot() {
        return bot;
    }

    @Override
    public PaintDialog getPaintDialog() {
        return paintDialog;
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    @Override
    public BotMenuBar getMenuBar() {
        return menuBar;
    }

    @Override
    public BotPanel getPanel() {
        return panel;
    }

    @Override
    public <C extends Component> C add(C component) {
        frame.add(component);
        return component;
    }

    @Override
    public void notifySupplied(Applet applet) {
        applet.init();
        applet.start();
        panel.setApplet(applet);
    }

    public void removeApplet() {
        panel.removeApplet();
    }

    public void revalidate() {
        panel.revalidate();
        frame.revalidate();
    }

    private class Adapter extends ComponentAdapter implements WindowListener {

        @Override
        public void componentResized(ComponentEvent e) {
            if (frame.isVisible() && paintDialog != null) {
                paintDialog.setSize(frame.getSize());
            }
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            if (frame.isVisible() && paintDialog != null) {
                Point vploc = panel.getLocationOnScreen();
                paintDialog.setLocation(vploc.x, vploc.y);
            }
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            if (frame.isVisible() && paintDialog != null) {
                paintDialog.setVisible(false);
                paintDialog.setVisible(true);
            }
        }

        public void windowOpened(WindowEvent e) {

        }

        public void windowClosing(WindowEvent e) {
            Inuvation.shutdown();
            Time.sleep(100);
        }

        public void windowClosed(WindowEvent e) {

        }

        public void windowIconified(WindowEvent e) {

        }

        public void windowActivated(WindowEvent e) {

        }

        public void windowDeactivated(WindowEvent e) {

        }
    }
}

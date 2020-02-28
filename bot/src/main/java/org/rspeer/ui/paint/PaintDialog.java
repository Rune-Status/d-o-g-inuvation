package org.rspeer.ui.paint;

import org.rspeer.api.commons.Functions;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSClient;
import org.rspeer.ui.BotView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public final class PaintDialog extends JDialog implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private final PaintOverlay overlay;

    public PaintDialog(BotView view, EventDispatcher dispatcher) {
        super(view.getFrame());
        overlay = new PaintOverlay(dispatcher, view.getFrame().getSize());
        view.getFrame().setVisible(true);
        update(view.getFrame().getSize());

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    }

    private static Canvas getCanvas() {
        return Functions.mapOrNull(Game::getClient, RSClient::getCanvas);
    }

    private void update(Dimension dimension) {
        setPreferredSize(dimension);
        setSize(dimension);
        setUndecorated(true);
        getRootPane().setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        setFocusableWindowState(true);
        setVisible(true);
        setContentPane(overlay);
        overlay.update(dimension);
    }

    public PaintOverlay getOverlay() {
        return overlay;
    }

    private void dispatch(AWTEvent e) {
        Canvas canvas = getCanvas();
        if (canvas != null) {
            canvas.dispatchEvent(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        dispatch(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        dispatch(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        dispatch(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        dispatch(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dispatch(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dispatch(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        dispatch(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        dispatch(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dispatch(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        dispatch(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        dispatch(e);
    }
}
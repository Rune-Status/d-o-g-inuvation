package org.rspeer.ui.paint;

import org.rspeer.event.Event;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.game.event.types.RenderEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class PaintOverlay extends JComponent implements Runnable {

    private static final int DELAY = 150;

    private final EventDispatcher dispatcher;
    private final Thread thread;

    private BufferedImage buffer;
    private Graphics2D graphics;
    private Dimension dimension;
    private boolean rendering = true;

    public PaintOverlay(EventDispatcher dispatcher, Dimension dimension) {
        update(dimension);
        this.dispatcher = dispatcher;
        (thread = new Thread(this)).start();
    }

    public void update(Dimension dimensions) {
        this.dimension = dimensions;
        buffer = new BufferedImage(dimensions.width, dimensions.height, BufferedImage.TYPE_INT_ARGB);
        graphics = buffer.createGraphics();
        setOpaque(false);
        setIgnoreRepaint(true);
        setPreferredSize(dimensions);
        setSize(dimensions);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (!rendering) {
            return;
        }
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, dimension.width, dimension.height);
        graphics.setComposite(AlphaComposite.SrcOver);

        g.setColor(Color.WHITE);
        Event<Graphics> event = new RenderEvent(g, getClass().getSimpleName());
        dispatcher.immediate(event);

        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void interrupt() {
        thread.interrupt();
    }

    public void start() {
        thread.start();
    }

    public boolean isRendering() {
        return rendering;
    }

    public void setRendering(boolean rendering) {
        this.rendering = rendering;
    }
}
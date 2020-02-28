package org.rspeer.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class SwingResources {

    public static final Font OPEN_SANS = getFont("/OPEN_SANS.ttf", 12f);
    public static final Font OPEN_SANS_BOLD = getFont("/OPEN_SANS_BOLD.ttf", 12f);

    public static final Color ERROR_FONT_COLOR = Color.decode("#ea6759");
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.decode("#252525");

    public static final String CLOSE_ICON = "\uf00d";
    public static final String ICONIFY_ICON = "\uf068";
    public static final String MAXIMISE_ICON = "\uf2d0";

    public static final String PLAY = "\uf04b";
    public static final String PAUSE = "\uf04c";
    public static final String STOP = "\uf04d";
    public static final String ACCOUNT = "\uf007";

    public static final String LIGHTBULB = "\uf0eb";

    public static final String LOW_CPU_DISABLED = "\uf240";
    public static final String LOW_CPU_ENABLED = "\uf243";

    public static final String SETTINGS = "\uf013";

    public static final String MOUSE = "\uf245";
    public static final String KEYBOARD = "\uf11c";

    public static Font getFontAwesome(float size) {
        return getFont("/FONTAWESOME.ttf", size);
    }

    public static Font getFont(String name, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT,
                    SwingResources.class.getResourceAsStream(name)
            ).deriveFont(Font.PLAIN, size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setStrictSize(Component c, int w, int h) {
        Dimension d = new Dimension(w, h);
        c.setMaximumSize(d);
        c.setMinimumSize(d);
        c.setSize(d);
        c.setPreferredSize(d);
    }

    public static Image scale(Image image, double scale) {
        int width = (int) (image.getWidth(null) * scale);
        int height = (int) (image.getHeight(null) * scale);
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();

        return resizedImg;
    }

    public static ImageIcon[] loadButtonImages(InputStream resource, int width, int height, float opacity) {
        try {
            BufferedImage image = ImageIO.read(resource);
            Image scaled = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            ImageIcon icon = new ImageIcon(scaled);

            BufferedImage opaque = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = opaque.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            graphics.drawImage(scaled, 0, 0, null);
            graphics.dispose();
            return new ImageIcon[]{icon, new ImageIcon(opaque)};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ImageIcon[0];
    }
}

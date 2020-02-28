package org.rspeer.ui;

import org.rspeer.bot.Bot;
import org.rspeer.ui.paint.PaintDialog;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public interface BotView extends AppletListener {

    Bot getBot();

    PaintDialog getPaintDialog();

    JFrame getFrame();

    void display();

    BotMenuBar getMenuBar();

    BotPanel getPanel();

    <C extends Component> C add(C component);

    default <C extends Component> C add(C component, Consumer<C> after) {
        add(component);
        after.accept(component);
        return component;
    }
}

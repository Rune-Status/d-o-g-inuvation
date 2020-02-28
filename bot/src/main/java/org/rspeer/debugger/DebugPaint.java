package org.rspeer.debugger;

import org.rspeer.api.awt.AWTUtil;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Mobile;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.component.Bank;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.component.tab.Backpack;
import org.rspeer.game.api.component.tab.Equipment;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.scene.Players;
import org.rspeer.game.event.listener.EngineTickListener;
import org.rspeer.game.event.listener.EntityHoverListener;
import org.rspeer.game.event.listener.RenderListener;
import org.rspeer.game.event.types.EngineTickEvent;
import org.rspeer.game.event.types.EntityHoverEvent;
import org.rspeer.game.event.types.RenderEvent;
import org.rspeer.game.providers.*;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DebugPaint implements RenderListener, EntityHoverListener, EngineTickListener {

    private static final Font DEFAULT = Font.getFont(Font.DIALOG);

    private static final Color NPC = Color.CYAN;
    private static final Color PLAYER = Color.GREEN;
    private static final Color OBJECT = Color.WHITE;
    private static final Color PICKABLE = Color.RED;

    private final List<RSSceneNode> hovered = new CopyOnWriteArrayList<>();
    private Point point = new Point(0, 0);

    @Override
    public void notify(RenderEvent e) {
        Graphics g = e.getSource();

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        ((Graphics2D) g).setRenderingHints(rh);

        if (DEFAULT != null) {
            g.setFont(DEFAULT);
        }

        int debugY = 20;
        AWTUtil.drawBoldedString(g, "Logged in: " + Game.isLoggedIn(), 20, debugY += 20);
        AWTUtil.drawBoldedString(g, "State: " + Game.getState(), 20, debugY += 20);

        Player local = Players.getLocal();
        if (local == null) {
            return;
        }

        AWTUtil.drawBoldedString(g, "Location: " + local.getPosition(), 20, debugY += 20);
        AWTUtil.drawBoldedString(g, "Scene Base: " + Position.base(), 20, debugY += 20);
        AWTUtil.drawBoldedString(g, "Animation: " + local.getAnimation(), 20, debugY += 20);
        AWTUtil.drawBoldedString(g, "Animation frame: " + local.getAnimationFrame(), 20, debugY += 20);

        for (RSMobileSpotAnimation anim : local.getGraphics()) {
            if (anim.isActive()) {
                AWTUtil.drawBoldedString(g, "Graphic: " + anim.getEffect(), 20, debugY += 20);
                AWTUtil.drawBoldedString(g, "Graphic frame: " + anim.getEffectFrame(), 20, debugY += 20);
            }
        }

        AWTUtil.drawBoldedString(g, "Stance: " + local.getStance(), 20, debugY += 20);
        AWTUtil.drawBoldedString(g, "Target: " + local.getTarget() + " (" + local.getTargetIndex() + ")", 20, debugY += 20);

        int cutscene = Game.getCutsceneId();
        if (cutscene != -1) {
            AWTUtil.drawBoldedString(g, "Cutscene: " + cutscene + " (" + Game.getCutsceneState() + ")", 20, debugY += 20);
        }

        if (Interfaces.isVisible(InterfaceComposite.BACKPACK.getGroup(), 0)) {
            drawItems(g, Backpack.getItems());
        }

        if (Interfaces.isVisible(InterfaceComposite.BANK.getGroup(), 0)) {
            drawItems(g, Bank.getItems());
        }

        if (Interfaces.isVisible(InterfaceComposite.EQUIPMENT.getGroup(), 0)) {
            drawItems(g, Equipment.getItems());
        }

        int baseMouseY = point.y;
        for (int i = 0; i < Math.min(5, hovered.size()); i++) {
            RSSceneNode node = hovered.get(i);
            if (node instanceof RSMobile) {
                Mobile mob = ((RSMobile) node).getAdapter();
                g.setColor(mob instanceof Npc ? NPC : PLAYER);
                AWTUtil.drawBoldedString(g, mob.getId() + " Anim: " + mob.getAnimation() + " [" + mob.getAnimationFrame() + "]", point.x, baseMouseY -= 15);
            } else if (node instanceof RSSceneObject) {
                g.setColor(node instanceof RSGroundEntity ? PICKABLE : OBJECT);
                SceneObject obj = ((RSSceneObject) node).getAdapter();
                StringBuilder debug = new StringBuilder();
                debug.append(obj.getId());
                if (obj.getProvider() instanceof RSStaticGameObject) {
                    RSStaticGameObject spec = (RSStaticGameObject) obj.getProvider();
                    debug.append(" Anim: ").append(spec.getAnimation())
                            .append(" [").append(spec.getAnimationFrame()).append("]");
                }
                AWTUtil.drawBoldedString(g, debug.toString(), point.x, baseMouseY -= 15);
            }
        }
    }

    private void drawItems(Graphics g, Collection<Item> items) {
        for (Item item : items) {
            InterfaceComponent component = item.getComponent();
            if (component == null) {
                continue;
            }
            Rectangle bounds = component.getBounds();
            AWTUtil.drawBoldedString(g, String.valueOf(item.getId()), bounds.x, bounds.y);
        }
    }

    @Override
    public void notify(EntityHoverEvent e) {
        if (hovered.contains(e.getSource())) {
            return;
        }
        hovered.add(e.getSource());
        point = e.getLocation();
    }

    @Override
    public void notify(EngineTickEvent e) {
        hovered.clear();
    }
}

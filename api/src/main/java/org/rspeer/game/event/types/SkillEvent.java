package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.api.component.tab.Skill;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.SkillListener;

public final class SkillEvent extends Event<Skill> {

    private final Type type;
    private final int previous, current;

    public SkillEvent(Skill skill, Type type, int previous, int current) {
        super(skill, "Static");
        this.type = type;
        this.previous = previous;
        this.current = current;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof SkillListener) {
            ((SkillListener) listener).notify(this);
        }
    }

    public Type getType() {
        return type;
    }

    public int getPrevious() {
        return previous;
    }

    public int getCurrent() {
        return current;
    }

    public int getChange() {
        return current - previous;
    }

    public enum Type {
        EXPERIENCE,
        LEVEL,
        TEMPORARY_LEVEL
    }
}

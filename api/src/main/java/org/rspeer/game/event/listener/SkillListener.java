package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.SkillEvent;

public interface SkillListener extends EventListener {
    void notify(SkillEvent e);
}

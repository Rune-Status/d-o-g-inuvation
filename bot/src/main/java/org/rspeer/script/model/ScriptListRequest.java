package org.rspeer.script.model;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public class ScriptListRequest {

    @DoNotRename
    private ScriptQueryType type;

    @DoNotRename
    // 0 = OSRS, 1 = RS3
    private int game;

    @DoNotRename
    public ScriptQueryType getType() {
        return type;
    }

    @DoNotRename
    public int getGame() {
        return game;
    }

    @DoNotRename
    public void setGame(int game) {
        this.game = game;
    }

    @DoNotRename
    public void setType(ScriptQueryType type) {
        this.type = type;
    }
}

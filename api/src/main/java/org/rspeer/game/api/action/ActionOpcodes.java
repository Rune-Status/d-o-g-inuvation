package org.rspeer.game.api.action;

/**
 * action formats
 * npc = (opcode, arrayindex, 0, 0)
 * player = (opcode, arrayindex, 0, 0)
 * object = (opcode, uid, regionx, regiony)
 * walk = (opcode, type, regionx, regiony) //type = 0 for viewport, 1 for map
 * widget = (opcode, actionIndex, widgetIndex, uid)
 * - actionIndex is the index of the action in the menu + 1. so for eg the first action for item would be 1, second 2 etc
 */
public interface ActionOpcodes {

    int USE_ON_OBJ = 2; // use (Object)
    int OP_OBJ1 = 3;
    int OP_OBJ2 = 4;
    int OP_OBJ3 = 5;
    int OP_OBJ4 = 6;
    int USE_ON_NPC = 8; // use (Npc)
    int OP_NPC1 = 9;
    int OP_NPC2 = 10;
    int OP_NPC3 = 11;
    int OP_NPC4 = 12; // Attack
    int OP_NPC5 = 13;
    int USE_ON_PLAYER = 15;
    int USE_ON_SELF = 16;
    int USE_ON_PICKABLE = 17;
    int OP_PICKABLE1 = 18; //ground item
    int OP_PICKABLE2 = 19;
    int OP_PICKABLE3 = 20;
    int OP_PICKABLE4 = 21;
    int OP_PICKABLE5 = 22;
    int WALK = 23;
    int USE_ON_BUTTON = 25;
    int OP_BUTTON = 30; //button = games necklace teleport option etc
    int OP_PLAYER1 = 44;
    int OP_PLAYER2 = 45;
    int OP_PLAYER3 = 46;
    int OP_PLAYER4 = 47;
    int OP_PLAYER5 = 48;
    int OP_PLAYER6 = 49;
    int OP_PLAYER7 = 50;
    int OP_PLAYER8 = 51;
    int OP_PLAYER9 = 52;
    int OP_PLAYER10 = 53;
    int OP_COMPONENT1 = 57;
    int USE_ON_COMPONENT = 58; //for using an item on an interface
    int USE_ON_GROUND = 59;
    int FACE_SQUARE = 60;
    int OP_OBJ5 = 1001;
    int EXAMINE_OBJ = 1002; // examine (Object)
    int EXAMINE_NPC = 1003; // examine (Npc)
    int EXAMINE_PICKABLE = 1004; // examine (Ground Object)
    int CANCEL = 1006;
    int OP_COMPONENT2 = 1007; //examine/drop inv item - for examine, primary arg is 10. for drop, primary arg is 8
    int OP_MAPELEMENT1 = 1008;
    int OP_MAPELEMENT2 = 1009;
    int OP_MAPELEMENT3 = 1010;
    int OP_MAPELEMENT4 = 1011;
    int OP_MAPELEMENT5 = 1012;

    int OP_DUEL_PLAYER = 1172; // <-- this one isn't listed in NXT Dump
}
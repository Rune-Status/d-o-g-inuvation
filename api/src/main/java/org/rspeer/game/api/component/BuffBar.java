package org.rspeer.game.api.component;

import org.rspeer.game.adapter.component.InterfaceComponent;

public enum BuffBar {

    WEAPON_POISON(14694),
    SCRIMSHAW(26097);

    private final int materialId;
    private final InterfaceAddress address;

    BuffBar(int materialId) {
        this.materialId = materialId;
        this.address = new InterfaceAddress(() -> Interfaces.getFirst(InterfaceComposite.BUFF_BAR.getGroup(),
                a -> a.getMaterialId() == materialId, true));
    }

    public int getMaterialId() {
        return materialId;
    }

    public InterfaceAddress getAddress() {
        return address;
    }

    public boolean isActive() {
        return address.mapToBoolean(InterfaceComponent::isVisible);
    }
}

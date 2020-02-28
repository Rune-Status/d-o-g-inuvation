package org.rspeer.game.providers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public interface RSEnumDefinition extends RSDefinition {

    Object[] getElements();

    Map<Integer, Serializable> getParameters();

    HashMap<Object, int[]> getKeys();

    String getDefaultString();

    int getDefaultValue();

    int getSize();
}

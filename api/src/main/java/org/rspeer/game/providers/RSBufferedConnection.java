package org.rspeer.game.providers;

import java.lang.reflect.Method;

public interface RSBufferedConnection extends RSProvider {

    int getIdleTicks();

    RSBuffer getOutgoing();

    RSConnection getConnection();

    RSNodeDeque getFrameDeque();

    default void close() {
        RSConnection con = getConnection();
        if (con != null) {
            try {
                Method m = con.getClass().getDeclaredMethod("finalize");
                m.setAccessible(true);
                m.invoke(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
package org.rspeer.game.providers;

import org.rspeer.game.event.interceptor.impl.Proxy;

@Proxy("jagdx/IDirect3D")
public interface RSDirect3D extends RSProvider {
    long Direct3DCreate();
}

package org.rspeer.loader.task;

import org.rspeer.Configuration;
import org.rspeer.api.collections.Pair;
import org.rspeer.api.concurrent.Tasks;
import org.rspeer.bot.Bot;
import org.rspeer.bot.BotTask;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.adapter.cache.NpcDefinition;
import org.rspeer.game.adapter.component.Interface;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.*;
import org.rspeer.game.adapter.world.World;
import org.rspeer.game.event.callback.ClientCallbackHandler;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.providers.*;
import org.rspeer.injector.Injector;
import org.rspeer.io.Archive;
import org.rspeer.io.InnerPack;
import org.rspeer.loader.GameConfiguration;
import org.rspeer.loader.adapt.*;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class InjectTask extends BotTask {

    private final byte[] modscript;
    private final InnerPack innerPack;
    private final List<EventListener> listeners;

    public InjectTask(Bot bot, byte[] modscript, InnerPack innerPack, List<EventListener> listeners) {
        super(bot);
        this.modscript = modscript;
        this.innerPack = innerPack;
        this.listeners = listeners;
    }

    public InjectTask(Bot bot, byte[] modscript, InnerPack innerPack) {
        this(bot, modscript, innerPack, Collections.emptyList());
    }

    @Override
    public void run() {
        Map<String, byte[]> buffer;
        GameConfiguration config = bot.getGameConfiguration();
        try (Injector injector = Injector.decrypting(innerPack, config, this.modscript)) {
            //TODO run without noverify - need to fix: AddWrapperAdapter, processSimpleCallbacks,
            //HideInterfaceAdapter, EngineTickCallback, FieldChangeCallback,
            //AddInvokerAdapter, RandomDatAdapter,
            injector.accept(new AddWrapperAdapter(injector.getModscript(),
                    new Pair<>(Player.class, RSPlayer.class),
                    new Pair<>(Npc.class, RSNpc.class),
                    new Pair<>(SceneObject.class, RSBoundary.class),
                    new Pair<>(SceneObject.class, RSBoundaryDecor.class),
                    new Pair<>(SceneObject.class, RSGroundEntity.class),
                    new Pair<>(SceneObject.class, RSSceneEntity.class),
                    new Pair<>(SceneObject.class, RSTileDecor.class),
                    new Pair<>(Interface.class, RSInterface.class),
                    new Pair<>(InterfaceComponent.class, RSInterfaceComponent.class),
                    new Pair<>(ItemDefinition.class, RSItemDefinition.class),
                    new Pair<>(Projectile.class, RSProjectile.class),
                    new Pair<>(NpcDefinition.class, RSNpcDefinition.class),
                    new Pair<>(World.class, RSWorld.class),
                    new Pair<>(GuidanceArrow.class, RSGuidanceArrow.class),
                    new Pair<>(GroundItem.class, RSGroundItem.class))
            );

            injector.accept(new AddInterfaceAdapter(injector.getModscript()));
            injector.accept(new AddGetterAdapter(injector.getModscript()));

            processFieldInsertions(injector);
            processSetters(injector);
            processSimpleCallbacks(injector);

            injector.accept(new ExceptionSuppressorAdapter(injector.getModscript()));
            injector.accept(new AnimatorParentAdapter(injector.getModscript()));
            injector.accept(new CombatGaugeParentAdapter(injector.getModscript()));
            //injector.accept(new GEIndexAdapter(injector.getModscript()));
            injector.accept(new GEOfferCallback(injector.getModscript()));
            //injector.accept(new HideInterfaceAdapter(injector.getModscript()));
            injector.accept(new ObjectImpassableAdapter(injector.getModscript()));
            injector.accept(new InterfacePositionAdapter(injector.getModscript()));
            injector.accept(new EngineTickCallback(injector.getModscript()));
            injector.accept(new EntityHoverCallback(injector.getModscript()));
            injector.accept(new MenuActionConstructor(injector.getModscript()));
            injector.accept(new ScriptExecutionConstructor(injector.getModscript()));

            injector.accept(new FieldChangeCallback(injector.getModscript()));
            injector.accept(new AddInvokerAdapter(injector.getModscript()));
            injector.accept(new RandomDatAdapter(injector.getModscript()));
            injector.accept(new AddConstantAdapter(injector.getModscript()));

            injector.accept(new JavaFontAdapter(injector.getModscript()));

            processInterceptions(injector);//needs interfaces to be added first

            buffer = injector.getBuffer();

            Archive.write(buffer, new File(Configuration.CACHE + "injected.jar"));
        } catch (NoSuchFileException e) {
            throw new RuntimeException("Modscript unavailable, rerun updater", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject or decrypt inner.pack.gz", e);
        }

        Tasks.execute(new AppletTask(bot, buffer, listeners));
    }

    private void processInterceptions(Injector injector) {
        //injector.accept(new InterceptorAdapter(injector.getModscript(), OpenGLInterceptor.class));
        //injector.accept(new InterceptorAdapter(injector.getModscript(), DirectXInterceptor.class));
    }

    private void processFieldInsertions(Injector injector) {
        injector.accept(new AddDataFieldAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class),
                "eventDispatcher",
                "L" + EventDispatcher.class.getName().replace('.', '/') + ";")
        );

        injector.accept(new AddDataFieldAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class),
                "callbackHandler",
                "L" + ClientCallbackHandler.class.getName().replace('.', '/') + ";")
        );

        injector.accept(new AddDataFieldAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSGrandExchangeOffer.class),
                "index",
                "I")
        );

        injector.accept(new AddDataFieldAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSAnimator.class),
                "owner",
                "L" + RSMobile.class.getName().replace('.', '/') + ";")
        );

        injector.accept(new AddDataFieldAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSCombatGauge.class),
                "owner",
                "L" + RSMobile.class.getName().replace('.', '/') + ";")
        );

        injector.accept(new AddDataFieldAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSGlobalPlayer.class),
                "baseX", "I")
        );

        injector.accept(new AddDataFieldAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSGlobalPlayer.class),
                "baseY", "I")
        );
    }

    private void processSetters(Injector injector) {
        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSInterfaceComponent.class).getField("contentType"))
        );

        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSDecodingException.class).getField("message"))
        );

        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSInterfaceComponent.class).getField("itemId"))
        );

        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSInterfaceComponent.class).getField("itemQuantity"))
        );

        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSInterfaceComponent.class).getField("text"))
        );

        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSScriptContext.class).getField("args"))
        );

        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSDynamicGameObject.class).getField("orientation"))
        );

        injector.accept(new AddSetterAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSInterfaceComponent.class).getField("renderCycle"))
        );
    }

    private void processSimpleCallbacks(Injector injector) {
        injector.accept(new AddCallbackAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class).getMethod("processAction"), true)
        );

        injector.accept(new AddCallbackAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class).getMethod("messageReceived"))
        );

        injector.accept(new AddCallbackAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class).getMethod("itemTableUpdated"))
        );

        injector.accept(new AddCallbackAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSNodeDeque.class).getMethod("add"))
        );

        injector.accept(new AddCallbackAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class).getMethod("scriptInvoked"))
        );

        injector.accept(new AddCallbackAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class).getMethod("processConsoleCommand"), true)
        );

        injector.accept(new AddCallbackAdapter(injector.getModscript(),
                injector.getModscript().resolve(RSClient.class).getMethod("onRenderTick"))
        );
    }

    @Override
    public String verbose() {
        return "Injecting...";
    }
}

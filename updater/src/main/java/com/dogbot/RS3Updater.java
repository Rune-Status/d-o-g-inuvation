/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.impl.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.graph.FlowGraph;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;
import org.rspeer.Configuration;
import org.rspeer.io.Archive;
import org.rspeer.io.ByteArrayInOutStream;
import org.rspeer.io.InnerPack;
import org.rspeer.loader.Crawler;
import org.rspeer.loader.GameConfiguration;
import org.rspeer.loader.GameEnvironment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarInputStream;

public class RS3Updater extends Updater {

    private String hash;

    public RS3Updater(JarInputStream stream, String hash) {
        super(stream, createVisitors(), false);
        this.hash = hash;
    }

    private static GraphVisitor[] createVisitors() {
        return new GraphVisitor[]{
                new Node(), new DoublyNode(), new NodeTable(), new DoublyNodeQueue(), new DoublyNodeQueueIterator(),
                new StatusNode(), new StatusList(), new NodeDeque(), new GroundItemDeque(), new NodeDequeIterator(),
                new Cache(), new SoftCacheReference(), new CacheReference(), new HardCacheReference(), new ReferenceTable(),
                new NodeTableIterator(), new FileOnDisk(), new Buffer(), new Canvas(), new Vector3f(), new Quaternion(),
                new Matrix4x3(), new Matrix4f(), new CoordinateSpace(), new Camera(), new CameraImpl(), new ItemTable(),
                new FrameBuffer(), new IsaacCipher(), new LatencyMonitor(), new AsyncInputStream(), new AsyncOutputStream(),
                new AsyncConnection(), new Connection(), new BufferedConnection(), new IncomingFrameMeta(), new OutgoingFrame(),
                new OutgoingFrameMeta(), new Js5ConfigGroup(), new ShortNode(), new com.dogbot.visitor.impl.Archive(),
                new LookupTable(), new ResourceProvider(), new ArchiveResourceProvider(), new NetResourceWorker(),
                new IntegerNode(), new LongNode(), new CombatGauge(), new CombatBar(), new DefinitionCacheLoader(),
                new Locatable(), new SceneNode(), new SceneEntity(), new IterableSceneEntity(), new Particle(),
                new ParticleProvider(), new ParticleConfiguration(), new GraphicsCard(), new RenderConfiguration(),
                new PureJavaRenderConfiguration(), new OpenGLRenderConfiguration(), new DirectXRenderConfiguration(),
                new Direct3DRenderConfiguration(), new OpenGLXRenderConfiguration(), new Model(), new PureJavaModel(),
                new OpenGLModel(), new DirectXModel(), new SceneGraphTile(), new TestHook(), new TestHookContainer(),
                new SceneGraph(), new SceneOffset(), new Scene(), new SceneSettings(),
                new FontSpecification(), new Font(), new SceneGraphLevel(),
                new PureJavaSceneGraphLevel(), new DirectXSceneGraphLevel(), new OpenGLSceneGraphLevel(), new Server(),
                new World(), new Animation(), new Animator(), new Definition(), new DefinitionLoader(), new Varps(),
                new Varp(), new VarpBit(), new ExpTable(), new SkillLevel(), new Skill(), new PlayerFacade(),
                new QuestDefinition(), new QuestDefinitionLoader(), new NpcDefinition(), new ObjectDefinition(),
                new ItemDefinition(), new FriendsChatMember(), new ChatMessageIcon(), new ChatMessage(),
                new GuidanceArrow(), new ObjectConfig(), new Projectile(), new ProjectileNode(), new DynamicGameObject(),
                new SceneObject(), new PlayerAppearance(), new Player(), new Rotation(), new ExpandableMenuItem(),
                new MenuItem(), new MobileSpotAnimation(), new Mobile(), new Npc(), new OverheadMessage(),
                new ObjectNode(), new GroundEntity(), new ItemPile(), new GroundItem(), new StaticGameObject(),
                new Boundary(), new TileDecor(), new BoundaryDecor(), new AnimableObject(), new AnimableObjectNode(),
                new ScriptInstruction(), new Script(), new ScriptExecutionContext(), new InterfaceComponent(),
                new Interface(), new InterfaceNode(), new GrandExchangeOffer(), new ScriptContext(), new Loader(),
                new ItemTableDefinition(), new ItemTableDefinitionLoader(), new Client(), new Rasterizer(),
                new MouseListener(), new MobileAnimator(), new SceneFormat(), new CombatGaugeDefinition(),
                new InterfaceComponentDefinition(), new ParameterDefinition(), new DecodingException(),
                new GlobalPlayer(), new EnumDefinition(), new EnumDefinitionLoader(), new ClanChat()
        };
    }

    public static RS3Updater create(String proxy, boolean newAuth) throws Exception {
        GameConfiguration configuration = new GameEnvironment(proxy, newAuth);
        configuration.load();
        Crawler crawler = new Crawler(configuration);
        ByteArrayInOutStream byteArrayInOutStream = new ByteArrayInOutStream();

        String hash = "";
        try (InnerPack innerPack = InnerPack.open(configuration)) {
            Archive.write(innerPack.decrypt(), byteArrayInOutStream);
            hash = innerPack.sha1();
        }
        JarInputStream stream = new JarInputStream(byteArrayInOutStream.getInputStream());
        return new RS3Updater(stream, hash);
    }

    public static RS3Updater create(String proxy) throws Exception {
        return create(proxy, false);
    }

    public static void run(JarInputStream stream, String proxy) throws Exception {
        String hash;
        RS3Updater updater;
        if (stream == null) {
            updater = create(proxy, true);
        } else {
            hash = String.valueOf(stream.getManifest().hashCode());
            updater = new RS3Updater(stream, hash);
        }
        updater.setRemoveUnusedMethods(true);
        System.gc();
        updater.run();
        updater.modscript(true);
        updater.flush();
    }

    private static GraphVisitor firstHookedParent(Updater updater, ClassNode start) {
        if (start == null) {
            return null;
        }
        ClassNode current = updater.classnodes.get(start.superName);
        while (current != null) {
            GraphVisitor next = updater.visitorForClass(current.name);
            if (next != null) {
                return next;
            }
            current = updater.classnodes.get(current.superName);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option
                .builder("proxy")
                .hasArg()
                .required(false)
                .argName("proxy")
                .desc("proxy ip")
                .build());

        CommandLine parser = new DefaultParser().parse(options, args);
        run(null, parser.getOptionValue("proxy"));
    }

    @Override
    public String getType() {
        return "modern";
    }

    @Override
    public String getAccessorPrefix() {
        return "com/dogbot/client/peers/RS";
    }

    @Override
    public String getWrapperPrefix() {
        return "com/dogbot/client/wrappers/";
    }

    @Override
    public int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs) {
        ClassNode client = null;
        for (ClassNode cn : graphs.keySet()) {
            if (!cn.name.equals("Rs2Applet") && !cn.ownerless() && cn.getMethodByName("init") != null) {
                client = cn;
                break;
            }
        }
        if (client == null) {
            throw new RuntimeException("Cannot find client");
        }
        MethodNode init = client.getMethodByName("init");
        FlowGraph graph = graphs.get(client).get(init);
        AtomicInteger revision = new AtomicInteger(0);
        for (Block block : graph) {
            block.tree().accept(new NodeVisitor() {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEVIRTUAL && mmn.desc().contains("Ljava/lang/String;IIII")) {
                        NumberNode nn = (NumberNode) mmn.first(SIPUSH);
                        if (nn != null) {
                            revision.set(nn.number());
                        }
                    }
                }
            });
        }
        return revision.get();
    }

    @Override
    public String getModscriptLocation() {
        return Configuration.CACHE + "/" + hash + ".dat";
    }

    @Override
    public String getHash() {
        return this.hash;
    }
}


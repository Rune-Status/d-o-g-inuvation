package org.rspeer.debugger;

import org.rspeer.api.awt.AWTUtil;
import org.rspeer.api.collections.Pair;
import org.rspeer.game.adapter.component.Interface;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.event.listener.RenderListener;
import org.rspeer.game.event.types.RenderEvent;
import org.rspeer.game.providers.RSIntegerNode;
import org.rspeer.game.providers.RSNode;
import org.rspeer.game.providers.RSNodeTable;
import org.rspeer.game.providers.RSObjectNode;
import org.rspeer.ui.BotTitlePane;
import org.rspeer.ui.SwingResources;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public final class InterfaceExplorer extends JFrame implements RenderListener {

    private static final List<Pair<String, Function<InterfaceComponent, Object>>> INFO = Arrays.asList(
            new Pair<>("Path", InterfaceComponent::toAddress),
            new Pair<>("Bounds", InterfaceComponent::getBounds),
            new Pair<>("Type", m -> InterfaceComponent.Type.get(m.getType())),
            new Pair<>("Content type", InterfaceComponent::getContentType),
            new Pair<>("Material id", InterfaceComponent::getMaterialId),
            new Pair<>("Model id", InterfaceComponent::getModelId),
            new Pair<>("Foreground", InterfaceComponent::getForeground),
            new Pair<>("Animation", x -> x.getAnimation() + " (" + x.getAnimationFrame() + ")"),
            new Pair<>("Name", InterfaceComponent::getName),
            new Pair<>("Text", InterfaceComponent::getText),
            new Pair<>("Tooltip", InterfaceComponent::getTooltip),
            new Pair<>("Shadow color", InterfaceComponent::getShadowColor),
            new Pair<>("Actions", InterfaceComponent::getActions),
            new Pair<>("Item", x -> x.getItemId() + " (" + x.getItemStackSize() + ")"),
            new Pair<>("Border thickness", InterfaceComponent::getBorderThickness),
            new Pair<>("Uid", InterfaceComponent::getUid),
            new Pair<>("Visible", InterfaceComponent::isVisible),
            new Pair<>("Parameters", x -> {
                RSNodeTable<RSNode> ye = x.getProvider().getProperties();
                StringBuilder yeeeeeeehaw = new StringBuilder();
                if (ye != null) {
                    List<RSNode> hmm = ye.toList();
                    for (RSNode ok : hmm) {
                        if (ok instanceof RSIntegerNode) {
                            yeeeeeeehaw.append(ok.getHash() + ": " + ((RSIntegerNode) ok).getValue())
                                    .append(", ");
                        } else if (ok instanceof RSObjectNode) {
                            yeeeeeeehaw.append(ok.getHash() + ": " + ((RSObjectNode) ok).getReferent())
                                    .append(", ");
                        }
                    }
                }
                return yeeeeeeehaw.toString();
            })
    );

    private final List<DefaultMutableTreeNode> containers = new LinkedList<>();
    private final List<JLabel> labels = new LinkedList<>();
    private final DefaultTreeModel treeModel;
    private final JPanel container;
    private volatile String query = "";
    private DefaultMutableTreeNode root = null;
    private Object selected = null;

    public InterfaceExplorer() {
        super("Interface Explorer");
        Game.getEventDispatcher().register(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Game.getEventDispatcher().deregister(InterfaceExplorer.this);
            }
        });

        JTree tree = new JTree(treeModel = new DefaultTreeModel(root = new DefaultMutableTreeNode()));
        JScrollPane treePane = new JScrollPane(tree);
        tree.setRootVisible(false);

        JScrollPane containerPane = new JScrollPane(container = new JPanel());
        GroupLayout containerLayout = new GroupLayout(container);
        containerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        containerLayout.setAutoCreateGaps(true);
        containerLayout.setAutoCreateContainerGaps(true);
        GroupLayout.ParallelGroup parallelGroup = containerLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup sequentialGroup = containerLayout.createSequentialGroup();
        for (int i = 0; i < INFO.size(); i++) {
            JLabel label = new JLabel();
            containerLayout.setHorizontalGroup(parallelGroup = parallelGroup.addComponent(label));
            containerLayout.setVerticalGroup(sequentialGroup = sequentialGroup.addComponent(label));
            labels.add(label);
        }
        container.setLayout(containerLayout);
        JTextField queryEntry = new JTextField();
        queryEntry.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                query = queryEntry.getText().toLowerCase();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    update();
                }
            }
        });

        JLabel queryLabel = new JLabel("Query:");
        JButton reload = new JButton("Refresh");
        reload.addActionListener(e -> update());

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(treePane, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
                        .addComponent(containerPane, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addComponent(queryLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(queryEntry, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reload, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(reload)
                                .addComponent(queryEntry, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(queryLabel)).addGap(10)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(containerPane, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(treePane, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))));
        getContentPane().setLayout(layout);
        tree.addTreeSelectionListener(e -> updateSelected((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()));

        SwingResources.setStrictSize(this, 560, 800);
        BotTitlePane.decorate(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getOwner());
        setResizable(true);
        setVisible(true);
        update();
    }

    private static void draw(InterfaceComponent component, Graphics2D g) {
        Rectangle bounds = component.getBounds();
        AWTUtil.drawBorderedRectangle(g, bounds.x, bounds.y, bounds.width, bounds.height, Color.BLACK, Color.WHITE);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        for (InterfaceComponent sub : component.getComponents()) {
            draw(sub, g);
        }
    }

    private void addComponent(DefaultMutableTreeNode groupNode, InterfaceComponent component) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Component-" + component.getIndex());
        for (InterfaceComponent c : component.getComponents()) {
            addComponent(node, c);
        }
        if (matchesQuery(component) || node.getChildCount() > 0) {
            groupNode.add(node);
        }
    }

    private boolean matchesQuery(InterfaceComponent component) {
        if (query == null || query.isEmpty() || query.equals("")) {
            return true;
        }

        String text = component.getText();
        if (text != null && text.toLowerCase().contains(query.toLowerCase())) {
            return true;
        }

        if (component.getName().toLowerCase().contains(query.toLowerCase())) {
            return true;
        }

        String[] actions = component.getActions();
        for (String action : actions) {
            if (action != null && action.toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void updateSelected(DefaultMutableTreeNode node) {
        if (node == null) {
            updateLabels(null);
            return;
        }

        if (node.getParent().toString().equals("")) {
            updateLabels(null);
        }

        TreeNode[] path = node.getPath();
        int groupIndex = Integer.parseInt(path[1].toString().substring(path[1].toString().indexOf('-') + 1));
        Interface group = Interfaces.get(groupIndex);

        if (node.getUserObject().toString().contains("Interface")) {
            selected = group;
        }

        InterfaceComponent child = null;
        for (int i = 2; i < path.length; i++) {
            int index = Integer.parseInt(path[i].toString().substring(10));
            if (child != null) {
                child = child.getComponent(index);
            } else if (group != null) {
                child = group.getComponent(index);
            }
        }

        if (child != null) {
            selected = child;
        }

        updateLabels(generateInfo(groupIndex, selected));
        container.validate();
        container.repaint();
    }

    private Map<String, String> generateInfo(int group, Object c) {
        Map<String, String> info = new HashMap<>();

        if (c instanceof Interface) {
            info.put("Path", String.valueOf(group));
            return info;
        }

        InterfaceComponent component = (InterfaceComponent) c;
        StringBuilder path = new StringBuilder().append(component.getGroupIndex()).append(", ");
        if (component.isGrandchild()) {
            path.append(component.getParentIndex()).append(", ").append(component.getIndex());
        } else {
            path.append(component.getIndex());
        }

        for (Pair<String, Function<InterfaceComponent, Object>> mapping : INFO) {
            Object value = mapping.getRight().apply(component);
            if (value instanceof Object[]) {
                value = Arrays.toString((Object[]) value);
            } else if (value instanceof int[]) {
                value = Arrays.toString((int[]) value);
            } else if (value instanceof InterfaceAddress) {
                value = path;
            } else if (value == null) {
                value = "null";
            }
            info.put(mapping.getLeft(), value.toString());
        }
        return info;
    }

    private void updateLabels(Map<String, String> info) {
        if (info == null || info.isEmpty()) {
            for (JLabel label : labels) {
                label.setText("");
            }
        } else {
            for (int i = 0; i < labels.size(); i++) {
                String key = INFO.get(i).getLeft();
                String value = info.get(key);
                if (value != null) {
                    value = key + ": " + value;
                    labels.get(i).setText(value);
                }
            }
        }
    }

    private void clearNodeTree() {
        for (DefaultMutableTreeNode node : containers) {
            if (node.getParent() == null) {
                continue;
            }
            treeModel.removeNodeFromParent(node);
        }
        containers.clear();
        treeModel.reload();
    }

    private void buildNodeTree() {
        Interface[] all = Interfaces.getLoaded();
        for (Interface group : all) {
            if (group == null) {
                continue;
            }

            String prepend = "-";
            InterfaceComposite composite = InterfaceComposite.getByGroup(group.getIndex());
            if (composite != null) {
                prepend = "[" + composite.toString() + "]" + prepend;
            }

            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode("Interface" + prepend + group.getIndex());
            for (InterfaceComponent component : group.getComponents()) {
                if (component == null) {
                    continue;
                }
                addComponent(groupNode, component);
            }
            if (groupNode.getChildCount() > 0) {
                containers.add(groupNode);
                root.add(groupNode);
            }
        }
    }

    private void update() {
        updateLabels(null);

        try {
            SwingUtilities.invokeLater(this::clearNodeTree);
            SwingUtilities.invokeLater(this::buildNodeTree);
            SwingUtilities.invokeLater(treeModel::reload);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void notify(RenderEvent event) {
        Graphics g = event.getSource();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (selected != null) {
            if (selected instanceof InterfaceComponent) {
                draw((InterfaceComponent) selected, g2d);
            } else if (selected instanceof Interface) {
                for (InterfaceComponent component : ((Interface) selected).getComponents()) {
                    draw(component, g2d);
                }
            }
        }
    }
}
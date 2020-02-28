package org.rspeer.ui;

import org.rspeer.Configuration;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.rspeer_rest_api.BotPreferenceService;
import org.rspeer.script.ScriptController;
import org.rspeer.script.provider.LocalScriptProvider;
import org.rspeer.script.provider.RemoteScriptProvider;
import org.rspeer.script.provider.ScriptProvider;
import org.rspeer.script.provider.ScriptSource;
import org.rspeer.ui.account.XorSerializedAccountList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public final class ScriptSelector extends JFrame {

    private final BotWindow parent;
    private final ScriptController controller;
    private final DefaultTableModel model;
    private final JTable table;
    private final JScrollPane pane;
    private ScriptProvider<ScriptSource> provider;
    private JButton start;

    private JComboBox<GameAccount> accountSelector;
    private ScriptRow selected = null;
    private String query = "";

    public ScriptSelector(BotWindow parent) {
        super("Script Selector");
        this.parent = parent;
        controller = ScriptController.getInstance();
        model = createModel();
        table = createTable();
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                Point point = e.getPoint();
                int row = table.rowAtPoint(point);
                int col = table.columnAtPoint(point);
                if (row == -1 || col == -1) {
                    return;
                }

                Object o = model.getValueAt(row, col);
                if (col == 3 && o instanceof String && ((String) o).startsWith("http")) {
                    try {
                        String url = (String) o;
                        if (!Desktop.isDesktopSupported()) {
                            Runtime runtime = Runtime.getRuntime();
                            runtime.exec("xdg-open " + url);
                        } else {
                            Desktop.getDesktop().browse(new URL(url).toURI());
                        }
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }

                if (e.getClickCount() == 2 && table.getSelectedRow() == row) {
                    onScriptStarted(null);
                }
            }
        });

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);

        pane = new JScrollPane(progress);
        pane.setPreferredSize(new Dimension(600, 425));
        pane.getVerticalScrollBar().setUnitIncrement(15);
        setLayout(new BorderLayout());

        add(pane, BorderLayout.NORTH);
        add(createBottomPanel(), BorderLayout.SOUTH);
        pack();

        setResizable(true);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        BotTitlePane.decorate(this);
        loadScripts();
    }

    private DefaultTableModel createModel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Name");
        model.addColumn("Version");
        model.addColumn("Developer");
        model.addColumn("Description");
        return model;
    }

    private JPanel createBottomPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        //Search
        panel.add(new JLabel("Search:"));
        JTextField query = new JTextField("Enter to search");
        query.setPreferredSize(new Dimension(180, 20));
        query.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (query.getText().equals("Enter to search")) {
                    query.setText("");
                    query.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (query.getText().isEmpty()) {
                    query.setForeground(Color.GRAY);
                    query.setText("Enter to search");
                }
            }
        });

        query.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadScripts();
                } else {
                    String prev = ScriptSelector.this.query;
                    if (!query.getText().equals(prev)) {
                        ScriptSelector.this.query = query.getText().toLowerCase();
                    }
                }
            }
        });
        panel.add(query);

        JPanel gap = new JPanel();
        gap.setPreferredSize(new Dimension(50, 20));
        panel.add(gap);

        JLabel accountLabel = new JLabel("Account:");
        panel.add(accountLabel);

        JCheckBox scriptSourceCheckBox = new JCheckBox("View Local Scripts");

        scriptSourceCheckBox.addActionListener(e -> {
            setProvider(!scriptSourceCheckBox.isSelected());
            this.loadScripts();
            BotPreferenceService.setBoolean("localScriptsOnly", scriptSourceCheckBox.isSelected());
        });

        scriptSourceCheckBox.setSelected(BotPreferenceService.getBoolean("localScriptsOnly"));

        panel.add(scriptSourceCheckBox);

        XorSerializedAccountList list = new XorSerializedAccountList();
        list.add(0, new GameAccount("None", "", -1));
        accountSelector = new JComboBox<>(list.toArray(new GameAccount[0]));
        accountSelector.setPreferredSize(new Dimension(150, 20));
        panel.add(accountSelector);

        start = new JButton("Start");
        start.setPreferredSize(new Dimension(panel.getWidth(), 30));
        start.addActionListener(this::onScriptStarted);
        container.add(start, BorderLayout.SOUTH);
        container.add(panel, BorderLayout.NORTH);
        return container;
    }


    private JTable createTable() {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);
                return this;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        //table.setShowHorizontalLines(false);
        table.setRowHeight(25);
        table.setRowSelectionAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        TableColumnModel model = table.getColumnModel();
        model.getColumn(0).setPreferredWidth(100);
        model.getColumn(1).setPreferredWidth(65);
        model.getColumn(2).setPreferredWidth(100);
        model.getColumn(3).setPreferredWidth(335);
        table.getSelectionModel().addListSelectionListener(this::rowSelected);
        return table;
    }

    private void rowSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selected = (ScriptRow) model.getDataVector().get(row);
            }
        }
    }

    private void addRow(ScriptSource source) {
        model.addRow(new ScriptRow(source));
    }

    private boolean matchesQuery(ScriptSource source) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        String test = source.getName();
        if (test.toLowerCase().contains(query)) {
            return true;
        }

        test = source.getDeveloper();
        if (test.toLowerCase().contains(query)) {
            return true;
        }

        test = source.getCategory().toString().toLowerCase();
        if (test.toLowerCase().contains(query)) {
            return true;
        }

        test = source.getDescription();
        return test.toLowerCase().contains(query);
    }

    private ScriptProvider<ScriptSource> getProvider() {
        if (provider == null) {
            setProvider(!BotPreferenceService.getBoolean("localScriptsOnly"));
        }
        return provider;
    }

    private void setProvider(boolean remote) {
        if (remote) {
            provider = new RemoteScriptProvider();
        } else {
            provider = new LocalScriptProvider(new File(Configuration.SCRIPTS), new File("scriptsout"));
        }
    }

    private void loadScripts() {
        this.provider = getProvider();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                start.setText("Loading Scripts...");
                model.getDataVector().clear();
                model.fireTableDataChanged();

                List<ScriptSource> sources = new ArrayList<>();
                for (ScriptSource source : provider.load()) {
                    if (matchesQuery(source)) {
                        sources.add(source);
                    }
                }
                sources.sort(Comparator.naturalOrder());
                sources.forEach(ScriptSelector.this::addRow);
                model.fireTableDataChanged();
                start.setText("Start");
                return null;
            }

            @Override
            protected void done() {
                pane.setViewportView(table);
            }
        }.execute();
    }

    private void onScriptStarted(ActionEvent event) {
        if (selected != null) {
            try {
                provider.prepare(selected.definition);
                if (selected.definition.getTarget() == null) {
                    JOptionPane.showMessageDialog(this, "Failed to start script!");
                    controller.stopActiveScript();
                    return;
                } else if (controller.getActiveScript() != null) {
                    JOptionPane.showMessageDialog(this, "A Script is already running!");
                    return;
                }

                parent.getMenuBar().scriptStarted();

                controller.setActiveScript(selected.definition.getTarget().newInstance());
                if (accountSelector.getSelectedIndex() != 0) {
                    controller.getActiveScript().setAccount((GameAccount) accountSelector.getSelectedItem());
                }
                dispose();
            } catch (Exception e) {
                controller.stopActiveScript();
                e.printStackTrace();
            }
            dispose();
        }
    }

    private class ScriptRow extends Vector<String> {

        private final ScriptSource definition;

        private ScriptRow(ScriptSource definition) {
            this.definition = definition;
            Collections.addAll(this, definition.getName(),
                    String.valueOf(definition.getVersion()), definition.getDeveloper(),
                    definition.getLink().isEmpty() ? definition.getDescription() : definition.getLink()
            );
        }

        public boolean equals(Object o) {
            return o instanceof ScriptSource && o.equals(definition);
        }
    }
}

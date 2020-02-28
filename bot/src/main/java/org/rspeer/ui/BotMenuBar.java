package org.rspeer.ui;

import org.rspeer.Configuration;
import org.rspeer.api.commons.ExecutionService;
import org.rspeer.debugger.DebugPaint;
import org.rspeer.debugger.InterfaceExplorer;
import org.rspeer.debugger.varp.VarpChangeFrame;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.event.types.BotEvent;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.scene.Projection;
import org.rspeer.rspeer_rest_api.BotPreferenceService;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptController;
import org.rspeer.ui.account.AccountManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public final class BotMenuBar extends JToolBar implements ActionListener {

    private static final Color BACKGROUND_COLOR = new Color(0x212121);

    private final JButton start, stop, settings;
    private final ScriptController scriptController;
    private final BotWindow container;

    private final JButton cpusave;

    public BotMenuBar(BotWindow container) {
        scriptController = ScriptController.getInstance();
        this.container = container;

        add(Box.createHorizontalGlue());

        start = createAndAddButton(SwingResources.PLAY);
        stop = createAndAddButton(SwingResources.STOP);
        cpusave = createAndAddButton(SwingResources.LOW_CPU_DISABLED);

        //add(Box.createHorizontalGlue());
        settings = createAndAddButton(SwingResources.SETTINGS);

        JPopupMenu popupMenu = createSettingsMenu();
        settings.addActionListener(e -> popupMenu.show(settings, settings.getWidth(), settings.getHeight()));

        ImageIcon[] icons = SwingResources.loadButtonImages(BotTitlePane.class.getResourceAsStream("/org/rspeer/ui/person-icon.png"), 16, 16, .7f);
        JButton accounts;
        if (icons.length > 0) {
            accounts = createButton(SwingResources.ACCOUNT);
            //accounts = new IconButton(icons[0]);
            //accounts.setRolloverIcon(icons[1]);
            accounts.setBorder(new EmptyBorder(0, 8, 0, 8));
        } else {
            accounts = createButton(SwingResources.ACCOUNT);
        }

        accounts.addActionListener(act -> {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Account Manager");
                AccountManager manager = new AccountManager();
                frame.setContentPane(manager);
                frame.pack();
                frame.validate();
                frame.setLocationRelativeTo(this);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        manager.saveAllAccounts();
                        super.windowClosing(e);
                    }
                });
                BotTitlePane.decorate(frame);
                frame.setVisible(true);
            });
        });

        add(Box.createHorizontalStrut(15));
        add(accounts);

        setFloatable(false);
    }

    private JPopupMenu createSettingsMenu() {
        JPopupMenu menu = new JPopupMenu();

        JCheckBoxMenuItem menuDebug = new JCheckBoxMenuItem("Menu Action Debug");
        menuDebug.addActionListener(e -> Game.getClient().getCallbackHandler().setDebugMenuActions(menuDebug.isSelected()));
        menu.add(menuDebug);

        JMenuItem itfcs = new JMenuItem("Interface Explorer");
        itfcs.addActionListener(e -> EventQueue.invokeLater(InterfaceExplorer::new));
        menu.add(itfcs);

        JMenuItem varps = new JMenuItem("Varp Debugger");
        varps.addActionListener(e -> EventQueue.invokeLater(VarpChangeFrame::new));
        menu.add(varps);

        JCheckBoxMenuItem entity = new JCheckBoxMenuItem("Debug paint");
        DebugPaint debugger = new DebugPaint();
        entity.addActionListener(e -> {
            if (Game.getClient() == null) {
                entity.setSelected(!entity.isSelected());
                return;
            }

            EventDispatcher dispatcher = Game.getEventDispatcher();
            if (entity.isSelected()) {
                dispatcher.register(debugger);
            } else {
                dispatcher.deregister(debugger);
            }
        });
        menu.add(entity);
        menu.add(createApperanceMenu());

        JCheckBoxMenuItem enableFileLogging = new JCheckBoxMenuItem("Enable File Logging");
        enableFileLogging.addActionListener(e -> {
            boolean selected = enableFileLogging.isSelected();
            container.getBot().toggleFileLogging(selected);
            BotPreferenceService.setBoolean("enableFileLogging", selected);
            if (selected) {
                System.out.println("File logging has been enabled. All logs will be saved to " + Configuration.LOGS);
            }
        });
        enableFileLogging.setSelected(BotPreferenceService.getBoolean("enableFileLogging"));
        menu.add(enableFileLogging);

        return menu;
    }

    private JMenuItem createApperanceMenu() {
        JMenuItem apperanceMenu = new JMenu("Appearance");

        //Display IP
        JCheckBoxMenuItem displayIp = new JCheckBoxMenuItem("Display Ip On Titlebar");
        displayIp.addActionListener(e -> ExecutionService.execute(() -> {
            BotPreferenceService.setBoolean("showIpOnMenuBar", displayIp.isSelected());
            Game.getClient().getEventDispatcher().immediate(new BotEvent("tile_pane_changed", true));
        }));
        displayIp.setSelected(BotPreferenceService.getBoolean("showIpOnMenuBar"));
        apperanceMenu.add(displayIp);

        //Display Account
        JCheckBoxMenuItem displayAccount = new JCheckBoxMenuItem("Display Account On Titlebar");
        displayAccount.addActionListener(e -> ExecutionService.execute(() -> {
            BotPreferenceService.setBoolean("showAccountOnMenuBar", displayAccount.isSelected());
            Game.getClient().getEventDispatcher().immediate(new BotEvent("tile_pane_changed", true));
        }));
        displayAccount.setSelected(BotPreferenceService.getBoolean("showAccountOnMenuBar"));
        apperanceMenu.add(displayAccount);

        //Display Script
        JCheckBoxMenuItem displayScript = new JCheckBoxMenuItem("Display Script On Titlebar");
        displayScript.addActionListener(e -> ExecutionService.execute(() -> {
            BotPreferenceService.setBoolean("showScriptOnMenuBar", displayScript.isSelected());
            Game.getClient().getEventDispatcher().immediate(new BotEvent("tile_pane_changed", true));
        }));
        displayScript.setSelected(BotPreferenceService.getBoolean("showScriptOnMenuBar"));
        apperanceMenu.add(displayScript);

        //Allow Script Message
        JCheckBoxMenuItem allowScriptMessage = new JCheckBoxMenuItem("Display Script Status");
        allowScriptMessage.addActionListener(e -> ExecutionService.execute(() -> {
            BotPreferenceService.setBoolean("allowScriptMessageOnMenuBar", allowScriptMessage.isSelected());
            System.out.println("Successfully toggled script status. Script status allows the script writer to update your toolbar with a custom message from within the script. This is useful for tracking status, kills, drops, etc.");
            Game.getClient().getEventDispatcher().immediate(new BotEvent("tile_pane_changed", true));
        }));
        allowScriptMessage.setSelected(BotPreferenceService.getBoolean("allowScriptMessageOnMenuBar"));
        apperanceMenu.add(allowScriptMessage);

        addCpuSaveListener(cpusave);
        return apperanceMenu;
    }

    private void addCpuSaveListener(JButton cpusave) {
        cpusave.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (Game.getClient() != null) {
                    if (!cpusave.getText().equals(SwingResources.LOW_CPU_DISABLED) || Projection.isRenderingDisabled()) {
                        cpusave.setText(SwingResources.LOW_CPU_DISABLED);
                        Projection.setRenderingDisabled(false);
                        Projection.setEngineTickDelayEnabled(false);
                    } else {
                        cpusave.setText(SwingResources.LOW_CPU_ENABLED);
                        Projection.setRenderingDisabled(true);
                        if (SwingUtilities.isRightMouseButton(e)) {
                            Projection.setEngineTickDelayEnabled(true);
                        }
                    }
                }
            }
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(28, 28));
        button.setFont(SwingResources.getFontAwesome(14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.addActionListener(this);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(button.getForeground().darker().darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setForeground(button.getForeground().brighter().brighter());
            }
        });
        return button;
    }

    private JButton createAndAddButton(String text) {
        JButton button = createButton(text);
        add(Box.createHorizontalStrut(15));
        add(button);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) e.getSource();
            switch (button.getText()) {
                case SwingResources.PLAY: {
                    playScript();
                    break;
                }

                case SwingResources.PAUSE: {
                    scriptController.getActiveScript().setState(Script.State.PAUSED);
                    scriptStopped();
                    break;
                }

                case SwingResources.STOP: {
                    scriptController.stopActiveScript();
                    scriptStopped();
                    break;
                }
            }
        }
    }

    private void playScript() {
        if (scriptController.getActiveScript() != null
                && scriptController.getActiveScript().getState() == Script.State.PAUSED) {
            scriptController.getActiveScript().setState(Script.State.RUNNING);
            scriptStarted();
        } else {
            ScriptSelector selector = new ScriptSelector(container);
            selector.setVisible(true);
        }
    }

    public void scriptStarted() {
        start.setText(SwingResources.PAUSE);
        stop.setText(SwingResources.STOP);
    }

    public void scriptStopped() {
        start.setText(SwingResources.PLAY);
        stop.setText(SwingResources.STOP);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*
         * g.setColor(border); g.drawLine(0, 1, getWidth(), 1); g.drawLine(0, 2,
         * getWidth(), 2); g.drawLine(0, 3, getWidth(), 3);
         */
        //  g.setColor(BACKGROUND_COLOR);
        //  g.fillRect(0, 0, getWidth(), getHeight()); // 0, 4
    }
}
package org.rspeer.script.random;

import org.rspeer.Inuvation;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.component.Bank;
import org.rspeer.game.api.scene.Npcs;
import org.rspeer.game.api.scene.Players;
import org.rspeer.script.Script;
import org.rspeer.script.provider.ScriptSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.BooleanSupplier;

public class Restart23hHandler extends ScriptDaemon {

    private static final BooleanSupplier DEFAULT_RESTART_CONDITION = () -> {
        Player me = Players.getLocal();
        if (me == null) {
            return true;
        }
        return me.getTarget() == null && !Bank.isOpen()
                && Npcs.newQuery().targeting(Players.getLocal()).results().isEmpty();
    };

    private final BooleanSupplier condition;

    public Restart23hHandler(Script script) {
        this(script, DEFAULT_RESTART_CONDITION);
    }

    public Restart23hHandler(Script script, BooleanSupplier condition) {
        super(script);
        this.condition = condition;
    }

    private static String getJarPath() {
        return Restart23hHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");
    }

    @Override
    public String name() {
        return "Restarting bot";
    }

    @Override
    public int execute() {
        String process = getProcess();
        executeProcess(process);
        Inuvation.shutdown();
        return 1000;
    }

    private String getProcess() {
        StringBuilder builder = new StringBuilder();
        String system = System.getProperty("os.name");
        if (system.contains("Windows") || system.contains("Linux") || system.contains("Mac")) {
            builder.append("java -noverify -Xmx1000m");
        }
        builder.append(" -cp \"").append(getJarPath()).append("\" ").append("org.rspeer.Inuvation");

        //TODO if args arent present then generate them based on current running acc and script

        String[] args = script.getController().getArgs();
        builder.append(" ");
        if (args == null || args.length == 0) {
            builder.append(generateArgs());
        } else {
            String arguments = Arrays.toString(args)
                    .replace("[", "")
                    .replace("]", "")
                    .replace(",", "");
            builder.append(arguments);
        }

        return builder.toString();
    }

    private String generateArgs() {
        StringBuilder builder = new StringBuilder();
        ScriptSource source = script.getController().getSource();
        if (source != null) {
            builder.append("-script ").append(source.getName());
            GameAccount account = script.getAccount();
            if (account != null) {
                builder.append(" -account ")
                        .append(account.getUsername())
                        .append(":")
                        .append(account.getPassword());
            }
        }
        return builder.toString();
    }

    private void executeProcess(String process) {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                Runtime.getRuntime().exec(process);
            } else {
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", process});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validate() {
        return Game.getRuntime().getElapsed().toHours() >= 23 && condition.getAsBoolean();
    }
}

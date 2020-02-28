package org.rspeer.arguments;

import org.rspeer.Configuration;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.game.api.scene.Projection;
import org.rspeer.rspeer_rest_api.Logger;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptController;
import org.rspeer.script.provider.*;
import org.rspeer.ui.account.XorSerializedAccountList;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public final class QuickStartService {

    private final QuickStartArgs args;
    private final ScriptProvider<ScriptSource> localProvider;
    private final ScriptProvider<ScriptSource> remoteProvider;

    public QuickStartService(QuickStartArgs args) {
        this.args = args;
        this.localProvider = new LocalScriptProvider(new File(Configuration.SCRIPTS));
        this.remoteProvider = new RemoteScriptProvider();
    }

    public void start() {

        SwingUtilities.invokeLater(this::tryApplyScript);

        if (args == null) {
            return;
        }

        QuickStartArgs.Config config = args.getConfig();
        if (config == null) {
            return;
        }

        if (config.getLowCpuMode()) {
            System.out.println("Setting low cpu mode.");
            Projection.setRenderingDisabled(true);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean tryApplyScript() {
        if (args == null || args.getScriptName() == null) {
            return false;
        }

        String name = args.getScriptName().toLowerCase().trim();
        if (name == null) {
            return false;
        }

        System.out.println("Attempting to start script by name: " + name);

        List<ScriptSource> sources = new ArrayList<>();

        if (args != null && args.getIsRepoScript()) {
            ScriptSource[] remoteSources = remoteProvider.load();
            sources.addAll(Arrays.asList(remoteSources));
        } else {
            ScriptSource[] remoteSources = remoteProvider.load();
            ScriptSource[] localSources = localProvider.load();
            sources.addAll(Arrays.asList(remoteSources));
            sources.addAll(Arrays.asList(localSources));
        }
        ScriptSource script = null;
        for (ScriptSource s : sources) {
            if (s.getName().toLowerCase().trim().equals(name)) {
                if (s instanceof RemoteScriptSource && args != null && !args.getIsRepoScript()) {
                    continue;
                }
                script = s;
                break;
            }
        }

        if (script == null) {
            System.err.println("Failed to find script by name " + name + ". Unable to quickstart.");
            return false;
        }

        try {
            if (script instanceof RemoteScriptSource) {
                remoteProvider.prepare(script);
            } else {
                localProvider.prepare(script);
            }

            tryApplyScript(name, script);
        } catch (Exception e) {
            Logger.getInstance().capture(e);
            ScriptController.getInstance().stopActiveScript();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void tryApplyScript(String name, ScriptSource source) {
        try {
            ScriptController controller = ScriptController.getInstance();
            Script script = source.getTarget().newInstance();

            if (script == null) {
                System.err.println("Failed to find script by name " + name + ". Unable to quickstart.");
                return;
            }

            if (args != null && args.getScriptArgs() != null) {
                controller.setArgs(args.getScriptArgs().split(" "));
            }

            if (args != null && args.getRsUsername() != null && args.getRsPassword() != null) {
                XorSerializedAccountList accountList = new XorSerializedAccountList();
                boolean set = false;
                for (GameAccount account : accountList) {
                    if (account.getUsername().equals(args.getRsUsername())) {
                        set = true;
                        script.setAccount(account);
                        break;
                    }
                }

                if (!set) {
                    script.setAccount(new GameAccount(args.getRsUsername(), args.getRsPassword()));
                }
            }

            //TODO add breaks
            /*if (args != null && args.getBreakProfile() != null) {
                BreakProfile named = BreakProfile.fromName(args.getBreakProfile());
                if (named != null) {
                    invoked.setBreakProfile(named);
                }
            }*/
            System.out.println("Successfully started script: " + script.getMeta().name() + " by " + script.getMeta().developer());
            controller.setSource(source);
            controller.setActiveScript(script);
        } catch (Exception e) {
            Logger.getInstance().capture(e);
            ScriptController.getInstance().stopActiveScript();
            e.printStackTrace();
        }
    }
}

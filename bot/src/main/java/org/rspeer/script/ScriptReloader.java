package org.rspeer.script;

import org.rspeer.api.commons.GameAccount;
import org.rspeer.arguments.QuickStartArgs;
import org.rspeer.arguments.QuickStartService;
import org.rspeer.script.provider.RemoteScriptSource;
import org.rspeer.script.provider.ScriptSource;

public final class ScriptReloader {

    public void execute() {
        ScriptController controller = ScriptController.getInstance();
        Script script = ScriptController.getInstance().getActiveScript();
        if (script == null) {
            return;
        }
        System.out.println("Attempting to reload script: " + script.getMeta().name() + " by " + script.getMeta().developer() + ".");
        QuickStartArgs args = buildArgs(script, controller.getSource());
        controller.stopActiveScript();
        QuickStartService service = new QuickStartService(args);
        service.start();
    }

    private QuickStartArgs buildArgs(Script script, ScriptSource source) {
        QuickStartArgs args = new QuickStartArgs();
        args.setScriptName(source.getName());
        args.setRepoScript(source instanceof RemoteScriptSource);

        //TODO add breaks.
        /*
        if(script.getProfile() != null) {
            args.setBreakProfile(script.getProfile().getName());
        }
         */
        GameAccount account = script.getAccount();
        if (account != null) {
            args.setRsUsername(account.getUsername());
            args.setRsPassword(account.getPassword());
        }
        return args;
    }

}

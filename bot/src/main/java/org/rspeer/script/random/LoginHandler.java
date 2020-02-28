package org.rspeer.script.random;

import org.rspeer.api.commons.GameAccount;
import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.input.Keyboard;
import org.rspeer.game.api.input.Mouse;
import org.rspeer.script.Script;

import java.awt.event.KeyEvent;

public class LoginHandler extends ScriptDaemon {

    private static final InterfaceAddress USERNAME = new InterfaceAddress(596, 38);
    private static final InterfaceAddress PASSWORD = new InterfaceAddress(596, 67); // was 64 as of 08/10/2018
    //private static final WidgetAddress LOGIN = new WidgetAddress(596, 72); // was 71
    private static final InterfaceAddress TRY_AGAIN = new InterfaceAddress(744, 230); // was 138
    private static final InterfaceAddress ERROR = new InterfaceAddress(744, 220); // was 138
    private InterfaceComponent loginButton;

    private boolean forceClear = false;
    private boolean incorrect = false;

    private GameAccount cachedAccount;

    public LoginHandler(Script script) {
        super(script);
    }

    @Override
    public boolean validate() {
        loginButton = Interfaces.getFirst(596, a -> a.getText().equals("Login"));
        return !Game.isLoggedIn() && loginButton != null && !loginButton.getText().equals("") && script.getAccount() != null;
    }

    @Override
    public int execute() {
        if (!script.getAccount().equals(cachedAccount)) {
            reset();
        }
        cachedAccount = script.getAccount();
        InterfaceComponent tryAgain = TRY_AGAIN.resolve();
        if (!incorrect && tryAgain != null &&
                (tryAgain.getText().toLowerCase().contains("error connecting") ||
                        tryAgain.getText().toLowerCase().contains("Unable to connect:") ||
                        tryAgain.getText().toLowerCase().contains("try again"))) {
            incorrect = true;
            forceClear = true;
            Keyboard.pressEventKey(KeyEvent.VK_ESCAPE);
            return 1000;
        }
        InterfaceComponent error = ERROR.resolve();
        if (error != null && !error.getText().equals("") && !error.getText().equals("Logging In - Please Wait") && !error.getText().equals("Login - bitte warten") && !error.getText().equals("Entrando - Aguarde") && !error.getText().equals("Connexion en cours. Patientez...")) {
            script.handleLoginError(error.getText());
            return 1000;
        }
        InterfaceComponent uname = USERNAME.resolve();
        InterfaceComponent pw = PASSWORD.resolve();
        if (uname == null || pw == null) return 1000;
        if (uname.getText().equals(script.getAccount().getUsername()) && pw.getText().length() == script.getAccount().getPassword().length()
                && !forceClear) {
            Mouse.click(loginButton.getBounds().getLocation());
            Time.sleep(100);
        } else {
            if (!uname.getText().equals(script.getAccount().getUsername())) {
                InterfaceComponent fixedUsername = Interfaces.getComponent(596, 37);
                if (fixedUsername != null && fixedUsername.isVisible()) {
                    Mouse.click(fixedUsername.getBounds().getLocation());
                }
                Time.sleep(100, 200);
                if (uname.getText().equals("")) {
                    Keyboard.sendText(script.getAccount().getUsername(), false);
                    Time.sleepUntil(() -> uname.getText().equals(script.getAccount().getUsername()), 2000);
                } else {
                    for (int i = 0; i < uname.getText().length() * 2; i++) {
                        Keyboard.pressEventKey(KeyEvent.VK_DELETE);
                        Time.sleep(10);
                        Keyboard.releaseKey(KeyEvent.VK_DELETE);
                        Keyboard.pressEventKey(KeyEvent.VK_BACK_SPACE);
                        Time.sleep(10);
                        Keyboard.releaseKey(KeyEvent.VK_BACK_SPACE);
                    }
                }
            } else if (pw.getText().length() != script.getAccount().getPassword().length() || forceClear) {
                Mouse.click(pw.getBounds().getLocation());
                Time.sleep(100, 200);
                if (pw.getText().equals("") && !forceClear) {
                    Keyboard.sendText(script.getAccount().getPassword(), false);
                    Time.sleepUntil(() -> pw.getText().length() == script.getAccount().getPassword().length(), 2000);
                } else {
                    for (int i = 0; i < pw.getText().length() * 2; i++) {
                        Keyboard.pressEventKey(KeyEvent.VK_DELETE);
                        Time.sleep(10);
                        Keyboard.releaseKey(KeyEvent.VK_DELETE);
                        Keyboard.pressEventKey(KeyEvent.VK_BACK_SPACE);
                        Time.sleep(10);
                        Keyboard.releaseKey(KeyEvent.VK_BACK_SPACE);
                    }
                    forceClear = false;
                }
            }
        }
        return 200;
    }

    public void reset() {
        forceClear = true;
        incorrect = false;
    }


    @Override
    public String name() {
        return "Login";
    }

}

package org.rspeer.loader.task;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.rspeer.Configuration;
import org.rspeer.api.concurrent.Tasks;
import org.rspeer.io.HttpCommons;
import org.rspeer.bot.Bot;
import org.rspeer.bot.BotTask;
import org.rspeer.io.Bytes;
import org.rspeer.io.InnerPack;
import org.rspeer.loader.Crawler;
import org.rspeer.loader.GameConfiguration;
import org.rspeer.rspeer_rest_api.RSPeerApi;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class DownloadModscriptTask extends BotTask {

    private static final boolean LOCAL = false;
    private final InnerPack innerPack;

    public DownloadModscriptTask(Bot bot, InnerPack innerPack) {
        super(bot);
        this.innerPack = innerPack;
    }

    @Override
    public void run() {
        try {
            String hash = innerPack.sha1();
            System.out.println("Modscript hash: " + hash);
            byte[] modscript = getModscript(hash, bot.getGameConfiguration());
            bot.getView().getPanel().setMessage("Injecting...");
            Tasks.execute(new InjectTask(bot, modscript, innerPack));
        } catch (Throwable e) {
            EventQueue.invokeLater(() -> bot.getView().getPanel().setError("Failed to inject.\nReason:\n" + e.getMessage()));
            e.printStackTrace();
        }
    }

    private static byte[] getModscript(String sha1, GameConfiguration configuration) throws Exception {
        if (LOCAL) {
            return Files.readAllBytes(Paths.get(Configuration.CACHE + sha1 + ".dat"));
        }
        Request request = new Request(sha1, configuration.getArchive(),
                configuration.getParameter("0"),
                configuration.getParameter("-1"));
        try {
            HttpResponse<InputStream> result = Unirest.post(Configuration.API_BASE + "Inuvation/Modscript")
                    .header("Authorization", "Bearer " + RSPeerApi.getSession())
                    .body(new Gson().toJson(request)).asBinary();
            if(result.getStatus() == 401) {
                throw new Exception("You do not have access to RSPeer Inuvation.");
            }
            if (result.getStatus() != 200) {
                throw new Exception("Failed to get modscript.\n" + HttpCommons.streamToString(result.getBody()));
            }
            InputStream stream = result.getBody();
            return Bytes.array(stream);
        } catch (UnirestException | IOException e) {
            throw new Exception("Failed to get modscript. Something went wrong.");
        }
    }
}

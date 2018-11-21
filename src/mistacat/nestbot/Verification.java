package mistacat.nestbot;

import lombok.Getter;
import lombok.Setter;
import mistacat.nestbot.utils.Utils;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles all the verification of RotMG to discord accounts.
 * Created by MistaCat 10/7/2018
 */
public class Verification {
    @Getter private static Map<IUser, String> verificationRequests = new ConcurrentHashMap<>();
    @Getter @Setter private static int vericodes = 0;

    public static final String API_URL = "http://www.tiffit.net/RealmInfo/api/user?u=";
    public static final int STAR_REQ = 30;
    public static final int FAME_REQ = 3000;
    public static final int CLASS_REQ = 6;

    public static void setupVerification() {
        NestBot.getGuild().getChannelByID(Constants.VERIFY_CHANNEL).bulkDelete();
        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("How to verify!");
        msg.withColor(0, 0, 255);
        msg.withThumbnail(NestBot.client.getApplicationIconURL());
        msg.withDesc("Please type -verify in this channel to receive instructions on how to verify!");
        msg.appendField("REQUIREMENTS", "30 Stars\n3000 Alive Fame\n3 - 6/8+ OR 2 - 8/8", true);
        Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.VERIFY_CHANNEL), msg.build());
    }

    /**
     * Creates a verification request for a discord user.
     * @param user
     */
    public static String requestVerificationUser(IUser user) {
        String passcode = "PEST_" + getVericodes();
        getVerificationRequests().put(user, passcode);
        setVericodes(getVericodes() + 1);
        return passcode;
    }

    /**
     * Returns a Json string from a URL.
     * @param urlString
     * @return
     * @throws Exception
     */
    public static String readJsonURL(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * Returns a Json object for a realmeye username. username is not case sensitive.
     * @param username
     * @return
     */
    public static JSONObject getRealmPlayer(String username) {
        try {
            return new JSONObject(readJsonURL(API_URL + username));
        } catch (Exception ex) {
            Utils.sendConsoleDebug("Failed to load json object from URL");
        }

        return null;
    }


}

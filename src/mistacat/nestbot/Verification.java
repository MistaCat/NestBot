package mistacat.nestbot;

import lombok.Getter;
import lombok.Setter;
import mistacat.nestbot.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
    public static final int STAR_REQ = 20;
    public static final int FAME_REQ = 2000;
    public static final int CLASS_REQ = 2;

    public static void setupVerification() {
        NestBot.getGuild().getChannelByID(Constants.VERIFY_CHANNEL).bulkDelete();
        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("How to verify!");
        msg.withColor(0, 0, 255);
        msg.withThumbnail(NestBot.client.getApplicationIconURL());
        msg.withDesc("Please type -verify in this channel to receive instructions on how to verify!");
        msg.appendField("REQUIREMENTS", "20 Stars\n2000 Alive Fame\n1 - 6/8 Class", true);
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
    public static String readJsonURL(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);

            return body;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a Json object for a realmeye username. username is not case sensitive.
     * @param username
     * @return
     */
    public static JSONObject getRealmPlayer(String username) {
        String json = readJsonURL(API_URL + username);

        if (json != null)
            return new JSONObject(json);
        else
            return null;
    }
}

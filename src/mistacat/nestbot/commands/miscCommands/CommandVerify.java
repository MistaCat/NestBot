package mistacat.nestbot.commands.miscCommands;

import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.punishment.BlacklistManager;
import mistacat.nestbot.utils.Utils;
import mistacat.nestbot.Verification;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CommandVerify extends Command {
    public CommandVerify() {
        setAliases(new String[] {"verify"});
        setMinRank(Rank.DEFAULT);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (msg.getAuthor().hasRole(NestBot.getGuild().getRoleByID(Constants.VERIFIED))) {
            Utils.sendPM(msg.getAuthor(), "You're already verified silly!");
            return;
        }

        if (args.length == 0) {
            if (Verification.getVerificationRequests().containsKey(msg.getAuthor())) {
                Utils.sendPM(msg.getAuthor(), "You currently have an active vericode: " + Verification.getVerificationRequests().get(msg.getAuthor()) +
                        "\nPlease enter that into your realmeye description and type -verify [realmeye name]");
                return;
            }

            String vericode = Verification.requestVerificationUser(msg.getAuthor());
            Utils.sendPM(msg.getAuthor(), "Thank you for verifying your account on pest control!\n" +
                    "Your verification code is: " + vericode +
                    "\nPlace the verification code in any of your realmeye description bars.\n" +
                    "Once you have saved the code to your realmeye description type -verify [realmeye name].\n" +
                    "You can verify in the verification channel! Realm eye names are NOT case-sensitive!");
            return;
        }

        if (BlacklistManager.isBlacklisted(args[0].toLowerCase())) {
            Utils.sendPM(msg.getAuthor(), "This realmeye name is blacklisted! You may not verify on this server!");
            return;
        }

        if (!Verification.getVerificationRequests().containsKey(msg.getAuthor())) {
            Utils.sendPM(msg.getAuthor(), "You don't have a pending verification!\n" +
                    "Please type -verify in the verification channel to get a code!");
            return;
        }

        JSONObject json = Verification.getRealmPlayer(args[0].replaceAll("[^A-Za-z0-9]", ""));

        if (json == null) {
            Utils.sendPM(msg.getAuthor(), "Could not get realmeye information.\n" +
                    "Please make sure your realmeye name is correct. If issues persist contact MistaCat on discord!");
            return;
        }

        if (json.has("error")) {
            Utils.sendPM(msg.getAuthor(), "Please ensure your realmeye information is public!");
            return;
        }

        if ((int) json.get("fame") < Verification.FAME_REQ) {
            Utils.sendPM(msg.getAuthor(), "You do not meet the required " + Verification.FAME_REQ + " alive fame!");
            return;
        }

        if ((int) json.get("rank") < Verification.STAR_REQ) {
            Utils.sendPM(msg.getAuthor(), "You do not meet the required " + Verification.FAME_REQ + " class stars!");
            return;
        }

        JSONArray characters = json.getJSONArray("characters");
        int points = 0;
        for (int i = 0; i < characters.length(); i++) {
            if (characters.getJSONObject(i).getString("stats_maxed").equalsIgnoreCase("6/8")
                    || characters.getJSONObject(i).getString("stats_maxed").equalsIgnoreCase("7/8"))
                points = points + 2;
            else if (characters.getJSONObject(i).getString("stats_maxed").equalsIgnoreCase("8/8"))
                points = points + 3;
        }

        if (points < Verification.CLASS_REQ) {
            Utils.sendPM(msg.getAuthor(), "You do not have the classes required to verify! (1 - 6/8)");
            return;
        }

        JSONArray description = json.getJSONArray("description");
        for (int i = 0; i < description.length(); i++) {
            if (description.getString(i).toLowerCase().contains(Verification.getVerificationRequests().get(msg.getAuthor()).toLowerCase())) {
                msg.getAuthor().addRole(NestBot.getGuild().getRoleByID(Constants.VERIFIED));
                Utils.changeNickname(msg.getAuthor(), json.getString("name"));
                Verification.getVerificationRequests().remove(msg.getAuthor());
                Utils.sendPM(msg.getAuthor(), "Thank you for verifying in pest control! Good luck on the nest raids!");
                logVerification(msg.getAuthor(), json.getString("name"));
                return;
            }
        }

        Utils.sendPM(msg.getAuthor(), "Couldn't find the vericode in your description!\n" +
                "Please make sure its in it's own line and wait a few seconds for the servers to save before trying again!");
    }

    private void logVerification(IUser user, String name) {
        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Verification success! - " + user.getName());
        msg.withColor(0, 255, 0);
        msg.withThumbnail(user.getAvatarURL());
        msg.withDesc("Name: " + name +
                "\nRealmeye: " + "https://www.realmeye.com/player/" + name);
        msg.withFooterText("Date: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

        Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.VERIFICATION_LOGS), msg.build());
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Verify");
        msg.appendField("Required rank", "None", false);
        msg.appendField("Syntax", "-alias <realmeye name>", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Verifies that a discord account is linked to a realmeye account", false);
        return msg.build();
    }
}

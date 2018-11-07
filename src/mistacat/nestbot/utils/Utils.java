package mistacat.nestbot.utils;

import mistacat.nestbot.NestBot;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Various utilities for the nest bot.
 * Created by MistaCat 10/7/2018
 */
public class Utils {

    /**
     * Sends a msg to console. Good for logging what people are doing or testing code.
     * @param msg
     */
    public static void sendConsoleDebug(String msg) {
        System.out.println(msg);
    }

    /**
     * Sends a plain text msg to a specified channel.
     * @param channel
     * @param msg
     */
    public static IMessage sendMessage(IChannel channel, String msg) {
        return RequestBuffer.request(() -> channel.sendMessage(msg)).get();
    }

    /**
     * Sends an embed object message to a specified channel.
     * @param channel
     * @param msg
     * @return
     */
    public static IMessage sendEmbed(IChannel channel, EmbedObject msg) {
        return RequestBuffer.request(() -> channel.sendMessage(msg)).get();
    }

    /**
     * Adds a reaction emoji to a message.
     * @param msg
     * @param reaction
     */
    public static void addReaction(IMessage msg, ReactionEmoji reaction) {
        RequestBuffer.request(() -> msg.addReaction(reaction));
    }

    /**
     * Changes a text channel's permission on a user basis.
     * @param channel
     * @param user
     * @param allow
     * @param deny
     */
    public static void updateChannelPerms(IChannel channel, IUser user, EnumSet<Permissions> allow, EnumSet<Permissions> deny) {
        RequestBuffer.request(() -> channel.overrideUserPermissions(user, allow, deny));
    }

    /**
     * Changes a text channel's permission on a discord role basis.
     * @param channel
     * @param role
     * @param allow
     * @param deny
     */
    public static void updateChannelPerms(IChannel channel, IRole role, EnumSet<Permissions> allow, EnumSet<Permissions> deny) {
        RequestBuffer.request(() -> channel.overrideRolePermissions(role, allow, deny));
    }

    /**
     * Changes a voice channel's permission on a user basis.
     * @param channel
     * @param user
     * @param allow
     * @param deny
     */
    public static void updateVoiceChannelPerms(IVoiceChannel channel, IUser user, EnumSet<Permissions> allow, EnumSet<Permissions> deny) {
        RequestBuffer.request(() -> channel.overrideUserPermissions(user, allow, deny));
    }

    /**
     * Changes a voice channel's permission on a discord role basis.
     * @param channel
     * @param role
     * @param allow
     * @param deny
     */
    public static void updateVoiceChannelPerms(IVoiceChannel channel, IRole role, EnumSet<Permissions> allow, EnumSet<Permissions> deny) {
        RequestBuffer.request(() -> channel.overrideRolePermissions(role, allow, deny));
    }

    /**
     * Moves a discord user to a specified channel.
     * @param target
     * @param user
     */
    public static void moveUser(IUser user, IVoiceChannel target) {
        RequestBuffer.request(() -> user.moveToVoiceChannel(target));
    }

    /**
     * Gets a user from a mention string
     * @param mention
     * @return
     */
    public static IUser getUserFromMention(String mention) {
        String userID = mention.replace("<", "");
        userID = userID.replace("@", "");
        userID = userID.replace(">", "");
        userID = userID.replace("!", "");
        try {
            return NestBot.getGuild().getUserByID(Long.parseLong(userID));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Creates a single string from an array of args.
     * @param args
     * @return
     */
    public static String singleArg(String[] args) {
        String arg = args[0];
        for (String word : Arrays.copyOfRange(args, 1, args.length))
            arg = arg + " " + word;

        return arg;
    }

    /**
     * Sends a PM to a specific discord user.
     * @param user
     * @param msg
     * @return
     */
    public static IMessage sendPM(IUser user, String msg) {
        return RequestBuffer.request(() -> user.getOrCreatePMChannel().sendMessage(msg)).get();
    }

    /**
     * Turn milliseconds into a user friendly string in relation to the current time epoch.
     * @param time
     * @return formatted
     */
    public static String formatTimeFromNow(long time) {
        return formatTime(time == -1 ? -1 : time - System.currentTimeMillis());
    }

    /**
     * Turn milliseconds into a user friendly string.
     * @param time
     * @return formatted
     */
    public static String formatTime(long time) {
        if (time == -1)
            return "never";

        time /= 1000;
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < TimeInterval.values().length; i++) {
            TimeInterval iv = TimeInterval.values()[TimeInterval.values().length - i - 1];
            if (time >= iv.getInterval()) {
                int temp = (int) (time - (time % iv.getInterval()));
                int add = temp / iv.getInterval();
                display.append(" ");
                display.append(add);
                display.append(iv.getSuffix());
                display.append(add > 1 && iv != TimeInterval.SECOND ? "s" : "");
                time -= temp;
            }
        }

        return display.length() > 0 ? display.toString().substring(1) : "0s";
    }

    /**
     * Format milliseconds based into a friendly display based on their distance from the current time.
     * @param time
     * @return formatted
     */
    public static String formatTimeFullFromNow(long time) {
        return formatTimeFull(time == -1 ? -1 : time - System.currentTimeMillis());
    }

    /**
     * Formats milliseconds into a user friendly display.
     * Different from formatTime because this does not use abbreviations.
     * @param time
     * @return formatted
     */
    public static String formatTimeFull(long time) {
        if (System.currentTimeMillis() + time == -1 || time == -1)
            return "Never";

        time /= 1000;
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < TimeInterval.values().length; i++) {
            TimeInterval iv = TimeInterval.values()[TimeInterval.values().length - i - 1];
            if (time >= iv.getInterval()) {
                int temp = (int) (time - (time % iv.getInterval()));
                int add = temp / iv.getInterval();
                display.append(" ");
                display.append(add);
                display.append(" ");
                display.append(iv.name().toLowerCase());
                display.append(add > 1 ? "s" : "");
                time -= temp;
            }
        }

        return display.length() > 0 ? display.toString().substring(1) : "0 Seconds";
    }

    /**
     * Convert user input ie: "3d 2h" into a date relative to zero.
     * @param input
     * @return date
     */
    public static long fromInput(String input) {
        if (input.startsWith("-"))
            return -1; // "Never"

        long time = 0;
        for (String s : input.split(" ")) {
            String code = StringUtils.isNumeric(s) ? "s" : s.substring(s.length() - 1);
            time += Long.parseLong(s.substring(0, Math.max(1,(StringUtils.isNumeric(s) ? s.length() : s.length() - 1)))) * (long) TimeInterval.getByCode(code).getInterval() * 1000L;
        }
        return time;
    }

    /**
     * Load the amount of time from now a string such as "3d 2h" is.
     * @param input
     * @return date
     */
    public static long fromInputFuture(String input) {
        long time = fromInput(input);
        return time >= 0 ? time + System.currentTimeMillis() : -1;
    }
}

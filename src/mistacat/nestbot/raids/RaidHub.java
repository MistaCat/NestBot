package mistacat.nestbot.raids;

import lombok.Getter;
import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all the current nest runs that are currently active.
 * Created by MistaCat 10/7/2018
 */
@Getter
public class RaidHub {
    public static List<Raid> activeRaids = new CopyOnWriteArrayList<>();

    @EventSubscriber
    public void onRaidJoin(UserVoiceChannelJoinEvent evt) {
        if(!isRaidChannel(evt.getVoiceChannel()))
            return;

        Utils.updateVoiceChannelPerms(evt.getVoiceChannel(), evt.getUser(), EnumSet.of(Permissions.VOICE_CONNECT), EnumSet.noneOf(Permissions.class));
    }

    @EventSubscriber
    public void onRaidLeave(UserVoiceChannelLeaveEvent evt) {
        if(!isRaidChannel(evt.getVoiceChannel()))
            return;

        if(getRaid(evt.getVoiceChannel()).isRaidActive())
            return;

        Utils.updateVoiceChannelPerms(evt.getVoiceChannel(), evt.getUser(), EnumSet.noneOf(Permissions.class), EnumSet.noneOf(Permissions.class));
    }

    /**
     * Starts a raid. set size to 0 for infinite, and set timer to -1 for no timer.
     * @param leader
     * @param size
     * @param timer
     */
    public static void startRaid(IUser leader, int size, int timer) {
        IVoiceChannel raidRoom = NestBot.getGuild().createVoiceChannel("New raid");
        raidRoom.changeCategory(NestBot.getGuild().getCategoryByID(Constants.RAID_CATEGORY));
        if (size > 0)
            raidRoom.changeUserLimit(Math.min(size, 99));

        activeRaids.add(new Raid(raidRoom, leader, timer));
    }

    /**
     * Gets an active raid from a discord user. Will return null if they have not started a raid.
     * @param leader
     * @return
     */
    public static Raid getRaid(IUser leader) {
        for (Raid raid : activeRaids) {
            if (raid.getLeader() == leader)
                return raid;
        }

        return null;
    }

    /**
     * Checks if a discord user is currently leading a raid.
     * @param leader
     * @return
     */
    public static boolean isLeading(IUser leader) {
        return getRaid(leader) != null;
    }

    /**
     * Gets an active raid from it's voice channel. Will return null if the channel is not a raid channel.
     * @param channel
     * @return
     */
    public static Raid getRaid(IVoiceChannel channel) {
        for (Raid raid : activeRaids) {
            if (raid.getRaidRoom() == channel)
                return raid;
        }

        return null;
    }

    public static boolean isRaidChannel(IVoiceChannel channel) { return getRaid(channel) != null; }
}

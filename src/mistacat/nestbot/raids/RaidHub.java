package mistacat.nestbot.raids;

import lombok.Getter;
import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all the current nest runs that are currently active.
 * Created by MistaCat 10/7/2018
 */
@Getter
public class RaidHub {
    public static List<Raid> activeRaids = new CopyOnWriteArrayList<>();

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
}

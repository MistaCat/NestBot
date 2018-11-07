package mistacat.nestbot.raids;

import lombok.Getter;
import mistacat.nestbot.Constants;
import mistacat.nestbot.Emote;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The base class for a nest run or "raid". Everything will be handled here.
 * Created by MistaCat 10/7/2018
 */
@Getter
public class Raid {
    private IVoiceChannel raidRoom;
    private IMessage raidMsg;
    private IUser leader;
    private Long startTime;

    private boolean raidActive = false;
    private boolean failed;
    private int countdownTime;
    private int countdown;
    private ScheduledFuture<?> countdownTimer;
    private final Long COUNTDOWN_INTERVAL = 5L;
    private final ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(2);


    public Raid(IVoiceChannel voice, IUser leader, int time) {
        this.raidRoom = voice;
        this.leader = leader;
        this.countdownTime = time;
        this.raidMsg = Utils.sendMessage(NestBot.getGuild().getChannelByID(Constants.RAID_CHANNEL), "Raid start!");

        initializeRaid();
    }

    public void initializeRaid() {
        this.failed = false;
        this.raidActive = false;
        raidMsg.delete();

        countdown = countdownTime;
        raidRoom.changeName("Nest starting soon!");
        this.raidMsg = Utils.sendMessage(NestBot.getGuild().getChannelByID(Constants.RAID_CHANNEL), "Raid start!");
        ping();
        raidMsg.edit(createRaidMsg().build());

        Utils.updateVoiceChannelPerms(raidRoom, NestBot.getGuild().getEveryoneRole(), EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.VOICE_CONNECT, Permissions.VOICE_SPEAK));
        Utils.updateVoiceChannelPerms(raidRoom, NestBot.getGuild().getRoleByID(Constants.VERIFIED), EnumSet.of(Permissions.VOICE_CONNECT), EnumSet.noneOf(Permissions.class));
        Utils.updateVoiceChannelPerms(raidRoom, NestBot.getGuild().getRoleByID(Constants.TECH), EnumSet.allOf(Permissions.class), EnumSet.noneOf(Permissions.class));
        for (Rank rank : Rank.values())
            if (rank.isCanSpeak())
                Utils.updateVoiceChannelPerms(raidRoom, NestBot.getGuild().getRoleByID(rank.getID()), EnumSet.allOf(Permissions.class), EnumSet.noneOf(Permissions.class));

        if (countdown > 0) {
            countdownTimer = TIMER.scheduleAtFixedRate(() -> {
                if (raidActive)
                    countdownTimer.cancel(true);

                if (!raidActive && countdown <= 0)
                    startRaid();
                else if (!raidActive)
                    updateRaidMsg();

                countdown -= COUNTDOWN_INTERVAL;
            }, 0L, COUNTDOWN_INTERVAL, TimeUnit.SECONDS);
        }
    }

    public void startRaid() {
        this.raidActive = true; //Activate the raid
        this.countdown = 0; //Ensure timer is at zero in case a manual start for a timed raid.
        this.startTime = System.currentTimeMillis();

        Utils.updateVoiceChannelPerms(raidRoom, NestBot.getGuild().getRoleByID(Constants.VERIFIED), EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.VOICE_CONNECT));

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle(leader.getName() + "'s raid has begun!");
        msg.withThumbnail(leader.getAvatarURL());
        msg.withColor(255, 0, 0);
        msg.withDesc("Please wait until another leader begins a raid!");
        raidMsg.edit(msg.build());

        raidRoom.changeName(leader.getName() + "'s Nest raid.");
    }

    public void abortRaid() {
        this.failed = true;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle(leader.getName() + "'s raid was a failure!");
        msg.withThumbnail(leader.getAvatarURL());
        msg.withColor(255, 0, 0);
        msg.withDesc("The raid has been aborted as nobody is left in the dungeon to complete it!");
        msg.withFooterText("Awaiting raid leader's decision.");
        raidMsg.edit(msg.build());
    }

    public void completeRaid(boolean newRun, int playersLeft) {
        this.raidActive = false;
        FeedbackHub.activeFeedback.add(new LeaderFeedback(leader, raidRoom.getConnectedUsers()));
        logRaid(playersLeft);

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle(leader.getName() + "'s raid was a success!");
        msg.withThumbnail(leader.getAvatarURL());
        msg.withColor(0, 0, 255);
        msg.withDesc("If you were a part of the run please enter the feed back channel and give feedback and rating for the leader!" +
                (newRun ? "\n\nAnother run will be starting soon! Please stay in the channel if you wish to participate." : ""));
        raidMsg.edit(msg.build());

        if (newRun)
            TIMER.schedule(this::initializeRaid, 20L, TimeUnit.SECONDS);
        else
            TIMER.schedule(this::endRaid, 20L, TimeUnit.SECONDS);
    }

    private void logRaid(int players) {
        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle(leader.getName() + " completed a raid!");
        msg.withDesc("Date Completed: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +
                "\nTime Taken: " + Utils.formatTimeFull(System.currentTimeMillis() - startTime) +
                "\nPlayers Remaining: " + (players == -1 ? "Unknown" : players));
        Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.RUN_LOGS), msg.build());
    }

    public void endRaid() {
        RaidHub.activeRaids.remove(this);
        raidMsg.delete();
        for (IUser user : this.raidRoom.getConnectedUsers())
            Utils.moveUser(user, NestBot.getGuild().getVoiceChannelByID(Constants.TEST_VOICE));

        TIMER.schedule(() -> raidRoom.delete(), 1L, TimeUnit.MINUTES);
        TIMER.shutdown();
    }

    private EmbedBuilder createRaidMsg() {
        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle(leader.getName() + " has started a raid!");
        msg.withThumbnail(leader.getAvatarURL());
        msg.withColor(0, 255, 0);
        msg.withDesc("A new nest raid has started! \nJoin the newly opened channel if you wish to participate in the raid." +
                "\nPlease react to anything that relates to you! Fake reacts may result in suspension!");
        msg.appendField("If you are bringing a KEY react with", Emote.NEST_KEY.display(), true);
        msg.appendField("If you are bringing a QOT react with", Emote.QOT.display(), true);
        msg.appendField("If you are bringing a PRIEST react with", Emote.PRIEST.display(), true);
        msg.appendField("If you are bringing a WARRIOR react with", Emote.WARRIOR.display(), true);
        msg.appendField("If you are bringing a PALADIN react with", Emote.PALLY.display(), true);
        Utils.addReaction(raidMsg, Emote.NEST.getEmote());
        Utils.addReaction(raidMsg, Emote.NEST_KEY.getEmote());
        Utils.addReaction(raidMsg, Emote.QOT.getEmote());
        Utils.addReaction(raidMsg, Emote.PRIEST.getEmote());
        Utils.addReaction(raidMsg, Emote.WARRIOR.getEmote());
        Utils.addReaction(raidMsg, Emote.PALLY.getEmote());

        if (countdown < 0)
            msg.withFooterText("The raid will begin when the raid leader is ready");
        else
            msg.withFooterText("The raid will begin in " + countdown + " seconds!");

        return msg;
    }

    private void updateRaidMsg() {
        EmbedBuilder msg = createRaidMsg();
        msg.withFooterText("The raid will begin in " + countdown + " seconds!");
        raidMsg.edit(msg.build());
    }

    private void ping() {
        IMessage msg = Utils.sendMessage(NestBot.getGuild().getChannelByID(Constants.RAID_CHANNEL),
                "@here " + getLeader().getName() + " has started a raid!");
        msg.delete();
    }
}

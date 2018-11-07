package mistacat.nestbot.raids;

import lombok.Getter;
import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A leader feedback class that holds all the information for a feedback
 * Created by MistaCat 10/8/2018
 */
@Getter
public class LeaderFeedback {
    private IChannel feedbackRoom;
    private IMessage feedbackMsg;
    private IUser leader;
    private List<IUser> raiders;

    private final ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(1);
    private final Long FEEDBACK_TIME = 3L;

    public LeaderFeedback(IUser user, List<IUser> raiders) {
        this.leader = user;
        this.raiders = raiders;

        initializeFeedback();
    }

    private void initializeFeedback() {
        this.feedbackRoom = NestBot.getGuild().createChannel(leader.getName().toLowerCase().replace(" ", "-") + "-feedback");
        feedbackRoom.changeCategory(NestBot.getGuild().getCategoryByID(Constants.FEEDBACK_CATEGORY));
        Utils.updateChannelPerms(feedbackRoom, NestBot.getGuild().getEveryoneRole(), EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.READ_MESSAGES));
        Utils.updateChannelPerms(feedbackRoom, NestBot.getGuild().getRoleByID(Constants.TECH), EnumSet.allOf(Permissions.class), EnumSet.noneOf(Permissions.class));

        for (IUser user : raiders)
            Utils.updateChannelPerms(feedbackRoom, user, EnumSet.of(Permissions.READ_MESSAGES), EnumSet.noneOf(Permissions.class));

        createPoll();

        TIMER.schedule(this::endFeedback, FEEDBACK_TIME, TimeUnit.MINUTES);
    }

    private void endFeedback() {
        FeedbackHub.activeFeedback.remove(this);
        TIMER.shutdown();
        logFeedback();

        feedbackRoom.delete();
    }

    private void logFeedback() {
        int pos = feedbackMsg.getReactionByEmoji(ReactionEmoji.of("\uD83D\uDC4D")).getCount();
        int neg = feedbackMsg.getReactionByEmoji(ReactionEmoji.of("\uD83D\uDC4E")).getCount();

        EmbedBuilder pollResults = new EmbedBuilder();
        pollResults.withTitle(leader.getName() + "'s feedback");
        pollResults.withColor(0, 255, 0);
        pollResults.withThumbnail(leader.getAvatarURL());
        pollResults.withDesc("Positive votes: " + (pos - 2) +
                "\nNegative votes: " + (neg - 2) +
                "\nComments: " + (feedbackRoom.getMessageHistory().size() - 1));
        Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.FEEDBACK_LOGS), pollResults.build());
        feedbackMsg.delete();

        for (IMessage msg : feedbackRoom.getMessageHistory().asArray()) {
            if (msg.getContent().isEmpty())
                continue;

            Utils.sendMessage(NestBot.getGuild().getChannelByID(Constants.FEEDBACK_LOGS),
                    msg.getAuthor() + " commented: " + msg.getContent());
        }
    }

    private void createPoll() {
        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle(leader.getName() + " feedback!");
        msg.withThumbnail(leader.getAvatarURL());
        msg.withColor(0, 255, 0);
        msg.withDesc("Rate how " + leader.getName() + " lead the raid!\nPlease react with a thumbs up or a thumbs down!\n" +
                "After you react please leave a comment below about the raid!");
        this.feedbackMsg = Utils.sendEmbed(feedbackRoom, msg.build());
        Utils.addReaction(feedbackMsg, ReactionEmoji.of("\uD83D\uDC4D"));
        Utils.addReaction(feedbackMsg, ReactionEmoji.of("\uD83D\uDC4E"));
    }
}

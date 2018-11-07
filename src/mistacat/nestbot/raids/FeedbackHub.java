package mistacat.nestbot.raids;

import lombok.Getter;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Listens to the feedback channels that are active
 * Created by MistaCat 10/8/2018
 */
public class FeedbackHub {
    public static List<LeaderFeedback> activeFeedback = new CopyOnWriteArrayList<>();

    @EventSubscriber
    public void onFeedback(MessageReceivedEvent evt) {
        if (!isFeedbackComment(evt.getMessage()))
            return;

        LeaderFeedback feedback = getFeedbackChannel(evt.getMessage());
        if (feedback.getRaiders().contains(evt.getAuthor())) {
            Utils.updateChannelPerms(feedback.getFeedbackRoom(), evt.getAuthor(), EnumSet.of(Permissions.READ_MESSAGES, Permissions.ADD_REACTIONS), EnumSet.of(Permissions.SEND_MESSAGES));
            Utils.sendPM(evt.getAuthor(), "Thank you for the feedback! It is appreciated.");
            return;
        }

        evt.getMessage().delete();
    }

    /**
     * Gets a Leader Feedback from a message.
     * @param msg
     * @return
     */
    public static LeaderFeedback getFeedbackChannel(IMessage msg) {
        for (LeaderFeedback feedback : activeFeedback)
            if (feedback.getFeedbackRoom() == msg.getChannel())
                return feedback;

        return null;
    }

    /**
     * Returns a boolean if a messages is a comment in a feedback channel.
     * @param msg
     * @return
     */
    public static boolean isFeedbackComment(IMessage msg) {
        return getFeedbackChannel(msg) != null;
    }
}

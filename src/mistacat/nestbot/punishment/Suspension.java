package mistacat.nestbot.punishment;

import lombok.Getter;
import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class to hold information about a suspension.
 * Created by MistaCat 10/9/2018
 */
@Getter
public class Suspension {
    private IUser recipient;
    private IUser punisher;
    private Long suspensionTime;
    private String reason;
    private String displayDate;

    public Suspension(IUser punisher, IUser recipient, Long time, String reason) {
        this.punisher = punisher;
        this.recipient = recipient;
        this.suspensionTime = time;
        this.reason = reason;
        this.displayDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        initializeSuspension();
    }

    private void initializeSuspension() {
        recipient.removeRole(NestBot.getGuild().getRoleByID(Constants.VERIFIED));
        recipient.addRole(NestBot.getGuild().getRoleByID(Constants.SUSPENDED));

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("You were suspended!");
        msg.withDesc("Suspended by: " + punisher +
                "\nReason: " + reason +
                "\nExpires in: " + Utils.formatTimeFullFromNow(suspensionTime));
        Utils.sendEmbed(recipient.getOrCreatePMChannel(), msg.build());
        logSuspension(false);
    }

    public void finishSuspension() {
        recipient.removeRole(NestBot.getGuild().getRoleByID(Constants.SUSPENDED));
        recipient.addRole(NestBot.getGuild().getRoleByID(Constants.VERIFIED));

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("You have been unsuspended!");
        msg.withDesc("Please follow the rules and have a good day!");
        Utils.sendEmbed(recipient.getOrCreatePMChannel(), msg.build());
        logSuspension(true);
        SuspensionHub.activeSuspensions.remove(this);
    }

    private void logSuspension(boolean unsuspend) {
        EmbedBuilder msg = new EmbedBuilder();
        if (unsuspend) {
            msg.withTitle(recipient.getName() + " has been unsuspended!");
            msg.withDesc("Date punished: " + displayDate +
                    "\nSuspended by: " + punisher);
        } else {
            msg.withTitle(recipient.getName() + " has been suspended!");
            msg.withDesc("Date punished: " + displayDate +
                    "\nSuspended by: " + punisher +
                    "\nReason: " + reason +
                    "\nExpires in: " + Utils.formatTimeFullFromNow(suspensionTime));
        }
        Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.SUSPENSION_LOGS), msg.build());
    }
}

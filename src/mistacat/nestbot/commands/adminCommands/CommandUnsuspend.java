package mistacat.nestbot.commands.adminCommands;

import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.punishment.SuspensionHub;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Command that unsuspends discord users from the raids.
 * Created by MistaCat 10/9/2018
 */
public class CommandUnsuspend extends Command {
    public CommandUnsuspend() {
        setAliases(new String[] {"unsuspend"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (args.length != 1) {
            Utils.sendPM(msg.getAuthor(), "Improper arguments!");
            return;
        }

        IUser recipient = Utils.getUserFromMention(args[0]);

        if (SuspensionHub.getUserSuspension(Utils.getUserFromMention(args[0])) == null &&
                !recipient.hasRole(NestBot.getGuild().getRoleByID(Constants.SUSPENDED))) {
            Utils.sendPM(msg.getAuthor(), "That user is not suspended!");
            return;
        }

        if (SuspensionHub.getUserSuspension(Utils.getUserFromMention(args[0])) == null &&
                recipient.hasRole(NestBot.getGuild().getRoleByID(Constants.SUSPENDED))) {

            recipient.removeRole(NestBot.getGuild().getRoleByID(Constants.SUSPENDED));
            recipient.addRole(NestBot.getGuild().getRoleByID(Constants.VERIFIED));

            EmbedBuilder susMsg = new EmbedBuilder();
            susMsg.withTitle("You have been unsuspended!");
            susMsg.withDesc("Please follow the rules and have a good day!");
            Utils.sendEmbed(recipient.getOrCreatePMChannel(), susMsg.build());

            susMsg = new EmbedBuilder();
            susMsg.withTitle(recipient.getName() + " has been unsuspended!");
            susMsg.withDesc("Unsuspended by: " + msg.getAuthor());
            Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.SUSPENSION_LOGS), susMsg.build());
            return;
        }

        SuspensionHub.getUserSuspension(recipient).finishSuspension();
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Unsuspend");
        msg.appendField("Required rank", "Raid leader", false);
        msg.appendField("Syntax", "-alias [@user]", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Unsuspends a discord user so they may partake in raids again.", false);
        return msg.build();
    }
}

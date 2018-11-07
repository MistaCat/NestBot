package mistacat.nestbot.commands.adminCommands;

import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.punishment.Suspension;
import mistacat.nestbot.punishment.SuspensionHub;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Arrays;

/**
 * Command that suspends a discord user from the raids.
 * Created by MistaCat 10/9/2018
 */
public class CommandSuspend extends Command {
    public CommandSuspend() {
        setAliases(new String[] {"suspend"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (args.length < 2) {
            Utils.sendPM(msg.getAuthor(), "Not enough arguments to suspend!");
            return;
        }

        if (Utils.getUserFromMention(args[0]) == null) {
            Utils.sendPM(msg.getAuthor(), "Could not find that user!");
            return;
        }

        if (SuspensionHub.getUserSuspension(Utils.getUserFromMention(args[0])) != null) {
            Utils.sendPM(msg.getAuthor(), "That user is already suspended!");
            return;
        }

        IUser recipient = Utils.getUserFromMention(args[0]);
        Long time = Utils.fromInputFuture(args[1]);
        String reason = (args.length >= 3) ? Utils.singleArg(Arrays.copyOfRange(args, 2, args.length)) : "Reason no specified";
        SuspensionHub.activeSuspensions.add(new Suspension(msg.getAuthor(), recipient, time, reason));
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Suspend");
        msg.appendField("Required rank", "Raid leader", false);
        msg.appendField("Syntax", "-alias [@user] [#(s/h/d)], <reason>", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Suspends a discord user from raiding for a specific amount of time", false);
        return msg.build();
    }
}

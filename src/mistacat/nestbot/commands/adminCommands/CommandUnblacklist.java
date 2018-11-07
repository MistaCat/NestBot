package mistacat.nestbot.commands.adminCommands;

import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.punishment.BlacklistManager;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandUnblacklist extends Command {
    public CommandUnblacklist() {
        setAliases(new String[] {"unblacklist"});
        setMinRank(Rank.OWNER);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (args.length != 1) {
            Utils.sendPM(msg.getAuthor(), "Invalid amount of arguments!");
            return;
        }

        if (!BlacklistManager.isBlacklisted(args[0])) {
            Utils.sendPM(msg.getAuthor(), "That user is not on the blacklist!");
            return;
        }

        BlacklistManager.removeFromBlacklist(args[0]);
        EmbedBuilder log = new EmbedBuilder();
        log.withTitle(args[0] + " has been removed from the blacklist!");
        log.withDesc("Unblacklisted by: " + msg.getAuthor() +
                "\nDate: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.SUSPENSION_LOGS), log.build());
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Unblacklist");
        msg.appendField("Required rank", "Owner", false);
        msg.appendField("Syntax", "-alias [realmeye name]", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Unblacklists a realmeye name, allowing them to re-verify", false);
        return msg.build();
    }
}

package mistacat.nestbot.commands.leaderCommands;

import mistacat.nestbot.Rank;
import mistacat.nestbot.utils.Utils;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.raids.RaidHub;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class CommandBeginRaid extends Command {
    public CommandBeginRaid() {
        setAliases(new String[] {"start", "begin", "beginraid"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (!RaidHub.isLeading(msg.getAuthor())) {
            Utils.sendPM(msg.getAuthor(), "You have not started a raid!");
            return;
        }

        RaidHub.getRaid(msg.getAuthor()).startRaid();
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Begin raid");
        msg.appendField("Required rank", "Almost raid leader.", false);
        msg.appendField("Syntax", "-alias", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Closes raid room to begin a nest raid.", false);
        return msg.build();
    }
}

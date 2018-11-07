package mistacat.nestbot.commands.leaderCommands;

import mistacat.nestbot.Rank;
import mistacat.nestbot.utils.Utils;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.raids.RaidHub;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class CommandEndRaid extends Command {
    public CommandEndRaid() {
        setAliases(new String[] {"end", "finish", "complete", "done", "cleared", "endraid"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (RaidHub.getRaid(msg.getAuthor()) == null) {
            Utils.sendPM(msg.getAuthor(), "You have not started a raid!");
            return;
        }

        if (!RaidHub.getRaid(msg.getAuthor()).isRaidActive()) {
            RaidHub.getRaid(msg.getAuthor()).endRaid();
            return;
        }

        if (RaidHub.getRaid(msg.getAuthor()).isFailed()) {
            RaidHub.getRaid(msg.getAuthor()).endRaid();
            return;
        }

        if (args.length > 0 && StringUtils.isNumeric(args[0])) {
            RaidHub.getRaid(msg.getAuthor()).completeRaid(false, Integer.parseInt(args[0]));
            return;
        }

        RaidHub.getRaid(msg.getAuthor()).completeRaid(false, -1);
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: End raid");
        msg.appendField("Required rank", "Almost raid leader.", false);
        msg.appendField("Syntax", "-alias <Players remaining>", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Ends a raid and closes the raid room also logs who was left alive.", false);
        return msg.build();
    }
}

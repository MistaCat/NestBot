package mistacat.nestbot.commands.leaderCommands;

import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.raids.RaidHub;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class CommandAbortRaid extends Command {
    public CommandAbortRaid() {
        setAliases(new String[] {"abortraid", "abort", "failure", "mission_failed_we'll_get_em_next_time"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (RaidHub.getRaid(msg.getAuthor()) == null) {
            Utils.sendPM(msg.getAuthor(), "You have not started a raid!");
            return;
        }

        if (!RaidHub.getRaid(msg.getAuthor()).isRaidActive()) {
            Utils.sendPM(msg.getAuthor(), "Your current raid is not in progress! So you can't abort it!");
            return;
        }

        RaidHub.getRaid(msg.getAuthor()).abortRaid();
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Abort raid");
        msg.appendField("Required rank", "Almost raid leader.", false);
        msg.appendField("Syntax", "-alias", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Aborts a failed raid so raid leader may start a new one or end without logging.", false);
        return msg.build();
    }
}

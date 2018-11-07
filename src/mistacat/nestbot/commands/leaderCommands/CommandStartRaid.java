package mistacat.nestbot.commands.leaderCommands;

import mistacat.nestbot.Rank;
import mistacat.nestbot.utils.Utils;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.raids.RaidHub;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Command to start a nest run. Only usable by raid leaders
 * Created by MistaCat 10/7/2018
 */
public class CommandStartRaid extends Command {
    public CommandStartRaid() {
        setAliases(new String[] {"raid", "newraid"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (RaidHub.isLeading(msg.getAuthor())) {
            if (!RaidHub.getRaid(msg.getAuthor()).isRaidActive()) {
                Utils.sendPM(msg.getAuthor(), "Your current raid is not in progress. So you can't finish and start a new one!");
                return;
            }

            if (RaidHub.getRaid(msg.getAuthor()).isFailed()) {
                RaidHub.getRaid(msg.getAuthor()).initializeRaid();
                return;
            }

            if (args.length > 0 && StringUtils.isNumeric(args[0])) {
                RaidHub.getRaid(msg.getAuthor()).completeRaid(true, Integer.parseInt(args[0]));
                return;
            }

            RaidHub.getRaid(msg.getAuthor()).completeRaid(true, -1);
            return;
        }

        if (args.length == 1) {
            try {
                int size = Integer.parseInt(args[0]);
                RaidHub.startRaid(msg.getAuthor(), size, -1);
                return;
            } catch (Exception ex) {
                Utils.sendPM(msg.getAuthor(), "Invalid raid size! Please retry");
                return;
            }
        }

        if (args.length == 2) {
            try {
                int size = Integer.parseInt(args[0]);
                int time = Integer.parseInt(args[1]);
                RaidHub.startRaid(msg.getAuthor(), size, time);
                return;
            } catch (Exception ex) {
                Utils.sendPM(msg.getAuthor(), "Invalid raid size or timer! Please retry");
                return;
            }
        }

        RaidHub.startRaid(msg.getAuthor(), 0, -1);
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Create raid");
        msg.appendField("Required rank", "Almost raid leader.", false);
        msg.appendField("Syntax", "-alias <room size> <countdown time (seconds)>", false);
        msg.appendField("Syntax", "-alias <Players remaining>", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Creates a new raid room and pings people that a nest raid will be starting.\n" +
                "Can also re-open the room for another raid after one has begun with logging.", false);
        return msg.build();
    }
}

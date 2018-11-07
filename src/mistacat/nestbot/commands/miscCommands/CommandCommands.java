package mistacat.nestbot.commands.miscCommands;

import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class CommandCommands extends Command {
    public CommandCommands() {
        setAliases(new String[] {"commands"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        if (args.length == 1) {
            if (NestBot.commands.getCommand(args[0]) == null) {
                Utils.sendPM(msg.getAuthor(), "Could not find that command!");
                return;
            }

            Utils.sendEmbed(msg.getAuthor().getOrCreatePMChannel(), NestBot.commands.getCommand(args[0]).getInfo());
            return;
        }

        for (Command cmd : NestBot.commands)
            Utils.sendEmbed(msg.getAuthor().getOrCreatePMChannel(), cmd.getInfo());
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Command information");
        msg.appendField("Required rank", "Almost raid leader.", false);
        msg.appendField("Syntax", "-alias <command alias>", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Shows information for the pest control bot commands", false);
        return msg.build();
    }
}

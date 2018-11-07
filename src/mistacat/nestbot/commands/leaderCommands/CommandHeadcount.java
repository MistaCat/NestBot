package mistacat.nestbot.commands.leaderCommands;

import mistacat.nestbot.Constants;
import mistacat.nestbot.Emote;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.Command;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class CommandHeadcount extends Command {
    public CommandHeadcount() {
        setAliases(new String[] {"headcount", "unnecessaryping"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(IMessage msg, String alias, String[] args) {
        IMessage ping = Utils.sendMessage(NestBot.getGuild().getChannelByID(Constants.RAID_CHANNEL),
                "@here " + msg.getAuthor() + " has started a headcount!");
        ping.delete();

        EmbedBuilder headcount = new EmbedBuilder();
        headcount.withTitle(msg.getAuthor().getName() + "'s headcount");
        headcount.withDesc("React with the nest icon if you plan to join the next raid!\nIf you have a key to pop react with it!");
        IMessage headcountMsg = Utils.sendEmbed(NestBot.getGuild().getChannelByID(Constants.RAID_CHANNEL), headcount.build());
        Utils.addReaction(headcountMsg, Emote.NEST.getEmote());
        Utils.addReaction(headcountMsg, Emote.NEST_KEY.getEmote());
    }

    @Override
    public EmbedObject getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Command: Create headcount");
        msg.appendField("Required rank", "Almost raid leader.", false);
        msg.appendField("Syntax", "-alias", false);
        msg.appendField("Aliases", aliases, false);
        msg.appendField("Information", "Creates a headcount. Please delete if not in use!", false);
        return msg.build();
    }
}

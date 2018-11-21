package mistacat.nestbot.commands;

import mistacat.nestbot.Constants;
import mistacat.nestbot.NestBot;
import mistacat.nestbot.Rank;
import mistacat.nestbot.commands.miscCommands.CommandVerify;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages all the commands sent to the discord
 * Created by MistaCat 10/7/2018
 */
public class CommandHub extends ArrayList<Command> {
    private static final String COMMAND_PREFIX = "-";

    public Command getCommand(String alias) {
        return stream().filter(cmd -> Arrays.stream(cmd.getAliases()).anyMatch(s -> s.equalsIgnoreCase(alias))).findAny().orElse(null);
    }

    @EventSubscriber
    public void onCommand(MessageReceivedEvent evt) {
        if (evt.getChannel() == NestBot.getGuild().getChannelByID(Constants.VERIFY_CHANNEL) && !evt.getMessage().getContent().startsWith(COMMAND_PREFIX)) {
            removeMsg(evt);
            return;
        }

        if (evt.getAuthor().isBot() || !evt.getMessage().getContent().startsWith(COMMAND_PREFIX)) // if a bot or not a Command ignore.
            return;

        String[] split = evt.getMessage().getContent().split(" ");
        String alias = split[0].replace(COMMAND_PREFIX, "");
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        if (getCommand(alias) != null) {
            if (!Rank.getHighestRank(evt.getAuthor()).isAtLeast(getCommand(alias).getMinRank())) {
                Utils.sendPM(evt.getAuthor(), "You don't have permission for this command!");
                removeMsg(evt);
                return;
            }

            if (getCommand(alias) instanceof CommandVerify && (evt.getChannel() != NestBot.getGuild().getChannelByID(Constants.VERIFY_CHANNEL))) {
                Utils.sendPM(evt.getAuthor(), "Please verify in the verification channel!");
                removeMsg(evt);
                return;
            }

            if (getCommand(alias).getMinRank().isAtLeast(Rank.ALMOST_RL) && evt.getChannel() != NestBot.getGuild().getChannelByID(Constants.RAID_COMMANDS)) {
                Utils.sendPM(evt.getAuthor(), "Please use the raid leader commands channel!");
                removeMsg(evt);
                return;
            }

            getCommand(alias).execute(evt.getMessage(), alias, args);

            if (evt.getChannel() == NestBot.getGuild().getChannelByID(Constants.VERIFY_CHANNEL))
                removeMsg(evt);
        } else {
            Utils.sendPM(evt.getAuthor(), "Invalid command!");
            removeMsg(evt);
        }
    }

    private void removeMsg(MessageReceivedEvent evt) {
        if (evt.getChannel() != evt.getAuthor().getOrCreatePMChannel())
            evt.getMessage().delete();
    }
}

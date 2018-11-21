package mistacat.nestbot;

import lombok.Getter;
import mistacat.nestbot.commands.CommandHub;
import mistacat.nestbot.commands.leaderCommands.*;
import mistacat.nestbot.commands.miscCommands.CommandCommands;
import mistacat.nestbot.commands.miscCommands.CommandVerify;
import mistacat.nestbot.commands.adminCommands.CommandBlacklist;
import mistacat.nestbot.commands.adminCommands.CommandSuspend;
import mistacat.nestbot.commands.adminCommands.CommandUnblacklist;
import mistacat.nestbot.commands.adminCommands.CommandUnsuspend;
import mistacat.nestbot.punishment.BlacklistManager;
import mistacat.nestbot.punishment.SuspensionHub;
import mistacat.nestbot.raids.FeedbackHub;
import mistacat.nestbot.raids.RaidHub;
import mistacat.nestbot.utils.Utils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.obj.IGuild;

/**
 * The main discord bot class for RotMG discord server "Pest Control"
 * Created by MistaCat 10/7/2018
 */
@Getter
public class NestBot {
    public static IDiscordClient client;
    public static CommandHub commands = new CommandHub();
    public static FeedbackHub feedback = new FeedbackHub();
    public static SuspensionHub suspensions = new SuspensionHub();
    public static RaidHub raidHub = new RaidHub();

    public static void main(String[] args) {

        client = new ClientBuilder().withToken("Bot token here!").build();
        client.getDispatcher().registerListener(new NestBot());
        client.getDispatcher().registerListener(commands);
        client.getDispatcher().registerListener(feedback);
        client.getDispatcher().registerListener(raidHub);

        client.login();
    }

    /**
     * Gets the pest control discord server.
     * @return
     */
    public static IGuild getGuild() {
        return client.getGuildByID(514788290809954305L);
    }

    @EventSubscriber
    public void onLogin(ReadyEvent evt) {
        registerCommands();
        BlacklistManager.setupBlacklist();
        Verification.setupVerification();

        Utils.sendConsoleDebug("Nest bot has started!");
    }

    private void registerCommands() {
        commands.add(new CommandBeginRaid());
        commands.add(new CommandStartRaid());
        commands.add(new CommandEndRaid());
        commands.add(new CommandAbortRaid());
        commands.add(new CommandHeadcount());
        commands.add(new CommandVerify());
        commands.add(new CommandSuspend());
        commands.add(new CommandUnsuspend());
        commands.add(new CommandBlacklist());
        commands.add(new CommandUnblacklist());
        commands.add(new CommandCommands());
    }
}

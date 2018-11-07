package mistacat.nestbot.commands;

import lombok.Getter;
import lombok.Setter;
import mistacat.nestbot.Rank;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

/**
 * The base for a nest bot command. Over-ride execute to do as you want. Be sure to add commands to command hub.
 * Created by MistaCat 10/7/2018
 */
@Getter @Setter
public abstract class Command {
    private String[] aliases;
    private Rank minRank;

    public abstract void execute(IMessage msg, String alias, String[] args);

    public EmbedObject getInfo() {
        EmbedBuilder msg = new EmbedBuilder();
        msg.withTitle("Failure!");
        msg.withDesc("This command has no information!");
        return msg.build();
    }
}

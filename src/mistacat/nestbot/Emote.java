package mistacat.nestbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;

/**
 * An enum of all the pest control emotes.
 * Created by MistaCat 10/10/2018
 */
@Getter @AllArgsConstructor
public enum Emote {
    NEST(":nest:", 438911027594264576L),
    NEST_KEY(":nestkey:", 410250309227642880L),
    QOT(":qot:", 438502758790987777L),
    PRIEST(":priesty:", 438502718798299168L),
    PALLY(":palbuff:", 438503234630451221L),
    WARRIOR(":warbuff:", 438503161209290752L);

    private String emoteName;
    private Long ID;

    public ReactionEmoji getEmote() {
        return ReactionEmoji.of(emoteName, ID);
    }

    public String display() {
        return "<" + emoteName + ID + ">";
    }
}

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
    NEST(":Nest:", 514800256077266945L),
    NEST_KEY(":NestKey:", 514800274242797578L),
    QOT(":QoT:", 514800353649098760L),
    PRIEST(":Priest:", 514800332467994639L),
    PALLY(":Paladin:", 514800298058055701L),
    WARRIOR(":Warrior:", 514800368899719168L);

    private String emoteName;
    private Long ID;

    public ReactionEmoji getEmote() {
        return ReactionEmoji.of(emoteName, ID);
    }

    public String display() {
        return "<" + emoteName + ID + ">";
    }
}

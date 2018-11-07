package mistacat.nestbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

/**
 * An enum that holds all the current Pest Control ranks
 * Created by MistaCAt 10/7/2018
 */
@Getter @AllArgsConstructor
public enum Rank {
    DEFAULT(null, false),
    ALMOST_RL(422614873772392448L, true),
    RL(406575952236380160L, true),
    SECURITY(438501150874927116L, true),
    OFFICER(406575543459512333L, true),
    HEAD_RL(470477690332381185L, true),
    ADMIN(406575140693082123L, true),
    OWNER(483503659771101207L, true);

    private Long ID;
    private boolean canSpeak;

    public IRole getRole() {
        return ID != null ? NestBot.getGuild().getRoleByID(ID) : null;
    }

    public boolean isAtLeast(Rank other) {
        return ordinal() >= other.ordinal();
    }

    /**
     * Gets the highest rank if a user has one. Default if they dont have one.
     * @param user
     * @return
     */
    public static Rank getHighestRank(IUser user) {
        for (IRole role : user.getRolesForGuild(NestBot.getGuild())) {
            for (int i = Rank.OWNER.ordinal(); i > 0; i--) {
                if (role == Rank.values()[i].getRole())
                    return Rank.values()[i];
            }
        }

        return Rank.DEFAULT;
    }
}

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
    ALMOST_RL(514789997262405645L, true),
    RL(514789718253109249L, true),
    SECURITY(514791076490379274L, true),
    OFFICER(514790850816114699L, true),
    HEAD_RL(514789673281912833L, true),
    ADMIN(514789410538127362L, true),
    OWNER(514789379311665152L, true);

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

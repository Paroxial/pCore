package land.pvp.core.player.rank;

import land.pvp.core.utils.message.CC;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
public enum Rank {
    OWNER("Owner", CC.D_RED),
    MANAGER("Manager", CC.RED + CC.I),
    DEV("Dev", CC.GRAY),
    ADMIN("Admin", CC.RED),
    SENIOR_MOD("SeniorMod", CC.BLUE + CC.I),
    MOD("Mod", CC.BLUE),
    TRIAL_MOD("TrialMod", CC.YELLOW),
    BUILDER("Builder", CC.GOLD),
    FAMOUS("Famous", CC.PURPLE + CC.I),
    YOUTUBER("YouTuber", CC.PURPLE),
    EXCLUSIVE("Exclusive", "%s✸%s", CC.PINK),
    VOTER("Voter", CC.PINK + "✔" + ChatColor.AQUA, CC.AQUA),
    MEMBER("Member", CC.GREEN, CC.GREEN);

    private final String name;
    private final String rawFormat;
    private final String format;
    private final String color;

    Rank(String name, String color) {
        this(name, CC.D_GRAY + "[%s" + name + CC.D_GRAY + "]%s" + " ", color);
    }

    Rank(String name, String rawFormat, String color) {
        this.name = name;
        this.rawFormat = rawFormat;
        this.format = String.format(rawFormat, color, color);
        this.color = color;
    }

    public static Rank getByName(String name) {
        for (Rank rank : values()) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    public void apply(Player player) {
        String coloredName = color + player.getName();

        player.setPlayerListName(coloredName);
        player.setDisplayName(coloredName);
    }
}

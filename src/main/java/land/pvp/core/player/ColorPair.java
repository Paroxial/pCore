package land.pvp.core.player;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum ColorPair {
    BLACK(ChatColor.DARK_GRAY, ChatColor.BLACK),
    DARK_GRAY(ChatColor.GRAY, ChatColor.DARK_GRAY),
    DARK_BLUE(ChatColor.BLUE, ChatColor.DARK_BLUE),
    DARK_GREEN(ChatColor.GREEN, ChatColor.DARK_GREEN),
    DARK_AQUA(ChatColor.AQUA, ChatColor.DARK_AQUA),
    DARK_RED(ChatColor.RED, ChatColor.DARK_RED),
    DARK_PURPLE(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE),
    GOLD(ChatColor.YELLOW, ChatColor.GOLD),
    GRAY(ChatColor.WHITE, ChatColor.GRAY),
    BLUE(ChatColor.DARK_BLUE, ChatColor.BLUE),
    GREEN(ChatColor.DARK_GREEN, ChatColor.GREEN),
    AQUA(ChatColor.DARK_AQUA, ChatColor.AQUA),
    RED(ChatColor.DARK_RED, ChatColor.RED),
    LIGHT_PURPLE(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE),
    YELLOW(ChatColor.GOLD, ChatColor.YELLOW),
    WHITE(ChatColor.GRAY, ChatColor.WHITE);

    private final String name;
    private final ChatColor primary;
    private final ChatColor secondary;

    ColorPair(ChatColor primary, ChatColor secondary) {
        this.primary = primary;
        this.secondary = secondary;
        this.name = secondary.name();
    }

    public static ColorPair getByName(String name) {
        for (ColorPair pair : values()) {
            if (name.equalsIgnoreCase(pair.name)) {
                return pair;
            }
        }

        return null;
    }
}

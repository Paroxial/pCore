package land.pvp.core.player.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@Setter
public class CustomColorPair {
    private ChatColor primary;
    private ChatColor secondary;

    public void apply(Player player) {
        if (secondary != null) {
            player.setDisplayName(secondary + player.getName());
        }
    }
}

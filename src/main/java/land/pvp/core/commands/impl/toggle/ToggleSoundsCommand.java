package land.pvp.core.commands.impl.toggle;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class ToggleSoundsCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public ToggleSoundsCommand(CorePlugin plugin) {
        super("togglesounds");
        this.plugin = plugin;
        setAliases("sounds", "ts");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean playingSounds = !profile.isPlayingSounds();

        profile.setPlayingSounds(playingSounds);
        player.sendMessage(playingSounds ? CC.GREEN + "Sounds enabled." : CC.RED + "Sounds disabled.");
    }
}

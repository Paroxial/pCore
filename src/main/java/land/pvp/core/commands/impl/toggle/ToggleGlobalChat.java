package land.pvp.core.commands.impl.toggle;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class ToggleGlobalChat extends PlayerCommand {
    private final CorePlugin plugin;

    public ToggleGlobalChat(CorePlugin plugin) {
        super("toggleglobalchat");
        this.plugin = plugin;
        setAliases("togglechat", "tgc");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean enabled = !profile.isGlobalChatEnabled();

        profile.setGlobalChatEnabled(enabled);
        player.sendMessage(enabled ? CC.GREEN + "Global chat enabled." : CC.RED + "Global chat disabled.");
    }
}

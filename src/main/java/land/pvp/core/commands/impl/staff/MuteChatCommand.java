package land.pvp.core.commands.impl.staff;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class MuteChatCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public MuteChatCommand(CorePlugin plugin) {
        super("mutechat", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean globalChatMuted = !plugin.getServerSettings().isGlobalChatMuted();

        plugin.getServerSettings().setGlobalChatMuted(globalChatMuted);
        plugin.getServer().broadcastMessage(globalChatMuted ? CC.RED + "Global chat has been muted by " + player.getName() + "."
                : CC.GREEN + "Global chat has been enabled by " + player.getName() + ".");
    }
}

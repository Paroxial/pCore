package land.pvp.core.commands.impl.toggle;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class ToggleMessagesCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public ToggleMessagesCommand(CorePlugin plugin) {
        super("togglemessages");
        this.plugin = plugin;
        setAliases("tpm");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean messaging = !profile.isMessaging();

        profile.setMessaging(messaging);
        player.sendMessage(messaging ? CC.GREEN + "Messages enabled." : CC.RED + "Messages disabled.");
    }
}

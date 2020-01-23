package land.pvp.core.commands.impl.staff;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {
    private final CorePlugin plugin;

    public VanishCommand(CorePlugin plugin) {
        super("vanish", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean vanished = !profile.isVanished();

        profile.setVanished(vanished);

        for (Player online : plugin.getServer().getOnlinePlayers()) {
            plugin.getStaffManager().hideVanishedStaffFromPlayer(online);
        }

        player.sendMessage(vanished ? CC.GREEN + "Poof, you vanished." : CC.RED + "You're visible again.");
    }
}

package land.pvp.core.commands.impl;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.message.Messages;
import org.bukkit.entity.Player;

public class IgnoreCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public IgnoreCommand(CorePlugin plugin) {
        super("ignore");
        this.plugin = plugin;
        setAliases("unignore");
        setUsage(CC.RED + "Usage: /ignore <player>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return;
        }

        if (target.getName().equals(player.getName())) {
            player.sendMessage(CC.RED + "You can't ignore yourself!");
            return;
        }

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (targetProfile.hasStaff()) {
            player.sendMessage(CC.RED + "You can't ignore a staff member. If this staff member is harrassing you " +
                    "or engaging in other abusive manners, please report this or contact a higher staff member.");
            return;
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile.hasPlayerIgnored(target.getUniqueId())) {
            profile.unignore(target.getUniqueId());
            player.sendMessage(CC.GREEN + "No longer ignoring " + target.getName() + ".");
        } else {
            profile.ignore(target.getUniqueId());
            player.sendMessage(CC.GREEN + "Now ignoring " + target.getName() + ".");
        }
    }
}

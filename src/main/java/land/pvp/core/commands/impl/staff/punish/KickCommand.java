package land.pvp.core.commands.impl.staff.punish;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.StringUtil;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.message.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends BaseCommand {
    private final CorePlugin plugin;

    public KickCommand(CorePlugin plugin) {
        super("kick", Rank.TRIAL_MOD);
        this.plugin = plugin;
        this.setUsage(CC.RED + "/kick <player> [reason] [-s]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND);
            return;
        }

        boolean silent;
        String reason;

        if (args.length < 2) {
            reason = "Misconduct";
            silent = false;
        } else {
            String builtArgs = StringUtil.buildString(args, 1).trim();
            silent = builtArgs.contains("-s");

            if (silent) {
                if (builtArgs.equals("-s")) {
                    reason = "Misconduct";
                } else {
                    reason = builtArgs.replace("-s", "");
                }
            } else {
                reason = builtArgs;
            }
        }

        target.kickPlayer(CC.RED + "You were kicked: " + reason);

        String msg = CC.GREEN + target.getName() + " was kicked by " + sender.getName() + ".";

        if (silent) {
            String silentMsg = CC.GRAY + "(Silent) " + msg;

            plugin.getStaffManager().messageStaff(silentMsg);
            plugin.getLogger().info(silentMsg);
        } else {
            plugin.getServer().broadcastMessage(msg);
        }
    }
}

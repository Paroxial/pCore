package land.pvp.core.commands.impl.staff;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.StringUtil;
import land.pvp.core.utils.message.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends BaseCommand {
    private final CorePlugin plugin;

    public BroadcastCommand(CorePlugin plugin) {
        super("broadcast", Rank.ADMIN);
        this.plugin = plugin;
        setAliases("bc");
        setUsage(CC.RED + "Usage: /broadcast <message> [-god]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return;
        }

        String message = CC.SECONDARY + "[Alert] " + CC.PRIMARY
                + ChatColor.translateAlternateColorCodes('&', StringUtil.buildString(args, 0)).trim();

        if (message.endsWith(" -god")) {
            message = message.substring(12, message.length() - 5).trim();
        }

        plugin.getServer().broadcastMessage(message);
    }
}

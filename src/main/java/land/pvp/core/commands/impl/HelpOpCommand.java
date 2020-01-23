package land.pvp.core.commands.impl;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.StringUtil;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.timer.Timer;
import org.bukkit.entity.Player;

public class HelpOpCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public HelpOpCommand(CorePlugin plugin) {
        super("helpop");
        this.plugin = plugin;
        setUsage(CC.RED + "/helpop <help message>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        Timer cooldownTimer = profile.getReportCooldownTimer();

        if (cooldownTimer.isActive()) {
            player.sendMessage(CC.RED + "You can't request assistance for another " + cooldownTimer.formattedExpiration() + ".");
            return;
        }

        String request = StringUtil.buildString(args, 0);

        plugin.getStaffManager().messageStaff(CC.RED + "\n(HelpOp) " + CC.SECONDARY + player.getName()
                + CC.PRIMARY + " requested assistance: " + CC.SECONDARY + request + CC.PRIMARY + ".\n ");

        player.sendMessage(CC.GREEN + "Request sent: " + CC.R + request);
    }
}

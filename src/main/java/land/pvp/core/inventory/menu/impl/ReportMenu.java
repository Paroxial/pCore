package land.pvp.core.inventory.menu.impl;

import land.pvp.core.CorePlugin;
import land.pvp.core.inventory.menu.Menu;
import land.pvp.core.inventory.menu.action.Action;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.item.ItemBuilder;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.timer.Timer;
import org.bukkit.Material;

public class ReportMenu extends Menu {
    private final CorePlugin plugin;

    public ReportMenu(CorePlugin plugin) {
        super(1, "Select a Report Reason");
        this.plugin = plugin;
    }

    private Action getAction(String reason) {
        return player -> {
            CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            Timer cooldownTimer = profile.getReportCooldownTimer();

            if (cooldownTimer.isActive()) {
                player.sendMessage(CC.RED + "You can't report a player for another " + cooldownTimer.formattedExpiration() + ".");
                player.closeInventory();
                return;
            }

            String targetName = profile.getReportingPlayerName();

            plugin.getStaffManager().messageStaff("");
            plugin.getStaffManager().messageStaff(CC.RED + "(Report) " + CC.SECONDARY + player.getName() + CC.PRIMARY
                    + " reported " + CC.SECONDARY + targetName + CC.PRIMARY + " for " + CC.SECONDARY + reason + CC.PRIMARY + ".");
            plugin.getStaffManager().messageStaff("");

            player.sendMessage(CC.GREEN + "Report sent for " + targetName + CC.GREEN + ": " + CC.R + reason);
            player.closeInventory();
        };
    }

    @Override
    public void setup() {
    }

    @Override
    public void update() {
        setActionableItem(1, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.PRIMARY + "Combat Cheats").build(), getAction("Combat Cheats"));
        setActionableItem(3, new ItemBuilder(Material.DIAMOND_BOOTS).name(CC.PRIMARY + "Movement Cheats").build(), getAction("Movement Cheats"));
        setActionableItem(5, new ItemBuilder(Material.PAPER).name(CC.PRIMARY + "Chat Violation").build(), getAction("Chat Violation"));
        setActionableItem(7, new ItemBuilder(Material.NETHER_STAR).name(CC.PRIMARY + "Assistance").build(), getAction("I need assistance related to this player"));
    }
}

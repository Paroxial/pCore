package land.pvp.core.commands.impl.staff.punish;

import land.pvp.core.CorePlugin;
import land.pvp.core.player.rank.Rank;

public class BanCommand extends PunishCommand {
    public BanCommand(CorePlugin plugin) {
        super(Rank.MOD, PunishType.BAN, plugin);
    }
}

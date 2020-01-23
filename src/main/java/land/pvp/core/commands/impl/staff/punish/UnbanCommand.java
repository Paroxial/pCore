package land.pvp.core.commands.impl.staff.punish;

import land.pvp.core.CorePlugin;
import land.pvp.core.player.rank.Rank;

public class UnbanCommand extends UnpunishCommand {
    public UnbanCommand(CorePlugin plugin) {
        super(Rank.SENIOR_MOD, PunishType.BAN, plugin);
    }
}

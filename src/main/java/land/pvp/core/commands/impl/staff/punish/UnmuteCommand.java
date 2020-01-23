package land.pvp.core.commands.impl.staff.punish;

import land.pvp.core.CorePlugin;
import land.pvp.core.player.rank.Rank;

public class UnmuteCommand extends UnpunishCommand {
    public UnmuteCommand(CorePlugin plugin) {
        super(Rank.MOD, PunishType.MUTE, plugin);
    }
}

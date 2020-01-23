package land.pvp.core.commands.impl.staff.punish;

import land.pvp.core.CorePlugin;
import land.pvp.core.player.rank.Rank;

public class MuteCommand extends PunishCommand {
    public MuteCommand(CorePlugin plugin) {
        super(Rank.TRIAL_MOD, PunishType.MUTE, plugin);
    }
}

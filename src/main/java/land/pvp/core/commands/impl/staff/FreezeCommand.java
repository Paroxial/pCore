package land.pvp.core.commands.impl.staff;

import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class FreezeCommand extends PlayerCommand {
    public FreezeCommand() {
        super("freeze", Rank.SENIOR_MOD);
        setAliases("screenshare", "ss");
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(CC.PRIMARY + "Nice meme.");
    }
}

package land.pvp.core.commands.impl.toggle;

import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class ToggleRecordingModeCommand extends PlayerCommand {
    public ToggleRecordingModeCommand() {
        super("togglerecordingmode", Rank.EXCLUSIVE);
        setAliases("togglerecording", "recording");
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean recording = !player.isRecording();

        player.setRecording(recording);
        player.sendMessage(recording ? CC.GREEN + "You are now in recording mode." : CC.RED + "You are no longer in recording mode.", true);
    }
}

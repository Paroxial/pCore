package land.pvp.core.task;

import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.time.TimeUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class ShutdownTask extends BukkitRunnable {
    private int shutdownSeconds;

    @Override
    public void run() {
        if (shutdownSeconds == 0) {
            Bukkit.shutdown();
        } else if (shutdownSeconds % 60 == 0 || shutdownSeconds == 30 || shutdownSeconds == 10 || shutdownSeconds <= 5) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(CC.D_RED + "[Alert] " + CC.RED + "The server is restarting in " + TimeUtil.formatTimeSeconds(shutdownSeconds) + ".");
            Bukkit.broadcastMessage("");
        }

        shutdownSeconds--;
    }
}

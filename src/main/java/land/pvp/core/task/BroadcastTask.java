package land.pvp.core.task;

import land.pvp.core.utils.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;

@RequiredArgsConstructor
public class BroadcastTask implements Runnable {
    private static final String[] MESSAGES = {
            CC.RED +CC.B+ "PvP Land is shutting down soon. Read here: https://twitter.com/PvP_Land/status/1015704134354460673"
//            "Butterfly/double clicking is discouraged and may get you banned.",
//            "Check out our website to view statistics, leaderboards, the forum, and more: " + CC.ACCENT + "http://pvp.land",
//            "Want a free rank? Vote for us on NameMC!\n" + CC.SECONDARY + "https://namemc.com/server/pvp.land",
//            "Follow us on Twitter for giveaways, updates, and more!\n" + CC.SECONDARY + "http://twitter.pvp.land",
//            "Join our Discord server!\n" + CC.SECONDARY + "http://discord.pvp.land",
//            "Get cool perks and help support us by getting a rank on the store!\n" + CC.SECONDARY + "https://store.pvp.land",
//            "Staff applications are open! Apply here: " + CC.ACCENT + "http://apply.pvp.land"
    };
    private final Server server;
    private int currentIndex = -1;

    @Override
    public void run() {
        if (++currentIndex >= MESSAGES.length) {
            currentIndex = 0;
        }

        String message = MESSAGES[currentIndex];

        server.broadcastMessage("");
        server.broadcastMessage(CC.SECONDARY + "[Land Bot] " + CC.PRIMARY + message);
        server.broadcastMessage("");
    }
}

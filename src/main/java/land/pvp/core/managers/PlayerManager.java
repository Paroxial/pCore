package land.pvp.core.managers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public class PlayerManager {
    private final Map<InetAddress, Integer> onlinePerIp = new HashMap<>();
    private final List<String> onlineNames = new ArrayList<>();

    public void addPlayer(Player player) {
        onlineNames.add(player.getName());

        InetAddress address = player.getAddress().getAddress();
        int count = onlinePerIp.getOrDefault(address, 0) + 1;

        onlinePerIp.put(address, count);
    }

    public void removePlayer(Player player) {
        onlineNames.remove(player.getName());

        InetAddress address = player.getAddress().getAddress();
        int count = onlinePerIp.getOrDefault(address, 0) - 1;

        if (count == 0) {
            onlinePerIp.remove(address);
        } else {
            onlinePerIp.put(address, count);
        }
    }

    public int getOnlineByIp(InetAddress address) {
        return onlinePerIp.getOrDefault(address, 0);
    }

    public boolean isNameOnline(String name) {
        return onlineNames.contains(name);
    }
}

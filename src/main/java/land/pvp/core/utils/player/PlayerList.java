package land.pvp.core.utils.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import land.pvp.core.CorePlugin;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerList {
    public static final String ORDERED_RANKS = String.join(", ", Arrays.stream(Rank.values())
            .sorted(Comparator.reverseOrder())
            .map(rank -> rank.getColor() + rank.getName() + CC.R)
            .collect(Collectors.toList()));
    private static final Comparator<Player> RANK_ORDER = (a, b) -> {
        CoreProfile profileA = CorePlugin.getInstance().getProfileManager().getProfile(a.getUniqueId());
        CoreProfile profileB = CorePlugin.getInstance().getProfileManager().getProfile(b.getUniqueId());
        return profileB.getRank().compareTo(profileA.getRank());
    };

    @Getter
    private final List<Player> onlinePlayers;

    public static PlayerList newList() {
        return new PlayerList(new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    public int size() {
        return onlinePlayers.size();
    }

    public PlayerList sortedByRank() {
        onlinePlayers.sort(RANK_ORDER);
        return this;
    }

    public String asColoredNames() {
        List<String> namesList = onlinePlayers.stream()
                .map(Player::getUniqueId)
                .map(CorePlugin.getInstance().getProfileManager()::getProfile)
                .map(profile -> profile.getRank().getColor() + profile.getName() + CC.R)
                .collect(Collectors.toList());
        return String.join(", ", namesList);
    }
}

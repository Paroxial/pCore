package land.pvp.core.managers;

import java.util.HashSet;
import java.util.Set;
import land.pvp.core.CorePlugin;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class StaffManager {
    @Getter
    private final Set<CoreProfile> cachedStaff = new HashSet<>();
    private final CorePlugin plugin;

    public void addCachedStaff(CoreProfile profile) {
        cachedStaff.add(profile);
    }

    public boolean isInStaffCache(CoreProfile profile) {
        return cachedStaff.contains(profile);
    }

    public void removeCachedStaff(CoreProfile profile) {
        cachedStaff.remove(profile);
    }

    public void messageStaff(String displayName, String msg) {
        String formattedMsg = CC.GREEN + "[Staff] " + displayName + CC.R + ": " + msg;
        messageStaff(formattedMsg);
    }

    public void messageStaff(Rank requiredRank, String msg) {
        for (CoreProfile profile : cachedStaff) {
            if (profile.hasRank(requiredRank)) {
                Player loopPlayer = plugin.getServer().getPlayer(profile.getId());

                if (loopPlayer != null && loopPlayer.isOnline()) {
                    loopPlayer.sendMessage(msg);
                }
            }
        }
    }

    public void messageStaffWithPrefix(String msg) {
        msg = CC.GREEN + "[Staff] " + msg;

        for (CoreProfile profile : cachedStaff) {
            Player loopPlayer = plugin.getServer().getPlayer(profile.getId());

            if (loopPlayer != null && loopPlayer.isOnline()) {
                loopPlayer.sendMessage(msg);
            }
        }
    }

    public void messageStaff(String msg) {
        for (CoreProfile profile : cachedStaff) {
            Player loopPlayer = plugin.getServer().getPlayer(profile.getId());

            if (loopPlayer != null && loopPlayer.isOnline()) {
                loopPlayer.sendMessage(msg);
            }
        }
    }

    public void hideVanishedStaffFromPlayer(Player player) {
        if (!plugin.getProfileManager().getProfile(player.getUniqueId()).hasStaff()) {
            for (CoreProfile profile : cachedStaff) {
                if (profile.isVanished()) {
                    Player loopPlayer = plugin.getServer().getPlayer(profile.getId());

                    if (loopPlayer != null && loopPlayer.isOnline()) {
                        player.hidePlayer(loopPlayer);
                    }
                }
            }
        }
    }
}

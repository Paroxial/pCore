package land.pvp.core.listeners;

import java.util.Iterator;
import land.pvp.core.CorePlugin;
import land.pvp.core.event.player.PlayerRankChangeEvent;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.CustomColorPair;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.server.ServerSettings;
import land.pvp.core.storage.database.MongoStorage;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.message.Messages;
import land.pvp.core.utils.time.TimeUtil;
import land.pvp.core.utils.timer.Timer;
import land.pvp.core.utils.web.WebUtil;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private static final String[] DISALLOWED_PERMISSIONS = {
            "bukkit.command.version", "bukkit.command.plugins", "bukkit.command.help", "bukkit.command.tps",
            "minecraft.command.tell", "minecraft.command.me", "minecraft.command.help"
    };
    private final CorePlugin plugin;

    private boolean isNotBanned(Document document, AsyncPlayerPreLoginEvent event) {
        if (document != null && document.getBoolean("banned") != null && document.getBoolean("banned")) {
            long expiry = document.getLong("ban_expiry");
            long difference = expiry - System.currentTimeMillis();

            if (expiry == -1L || difference > 0) {
                String formattedDifference = TimeUtil.formatTimeMillis(difference);
                String kickMessage = expiry == -1L ? Messages.BANNED_PERMANENTLY : String.format(Messages.BANNED_TEMPORARILY, formattedDifference);

                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getPlayerManager().isNameOnline(event.getName()) || plugin.getPlayerManager().getOnlineByIp(event.getAddress()) > 3) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "You're already online!");
        } else if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            MongoStorage storage = plugin.getMongoStorage();

            boolean notBannedById = isNotBanned(storage.getDocument("punished_ids", event.getUniqueId()), event);
            boolean notBannedByIp = isNotBanned(storage.getDocument("punished_addresses", event.getAddress().getHostAddress()), event);

            if (notBannedById && notBannedByIp) {
                CoreProfile profile = plugin.getProfileManager().createProfile(event.getName(), event.getUniqueId(), event.getAddress().getHostAddress());
                ServerSettings serverSettings = plugin.getServerSettings();

                if (serverSettings.getServerWhitelistMode().isProfileIneligible(profile)) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, serverSettings.getWhitelistMessage());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Messages.DATA_LOAD_FAIL);
            return;
        } else if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            plugin.getProfileManager().removeProfile(player.getUniqueId());
            return;
        }

        PermissionAttachment attachment = player.addAttachment(plugin);

        if (!profile.hasRank(Rank.ADMIN)) {
            for (String permission : DISALLOWED_PERMISSIONS) {
                attachment.setPermission(permission, false);
            }
        }

        Rank rank = profile.getRank();

        rank.apply(player);

        profile.getColorPair().apply(player);

        if (profile.hasStaff()) {
            plugin.getStaffManager().addCachedStaff(profile);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();

        plugin.getPlayerManager().addPlayer(player);

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        plugin.getStaffManager().hideVanishedStaffFromPlayer(player);

        if (profile.hasStaff()) {
            plugin.getStaffManager().messageStaffWithPrefix(profile.getChatFormat() + CC.PRIMARY + " joined the server.");
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }

            player.sendMessage("");
            player.sendMessage(CC.RED + CC.B + "PvP Land is shutting down soon. Read here: https://twitter.com/PvP_Land/status/1015704134354460673");
            player.sendMessage("");
        }, 20L);

        Rank rank = profile.getRank();

        if (rank == Rank.MEMBER || rank == Rank.VOTER) {
            WebUtil.getResponse(plugin, "https://api.namemc.com/server/pvp.land/votes?profile=" + player.getUniqueId(),
                    response -> {
                        switch (response) {
                            case "false":
                                if (rank == Rank.MEMBER) {
                                    player.sendMessage("");
                                    player.sendMessage(CC.PRIMARY + "Looks like you haven't voted for PvP Land!");
                                    player.sendMessage(CC.GREEN + "Vote here to get a free rank in-game: " + CC.SECONDARY + "https://namemc.com/server/pvp.land");
                                    player.sendMessage("");
                                } else {
                                    Rank newRank = Rank.MEMBER;
                                    profile.setRank(newRank);
                                    newRank.apply(player);
                                    player.sendMessage(CC.RED + "Your voter rank has been removed because you removed your vote! :(");
                                }
                                break;
                            case "true":
                                if (rank == Rank.MEMBER) {
                                    Rank newRank = Rank.VOTER;
                                    profile.setRank(newRank);
                                    newRank.apply(player);
                                    player.sendMessage(CC.GREEN + "Thanks for voting! You've been given the Voter rank.");
                                }
                                break;
                        }
                    }
            );
        }
    }

    private void onDisconnect(Player player) {
        plugin.getPlayerManager().removePlayer(player);

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        // in case disconnect is somehow called twice
        if (profile == null) {
            return;
        }

        if (profile.hasStaff()) {
            plugin.getStaffManager().removeCachedStaff(profile);
            plugin.getStaffManager().messageStaffWithPrefix(profile.getChatFormat() + CC.PRIMARY + " left the server.");
        }

        profile.save(true);
        plugin.getProfileManager().removeProfile(player.getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);

        onDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        onDisconnect(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        String msg = event.getMessage();

        if (!profile.hasStaff()) {
            if (plugin.getServerSettings().isGlobalChatMuted()) {
                event.setCancelled(true);
                player.sendMessage(CC.RED + "Global chat is currently muted.");
                return;
            } else if (profile.isMuted()) {
                event.setCancelled(true);

                if (profile.isTemporarilyMuted()) {
                    player.sendMessage(CC.RED + "You're muted for another " + profile.getTimeMuted() + ".");
                } else if (profile.isPermanentlyMuted()) {
                    player.sendMessage(CC.RED + "You're permanently muted.");
                }
                return;
            } else if (plugin.getServerSettings().getSlowChatTime() != -1) {
                long lastChatTime = profile.getLastChatTime();
                int slowChatTime = plugin.getServerSettings().getSlowChatTime();
                long sum = lastChatTime + (slowChatTime * 1000);

                if (lastChatTime != 0 && sum > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    String diff = TimeUtil.formatTimeMillis(sum - System.currentTimeMillis());
                    player.sendMessage(CC.RED + "Slow chat is currently enabled. You can talk again in " + diff + ".");
                    return;
                }
            }

            Timer timer = profile.getChatCooldownTimer();

            if (timer.isActive()) {
                event.setCancelled(true);
                player.sendMessage(CC.RED + "You can't chat for another " + timer.formattedExpiration() + ".");
                return;
            }

        } else if (profile.isInStaffChat()) {
            event.setCancelled(true);
            plugin.getStaffManager().messageStaff(profile.getChatFormat(), msg);
            return;
        }

        if (plugin.getFilter().isFiltered(msg)) {
            if (profile.hasStaff()) {
                player.sendMessage(CC.RED + "That would have been filtered.");
            } else {
                event.setCancelled(true);

                String formattedMessage = profile.getChatFormat() + CC.R + ": " + msg;

                plugin.getStaffManager().messageStaff(CC.RED + "(Filtered) " + formattedMessage);
                player.sendMessage(formattedMessage);
                return;
            }
        }

        Iterator<Player> recipients = event.getRecipients().iterator();

        while (recipients.hasNext()) {
            Player recipient = recipients.next();
            CoreProfile recipientProfile = plugin.getProfileManager().getProfile(recipient.getUniqueId());

            if (recipientProfile == null) {
                continue;
            }

            if (recipientProfile.hasPlayerIgnored(player.getUniqueId())
                    || (!recipientProfile.isGlobalChatEnabled() && (!profile.hasStaff() || recipientProfile.hasStaff()))) {
                recipients.remove();
            } else if (recipient != player) {
                String[] words = msg.split(" ");
                boolean found = false;

                StringBuilder newMessage = new StringBuilder();

                for (String word : words) {
                    if (recipient.getName().equalsIgnoreCase(word) && !found) {
                        newMessage.append(CC.PINK).append(CC.I).append(word).append(CC.R).append(" ");
                        found = true;
                    } else {
                        newMessage.append(word).append(" ");
                    }
                }

                if (!found) {
                    continue;
                }

                if (recipientProfile.isPlayingSounds()) {
                    recipient.playSound(recipient.getLocation(), Sound.LEVEL_UP, 1.0F, 2.0F);
                }

                String mentionMessage = profile.getChatFormat() + CC.R + ": " + newMessage.toString();

                recipient.sendMessage(mentionMessage);
                recipient.sendMessage(player.getDisplayName() + CC.PRIMARY + " mentioned you!");

                recipients.remove();
            }
        }

        event.setFormat(profile.getChatFormat() + CC.R + ": %2$s");

        profile.updateLastChatTime();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile.hasStaff()) {
            return;
        }

        Timer timer = profile.getCommandCooldownTimer();

        if (timer.isActive()) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can't use commands for another " + timer.formattedExpiration() + ".");
        }
    }

    @EventHandler
    public void onRankChange(PlayerRankChangeEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = event.getProfile();
        Rank newRank = event.getNewRank();

        profile.setRank(newRank);

        if (profile.hasStaff()) {
            if (!plugin.getStaffManager().isInStaffCache(profile)) {
                plugin.getStaffManager().addCachedStaff(profile);
            }
        } else if (plugin.getStaffManager().isInStaffCache(profile)) {
            plugin.getStaffManager().removeCachedStaff(profile);
        }

        newRank.apply(player);

        if (newRank == Rank.MEMBER) {
            profile.setColorPair(new CustomColorPair());
        } else {
            profile.getColorPair().apply(player);
        }

        player.sendMessage(CC.GREEN + "You now have the " + newRank.getColor() + newRank.getName() + CC.GREEN + " rank!");
    }
}

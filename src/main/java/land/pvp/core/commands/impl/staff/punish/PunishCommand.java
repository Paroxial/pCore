package land.pvp.core.commands.impl.staff.punish;

import java.util.List;
import java.util.UUID;
import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.event.BanEvent;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.storage.database.MongoRequest;
import land.pvp.core.utils.ProfileUtil;
import land.pvp.core.utils.StringUtil;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.message.Messages;
import land.pvp.core.utils.time.TimeUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishCommand extends BaseCommand {
    private final PunishType type;
    private final CorePlugin plugin;

    PunishCommand(Rank requiredRank, PunishType type, CorePlugin plugin) {
        super(type.getName(), requiredRank);
        this.type = type;
        this.plugin = plugin;
        this.setUsage(CC.RED + "Usage: /" + getName() + " <player> [time] [reason] [-s]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        boolean silent;
        String reason;
        long time;

        if (args.length < 2) {
            reason = type.getDefaultMessage();
            time = -1L;
            silent = false;
        } else {
            String builtArgs = StringUtil.buildString(args, 1).trim();

            time = TimeUtil.parseTime(args[1]);

            if (time != -1) {
                builtArgs = builtArgs.substring(args[1].length());
            }

            silent = builtArgs.endsWith("-s");

            if (silent) {
                if (builtArgs.equals("-s")) {
                    reason = type.getDefaultMessage();
                } else {
                    reason = builtArgs.substring(0, builtArgs.length() - 2).trim();
                }
            } else {
                reason = builtArgs;
            }
        }

        boolean permanent = time == -1L;
        long expiryTime = permanent ? -1L : System.currentTimeMillis() + time;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID targetId;
            String targetName;
            Player targetPlayer = plugin.getServer().getPlayer(args[0]);
            CoreProfile targetProfile = null;

            if (targetPlayer == null) {
                ProfileUtil.MojangProfile profile = ProfileUtil.lookupProfile(args[0]);

                if (profile == null) {
                    sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return;
                } else {
                    targetId = profile.getId();
                    targetName = profile.getName();
                }
            } else {
                targetId = targetPlayer.getUniqueId();
                targetName = targetPlayer.getName();
                targetProfile = plugin.getProfileManager().getProfile(targetId);

                if (type == PunishType.MUTE) {
                    targetProfile.setMuteExpiryTime(expiryTime);
                }
            }

            if (sender instanceof Player && targetProfile != null) {
                Player player = (Player) sender;
                CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

                if (!profile.hasRank(targetProfile.getRank())) {
                    player.sendMessage(CC.RED + "You can't punish someone with a higher rank than your own.");
                    return;
                }
            }

            if (type == PunishType.BAN) {
                BanEvent event = new BanEvent(sender, targetId);

                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
            }

            punish(sender.getName(), targetId, reason, expiryTime);

            String diff = TimeUtil.formatTimeMillis(expiryTime - System.currentTimeMillis());
            String msg = permanent ? CC.GREEN + targetName + " was permanently " + type.getPastTense() + " by " + sender.getName() + "."
                    : CC.GREEN + targetName + " was temporarily " + type.getPastTense() + " by " + sender.getName()
                    + " for " + diff + ".";

            if (silent) {
                String silentMsg = CC.GRAY + "(Silent) " + msg;

                plugin.getStaffManager().messageStaff(silentMsg);
                plugin.getLogger().info(silentMsg);
            } else {
                plugin.getServer().broadcastMessage(msg);
            }

            if (type == PunishType.BAN && targetPlayer != null && targetPlayer.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> targetPlayer.kickPlayer(permanent
                        ? Messages.BANNED_PERMANENTLY
                        : String.format(Messages.BANNED_TEMPORARILY, diff)));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void punish(String punisher, UUID punished, String reason, long expiry) {
        Document document = plugin.getMongoStorage().getDocument("players", punished);

        if (document != null) {
            List<String> knownAddresses = (List<String>) document.get("known_addresses");

            if (knownAddresses != null) {
                for (String address : knownAddresses) {
                    plugin.getMongoStorage().getOrCreateDocument("punished_addresses", address, (doc, found) ->
                            MongoRequest.newRequest("punished_addresses", address)
                                    .put(type.getPastTense(), true)
                                    .put(type.getName() + "_expiry", expiry)
                                    .put(type.getName() + "_reason", reason)
                                    .put(type.getName() + "_punisher", punisher)
                                    .run());
                }
            }
        }

        plugin.getMongoStorage().getOrCreateDocument("punished_ids", punished, (doc, found) ->
                MongoRequest.newRequest("punished_ids", punished)
                        .put(type.getPastTense(), true)
                        .put(type.getName() + "_expiry", expiry)
                        .put(type.getName() + "_reason", reason)
                        .put(type.getName() + "_punisher", punisher)
                        .run());
    }
}

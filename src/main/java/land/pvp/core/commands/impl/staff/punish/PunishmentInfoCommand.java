package land.pvp.core.commands.impl.staff.punish;

import java.util.UUID;
import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.ProfileUtil;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.message.Messages;
import land.pvp.core.utils.time.TimeUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static land.pvp.core.utils.StringUtil.IP_REGEX;

// TODO: cleanup
public class PunishmentInfoCommand extends BaseCommand {
    private final CorePlugin plugin;

    public PunishmentInfoCommand(CorePlugin plugin) {
        super("punishmentinfo", Rank.MOD);
        this.plugin = plugin;
        this.setAliases("baninfo", "muteinfo", "playerinfo");
        this.setUsage(CC.RED + "/punishmentinfo <player|ip>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String arg = args[0];
            Document document;

            if (IP_REGEX.matcher(arg).matches()) {
                document = plugin.getMongoStorage().getDocument("punished_addresses", arg);
            } else {
                UUID id;
                String name;
                Player player = plugin.getServer().getPlayer(arg);

                if (player == null) {
                    ProfileUtil.MojangProfile profile = ProfileUtil.lookupProfile(arg);

                    if (profile == null) {
                        sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                        return;
                    }

                    id = profile.getId();
                    name = profile.getName();
                } else {
                    id = player.getUniqueId();
                    name = player.getName();
                }

                arg = name;
                document = plugin.getMongoStorage().getDocument("punished_ids", id);
            }

            if (document == null) {
                sender.sendMessage(CC.RED + "No punishment info was found for " + arg + ".");
                return;
            }

            sender.sendMessage(CC.PRIMARY + "Punishment Information for " + arg);

            Boolean punished = document.getBoolean("banned");
            boolean actuallyPunished = punished != null && punished && (document.getLong("ban_expiry") == -1L || System.currentTimeMillis() < document.getLong("ban_expiry"));
            sender.sendMessage(CC.PRIMARY + "Banned: " + CC.SECONDARY + actuallyPunished);

            if (actuallyPunished) {
                long expiry = document.getLong("ban_expiry");
                String punisher = document.getString("ban_punisher");
                String reason = document.getString("ban_reason");

                sender.sendMessage(CC.PRIMARY + "Expiry: " + CC.SECONDARY
                        + (expiry == -1L ? "never" : TimeUtil.formatTimeMillis(expiry - System.currentTimeMillis())));
                sender.sendMessage(CC.PRIMARY + "Punisher: " + CC.SECONDARY + punisher);
                sender.sendMessage(CC.PRIMARY + "Reason: " + CC.SECONDARY + reason);
            }

            sender.sendMessage("");

            punished = document.getBoolean("muted");
            actuallyPunished = punished != null && punished && (document.getLong("mute_expiry") == -1L || System.currentTimeMillis() < document.getLong("mute_expiry"));

            sender.sendMessage(CC.PRIMARY + "Muted: " + CC.SECONDARY + actuallyPunished);

            if (actuallyPunished) {
                long expiry = document.getLong("mute_expiry");
                String punisher = document.getString("mute_punisher");
                String reason = document.getString("mute_reason");

                sender.sendMessage(CC.PRIMARY + "Expiry: " + CC.SECONDARY
                        + (expiry == -1L ? "never" : TimeUtil.formatTimeMillis(expiry - System.currentTimeMillis())));
                sender.sendMessage(CC.PRIMARY + "Punisher: " + CC.SECONDARY + punisher);
                sender.sendMessage(CC.PRIMARY + "Reason: " + CC.SECONDARY + reason);
            }
        });
    }
}

package land.pvp.core.commands.impl.staff;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.event.player.PlayerRankChangeEvent;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.storage.database.MongoRequest;
import land.pvp.core.utils.ProfileUtil;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.message.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand extends BaseCommand {
    private final CorePlugin plugin;

    public RankCommand(CorePlugin plugin) {
        super("rank", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /rank <player> <rank>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(usageMessage);
            return;
        }

        Rank rank = Rank.getByName(args[1]);

        if (rank == null) {
            sender.sendMessage(CC.RED + "Rank not found.");
            return;
        }

        if (sender instanceof Player) {
            CoreProfile playerProfile = plugin.getProfileManager().getProfile(((Player) sender).getUniqueId());

            if (!playerProfile.hasRank(rank)) {
                sender.sendMessage(CC.RED + "You can't give ranks higher than your own.");
                return;
            }
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                ProfileUtil.MojangProfile profile = ProfileUtil.lookupProfile(args[0]);

                if (profile != null && plugin.getMongoStorage().getDocument("players", profile.getId()) != null) {
                    MongoRequest.newRequest("players", profile.getId())
                            .put("rank_name", rank.getName())
                            .run();

                    sender.sendMessage(CC.GREEN + "Set " + profile.getName() + "'s rank to "
                            + rank.getColor() + rank.getName() + CC.GREEN + ".");
                } else {
                    sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                }
            });
        } else {
            CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

            plugin.getServer().getPluginManager().callEvent(new PlayerRankChangeEvent(target, targetProfile, rank));
            sender.sendMessage(CC.GREEN + "Set " + target.getName() + "'s rank to "
                    + rank.getColor() + rank.getName() + CC.GREEN + ".");
        }
    }
}

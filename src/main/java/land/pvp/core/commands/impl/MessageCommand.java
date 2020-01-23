package land.pvp.core.commands.impl;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.event.player.PlayerMessageEvent;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.StringUtil;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.message.Messages;
import org.bukkit.entity.Player;

public class MessageCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public MessageCommand(CorePlugin plugin) {
        super("message");
        this.plugin = plugin;
        setAliases("msg", "m", "whisper", "w", "tell", "t");
        setUsage(CC.RED + "Usage: /message <player> <message>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usageMessage);
            return;
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile.isMuted()) {
            if (profile.isTemporarilyMuted()) {
                player.sendMessage(CC.RED + "You're muted for another " + profile.getTimeMuted() + ".");
            } else if (profile.isPermanentlyMuted()) {
                player.sendMessage(CC.RED + "You're permanently muted.");
            }
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return;
        }

        if (target.isRecording()) {
            player.sendMessage(CC.RED + "That player is in recording mode and can't see your messages!");
            return;
        }

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (targetProfile.hasPlayerIgnored(player.getUniqueId())) {
            player.sendMessage(CC.RED + "That player is ignoring you!");
            return;
        }

        plugin.getServer().getPluginManager().callEvent(new PlayerMessageEvent(player, target, StringUtil.buildString(args, 1)));
    }
}

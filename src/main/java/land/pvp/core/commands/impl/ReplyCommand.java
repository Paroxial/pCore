package land.pvp.core.commands.impl;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.event.player.PlayerMessageEvent;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.utils.StringUtil;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class ReplyCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public ReplyCommand(CorePlugin plugin) {
        super("reply");
        this.plugin = plugin;
        setAliases("r");
        setUsage(CC.RED + "Usage: /reply <message>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
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

        Player target = plugin.getServer().getPlayer(profile.getConverser());

        if (target == null) {
            player.sendMessage(CC.RED + "You are not in a conversation.");
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

        plugin.getServer().getPluginManager().callEvent(new PlayerMessageEvent(player, target, StringUtil.buildString(args, 0)));
    }
}

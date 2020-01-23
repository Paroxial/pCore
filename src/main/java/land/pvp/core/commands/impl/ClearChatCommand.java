package land.pvp.core.commands.impl;

import java.util.Collections;
import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {
    private static final String BLANK_MESSAGE = String.join("", Collections.nCopies(150, "§8 §8 §1 §3 §3 §7 §8 §r\n"));
    private final CorePlugin plugin;

    public ClearChatCommand(CorePlugin plugin) {
        super("clearchat", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setAliases("cc");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

            if (!profile.hasStaff()) {
                player.sendMessage(BLANK_MESSAGE);
            }
        }

        plugin.getServer().broadcastMessage(CC.GREEN + "The chat was cleared by " + sender.getName() + ".");
        sender.sendMessage(CC.YELLOW + "Don't worry, staff can still see cleared messages.");
    }
}

package land.pvp.core.commands.impl.staff;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.PlayerCommand;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.StringUtil;
import land.pvp.core.utils.message.CC;
import org.bukkit.entity.Player;

public class StaffChatCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public StaffChatCommand(CorePlugin plugin) {
        super("staffchat", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setAliases("sc");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (args.length == 0) {
            boolean inStaffChat = !profile.isInStaffChat();

            profile.setInStaffChat(inStaffChat);

            player.sendMessage(inStaffChat ? CC.GREEN + "You are now in staff chat." : CC.RED + "You are no longer in staff chat.");
        } else {
            String message = StringUtil.buildString(args, 0);

            plugin.getStaffManager().messageStaff(profile.getChatFormat(), message);
        }
    }
}

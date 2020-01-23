package land.pvp.core.commands.impl;

import land.pvp.core.commands.BaseCommand;
import land.pvp.core.utils.player.PlayerList;
import org.bukkit.command.CommandSender;

public class ListCommand extends BaseCommand {
    public ListCommand() {
        super("list");
        setAliases("online", "players", "who");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        PlayerList onlinePlayerList = PlayerList.newList().sortedByRank();

        sender.sendMessage(onlinePlayerList.asColoredNames() + " (" + onlinePlayerList.size() + ")");
        sender.sendMessage(PlayerList.ORDERED_RANKS);
    }
}

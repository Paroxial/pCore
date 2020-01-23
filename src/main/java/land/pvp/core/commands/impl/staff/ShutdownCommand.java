package land.pvp.core.commands.impl.staff;

import land.pvp.core.CorePlugin;
import land.pvp.core.commands.BaseCommand;
import land.pvp.core.event.server.ServerShutdownCancelEvent;
import land.pvp.core.event.server.ServerShutdownScheduleEvent;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.task.ShutdownTask;
import land.pvp.core.utils.NumberUtil;
import land.pvp.core.utils.message.CC;
import org.bukkit.command.CommandSender;

public class ShutdownCommand extends BaseCommand {
    private final CorePlugin plugin;

    public ShutdownCommand(CorePlugin plugin) {
        super("shutdown", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /shutdown <seconds|cancel>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        String arg = args[0];

        if (arg.equals("cancel")) {
            ShutdownTask task = plugin.getServerSettings().getShutdownTask();

            if (task == null) {
                sender.sendMessage(CC.RED + "There is no shutdown in progress.");
            } else {
                plugin.getServer().getPluginManager().callEvent(new ServerShutdownCancelEvent());

                task.cancel();
                plugin.getServerSettings().setShutdownTask(null);
                plugin.getServer().broadcastMessage(CC.GREEN + "The shutdown in progress has been cancelled by " + sender.getName() + ".");
            }
            return;
        }

        Integer seconds = NumberUtil.getInteger(arg);

        if (seconds == null) {
            sender.sendMessage(usageMessage);
        } else {
            if (seconds >= 5 && seconds <= 300) {
                plugin.getServer().getPluginManager().callEvent(new ServerShutdownScheduleEvent());

                ShutdownTask task = new ShutdownTask(seconds);

                plugin.getServerSettings().setShutdownTask(task);
                task.runTaskTimer(plugin, 0L, 20L);
            } else {
                sender.sendMessage(CC.RED + "Please enter a time between 5 and 300 seconds.");
            }
        }
    }
}

package land.pvp.core;

import land.pvp.core.commands.impl.ClearChatCommand;
import land.pvp.core.commands.impl.ColorCommand;
import land.pvp.core.commands.impl.HelpOpCommand;
import land.pvp.core.commands.impl.IgnoreCommand;
import land.pvp.core.commands.impl.ListCommand;
import land.pvp.core.commands.impl.MessageCommand;
import land.pvp.core.commands.impl.PingCommand;
import land.pvp.core.commands.impl.ReplyCommand;
import land.pvp.core.commands.impl.ReportCommand;
import land.pvp.core.commands.impl.staff.BroadcastCommand;
import land.pvp.core.commands.impl.staff.FreezeCommand;
import land.pvp.core.commands.impl.staff.GameModeCommand;
import land.pvp.core.commands.impl.staff.MuteChatCommand;
import land.pvp.core.commands.impl.staff.RankCommand;
import land.pvp.core.commands.impl.staff.ShutdownCommand;
import land.pvp.core.commands.impl.staff.SlowChatCommand;
import land.pvp.core.commands.impl.staff.StaffChatCommand;
import land.pvp.core.commands.impl.staff.TeleportCommand;
import land.pvp.core.commands.impl.staff.VanishCommand;
import land.pvp.core.commands.impl.staff.WhitelistCommand;
import land.pvp.core.commands.impl.staff.punish.BanCommand;
import land.pvp.core.commands.impl.staff.punish.KickCommand;
import land.pvp.core.commands.impl.staff.punish.MuteCommand;
import land.pvp.core.commands.impl.staff.punish.PunishmentInfoCommand;
import land.pvp.core.commands.impl.staff.punish.UnbanCommand;
import land.pvp.core.commands.impl.staff.punish.UnmuteCommand;
import land.pvp.core.commands.impl.toggle.ToggleGlobalChat;
import land.pvp.core.commands.impl.toggle.ToggleMessagesCommand;
import land.pvp.core.commands.impl.toggle.ToggleRecordingModeCommand;
import land.pvp.core.commands.impl.toggle.ToggleSoundsCommand;
import land.pvp.core.listeners.InventoryListener;
import land.pvp.core.listeners.MessageListener;
import land.pvp.core.listeners.PlayerListener;
import land.pvp.core.managers.MenuManager;
import land.pvp.core.managers.PlayerManager;
import land.pvp.core.managers.ProfileManager;
import land.pvp.core.managers.StaffManager;
import land.pvp.core.server.ServerSettings;
import land.pvp.core.server.filter.Filter;
import land.pvp.core.storage.database.MongoStorage;
import land.pvp.core.storage.flatfile.Config;
import land.pvp.core.task.BroadcastTask;
import land.pvp.core.utils.message.CC;
import land.pvp.core.utils.structure.Cuboid;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CorePlugin extends JavaPlugin {
    @Getter
    private static CorePlugin instance;

    private ServerSettings serverSettings;
    private Filter filter;
    private MongoStorage mongoStorage;

    private ProfileManager profileManager;
    private StaffManager staffManager;
    private PlayerManager playerManager;
    private MenuManager menuManager;

    private static void registerSerializableClass(Class<?> clazz) {
        if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
            ConfigurationSerialization.registerClass(serializable);
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        registerSerializableClass(Cuboid.class);

        serverSettings = new ServerSettings(this);
        filter = new Filter();

        Config coreConfig = serverSettings.getCoreConfig();

        mongoStorage = new MongoStorage(coreConfig.getString("database.host"), coreConfig.getString("database.password"));

        profileManager = new ProfileManager();
        staffManager = new StaffManager(this);
        playerManager = new PlayerManager();
        menuManager = new MenuManager(this);

        registerCommands(
                new BroadcastCommand(this),
                new ClearChatCommand(this),
                new IgnoreCommand(this),
                new ListCommand(),
                new MessageCommand(this),
                new RankCommand(this),
                new ReplyCommand(this),
                new StaffChatCommand(this),
                new TeleportCommand(this),
                new ToggleMessagesCommand(this),
                new ToggleGlobalChat(this),
                new ToggleSoundsCommand(this),
                new ToggleRecordingModeCommand(),
                new VanishCommand(this),
                new ReportCommand(this),
                new HelpOpCommand(this),
                new PingCommand(),
                new BanCommand(this),
                new MuteCommand(this),
                new UnbanCommand(this),
                new UnmuteCommand(this),
                new KickCommand(this),
                new PunishmentInfoCommand(this),
                new MuteChatCommand(this),
                new SlowChatCommand(this),
                new GameModeCommand(),
                new ColorCommand(this),
                new ShutdownCommand(this),
                new FreezeCommand(),
                new WhitelistCommand(this)
        );
        registerListeners(
                new PlayerListener(this),
                new MessageListener(this),
                new InventoryListener(this)
        );

        getServer().getScheduler().runTaskTimerAsynchronously(this, new BroadcastTask(getServer()), 20 * 30L, 20 * 30L);
    }

    @Override
    public void onDisable() {
        profileManager.saveProfiles();
        serverSettings.saveConfig();

        for (Player player : getServer().getOnlinePlayers()) {
            player.kickPlayer(CC.RED + "The server is restarting.");
        }
    }
}

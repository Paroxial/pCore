package land.pvp.core.server;

import com.google.common.collect.ImmutableMap;
import land.pvp.core.CorePlugin;
import land.pvp.core.storage.flatfile.Config;
import land.pvp.core.task.ShutdownTask;
import land.pvp.core.utils.message.CC;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ServerSettings {
    private final Config coreConfig;
    private final String whitelistMessage;
    @Setter
    private WhitelistMode serverWhitelistMode;
    @Setter
    private ShutdownTask shutdownTask;
    @Setter
    private boolean globalChatMuted;
    @Setter
    private int slowChatTime = -1;

    public ServerSettings(CorePlugin plugin) {
        this.coreConfig = new Config(plugin, "core");

        coreConfig.addDefaults(ImmutableMap.<String, Object>builder()
                .put("database.host", "localhost")
                .put("database.password", "password")
                .put("whitelist.mode", WhitelistMode.NONE.name())
                .put("whitelist.message", CC.RED + "The server is whitelisted. Come back later!")
                .build());
        coreConfig.copyDefaults();

        this.serverWhitelistMode = WhitelistMode.valueOf(coreConfig.getString("whitelist.mode"));
        this.whitelistMessage = coreConfig.getString("whitelist.message");
    }

    public void saveConfig() {
        coreConfig.save();
    }
}

package land.pvp.core.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import land.pvp.core.CorePlugin;
import land.pvp.core.player.rank.CustomColorPair;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.storage.database.MongoRequest;
import land.pvp.core.utils.time.TimeUtil;
import land.pvp.core.utils.timer.Timer;
import land.pvp.core.utils.timer.impl.DoubleTimer;
import land.pvp.core.utils.timer.impl.IntegerTimer;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;

@Setter
public class CoreProfile {
    private final List<UUID> ignored = new ArrayList<>();
    private final List<String> knownAddresses = new ArrayList<>();
    @Getter
    private final String name;
    @Getter
    private final UUID id;
    @Getter
    private final Timer commandCooldownTimer = new DoubleTimer(1);
    @Getter
    private final Timer reportCooldownTimer = new IntegerTimer(TimeUnit.SECONDS, 60);
    private Timer chatCooldownTimer;
    @Getter
    private Rank rank = Rank.MEMBER;
    @Getter
    private CustomColorPair colorPair = new CustomColorPair();
    @Getter
    private UUID converser;
    @Getter
    private String reportingPlayerName;
    private long muteExpiryTime = -2;
    @Getter
    private boolean playingSounds = true;
    @Getter
    private boolean messaging = true;
    @Getter
    private boolean globalChatEnabled = true;
    @Getter
    private boolean inStaffChat;
    @Getter
    private boolean vanished;
    @Getter
    private long lastChatTime;

    @SuppressWarnings("unchecked")
    public CoreProfile(String name, UUID id, String address) {
        this.name = name;
        this.id = id;
        this.knownAddresses.add(address);

        CorePlugin.getInstance().getMongoStorage().getOrCreateDocument("players", id, (document, exists) -> {
            if (exists) {
                this.inStaffChat = document.getBoolean("staff_chat_enabled", inStaffChat);
                this.messaging = document.getBoolean("messaging_enabled", messaging);
                this.playingSounds = document.getBoolean("playing_sounds", playingSounds);

                String rankName = document.get("rank_name", rank.getName());
                Rank rank = Rank.getByName(rankName);

                if (rank != null) {
                    this.rank = rank;
                }

                String colorPrimaryName = document.getString("color_primary");
                String colorSecondaryName = document.getString("color_secondary");

                if (colorPrimaryName != null) {
                    this.colorPair.setPrimary(ChatColor.valueOf(colorPrimaryName));
                }

                if (colorSecondaryName != null) {
                    this.colorPair.setSecondary(ChatColor.valueOf(colorSecondaryName));
                }

                List<UUID> ignored = (List<UUID>) document.get("ignored_ids");

                if (ignored != null) {
                    this.ignored.addAll(ignored);
                }

                List<String> knownAddresses = (List<String>) document.get("known_addresses");

                if (knownAddresses != null) {
                    for (String knownAddress : knownAddresses) {
                        if (knownAddress.equals(address)) {
                            continue;
                        }

                        this.knownAddresses.add(knownAddress);
                    }
                }
            }

            save(false);
        });

        Document punishDoc = CorePlugin.getInstance().getMongoStorage().getDocument("punished_ids", id);

        if (!loadMuteData(punishDoc)) {
            for (String knownAddress : knownAddresses) {
                punishDoc = CorePlugin.getInstance().getMongoStorage().getDocument("punished_addresses", knownAddress);
                loadMuteData(punishDoc);
            }
        }
    }

    private boolean loadMuteData(Document document) {
        if (document != null) {
            Boolean muted = document.getBoolean("muted");

            if (muted != null) {
                long muteExpiryTime = document.getLong("mute_expiry");

                if (muted && (muteExpiryTime == -1 || System.currentTimeMillis() < muteExpiryTime)) {
                    this.muteExpiryTime = muteExpiryTime;
                    return true;
                }
            }
        }

        return false;
    }

    public void save(boolean async) {
        MongoRequest request = MongoRequest.newRequest("players", id)
                .put("name", name)
                .put("staff_chat_enabled", inStaffChat)
                .put("messaging_enabled", messaging)
                .put("playing_sounds", playingSounds)
                .put("rank_name", rank.getName())
                .put("ignored_ids", ignored)
                .put("known_addresses", knownAddresses);

        ChatColor primary = colorPair.getPrimary();
        ChatColor secondary = colorPair.getSecondary();

        request.put("color_primary", primary == null ? null : primary.name());
        request.put("color_secondary", secondary == null ? null : secondary.name());

        if (async) {
            CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), request::run);
        } else {
            request.run();
        }
    }

    public Timer getChatCooldownTimer() {
        if (chatCooldownTimer == null) {
            if (isDonor()) {
                chatCooldownTimer = new DoubleTimer(1);
            } else {
                chatCooldownTimer = new DoubleTimer(3);
            }
        }

        return chatCooldownTimer;
    }

    public String getChatFormat() {
        String rankColor = rank.getColor();
        String primary = colorPair.getPrimary() == null ? rankColor : colorPair.getPrimary().toString();
        String secondary = colorPair.getSecondary() == null ? rankColor : colorPair.getSecondary().toString();

        return String.format(rank.getRawFormat(), primary, secondary) + name;
    }

    public void updateLastChatTime() {
        lastChatTime = System.currentTimeMillis();
    }

    public boolean isMuted() {
        return isTemporarilyMuted() || isPermanentlyMuted();
    }

    public boolean isTemporarilyMuted() {
        return System.currentTimeMillis() < muteExpiryTime;
    }

    public boolean isPermanentlyMuted() {
        return muteExpiryTime == -1;
    }

    public String getTimeMuted() {
        return TimeUtil.formatTimeMillis(muteExpiryTime - System.currentTimeMillis());
    }

    public boolean hasRank(Rank requiredRank) {
        return rank.ordinal() <= requiredRank.ordinal();
    }

    public boolean hasStaff() {
        return hasRank(Rank.TRIAL_MOD);
    }

    public boolean hasDonor() {
        return hasRank(Rank.EXCLUSIVE);
    }

    public boolean isDonor() {
        return rank == Rank.EXCLUSIVE;
    }

    public void ignore(UUID id) {
        ignored.add(id);
    }

    public void unignore(UUID id) {
        ignored.remove(id);
    }

    public boolean hasPlayerIgnored(UUID id) {
        return ignored.contains(id);
    }
}

package land.pvp.core.commands.impl.staff.punish;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PunishType {
    BAN("ban", "banned", "Unfair Advantage"),
    MUTE("mute", "muted", "Misconduct");

    private final String name;
    private final String pastTense;
    private final String defaultMessage;
}

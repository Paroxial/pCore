package land.pvp.core.utils.timer.impl;

import java.util.concurrent.TimeUnit;
import land.pvp.core.utils.time.TimeUtil;
import land.pvp.core.utils.timer.AbstractTimer;

public class IntegerTimer extends AbstractTimer {
    public IntegerTimer(TimeUnit unit, int amount) {
        super(unit, amount);
    }

    @Override
    public String formattedExpiration() {
        return TimeUtil.formatTimeMillis(expiry - System.currentTimeMillis());
    }
}

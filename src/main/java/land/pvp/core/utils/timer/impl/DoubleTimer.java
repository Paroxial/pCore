package land.pvp.core.utils.timer.impl;

import java.util.concurrent.TimeUnit;
import land.pvp.core.utils.timer.AbstractTimer;

public class DoubleTimer extends AbstractTimer {
    public DoubleTimer(int seconds) {
        super(TimeUnit.SECONDS, seconds);
    }

    @Override
    public String formattedExpiration() {
        double seconds = (expiry - System.currentTimeMillis()) / 1000.0;
        return String.format("%.1f seconds", seconds);
    }
}

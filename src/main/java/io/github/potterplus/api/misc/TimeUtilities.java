package io.github.potterplus.api.misc;

import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimeUtilities {

    public static final String DEF_PATTERN = "MM/dd/yyyy hh:mm a";

    public static Date getDate(long unix) {
        return new Date(unix);
    }

    public static String format(String format, long unix) {
        return new SimpleDateFormat(format).format(getDate(unix));
    }

    public static String format(long unix) {
        return format(DEF_PATTERN, unix);
    }

    public static String prettyDuration(Date then) {
        return prettyDuration(new Date(), then);
    }

    public static String prettyDuration(Date now, Date then) {
        PrettyTime pt = new PrettyTime(now);
        List<Duration> d = pt.calculatePreciseDuration(then);

        return pt.formatDuration(d);
    }

    public static String prettyTime(Date date) {
        PrettyTime pt = new PrettyTime();

        return pt.format(date);
    }

    public static String prettyTime(long unix) {
        return prettyTime(getDate(unix));
    }
}

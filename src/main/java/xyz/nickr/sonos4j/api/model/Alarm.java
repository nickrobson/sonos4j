package xyz.nickr.sonos4j.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * @author Nick Robson
 */
@Data
@Wither
@AllArgsConstructor
public class Alarm {

    private final long id;
    private final String startTime;
    private final String duration;
    private final String recurrence;
    private final boolean enabled;
    private final String roomUUID;
    private final String programURI;
    private final String programMetaData;
    private final String playMode;
    private final int volume;
    private final boolean includeLinkedZones;

    public boolean isOn(AlarmDay day) {
        if (recurrence.equals("DAILY"))
            return true;
        if (recurrence.startsWith("ON_"))
            return recurrence.contains(String.valueOf(day.ordinal()));
        if (recurrence.equals("ONCE")) {
            Calendar now = Calendar.getInstance();
            String[] spl = startTime.split(":");
            int[] nowTime = { now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND) };
            int[] startTime = { Integer.parseInt(spl[0]), Integer.parseInt(spl[1]), Integer.parseInt(spl[2]) };
            int nowDay = now.get(Calendar.FRIDAY) - 1;
            int nextDay = (nowDay + 1) % 7;
            for (int i = 0; i < 2; i++) {
                if (startTime[i] < nowTime[i])
                    return day.ordinal() == nextDay;
                if (startTime[i] > nowTime[i])
                    return day.ordinal() == nowDay;
            }
            return day.ordinal() == nowDay;
        }
        return false;
    }

    public enum AlarmDay {

        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY

    }

    public static final Pattern RECURRENCE_PATTERN = Pattern.compile("DAILY|ONCE|ON_0?1?2?3?4?5?6?");

}

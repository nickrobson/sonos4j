package xyz.nickr.sonos4j.api.model.alarm;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Wither;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * @author Nick Robson
 */
@Data
@Wither
public class Alarm {

    public static final Pattern RECURRENCE_PATTERN = Pattern.compile("DAILY|ONCE|ON_0?1?2?3?4?5?6?");

    private final long id;
    private final String startTime;
    private final String duration;
    private final String recurrence;
    private final boolean enabled;
    private final String roomUUID;
    private final String programURI;
    private final String programMetaData;
    private final AlarmPlayMode playMode;
    private final int volume;
    private final boolean includeLinkedZones;

    public Alarm(long id, String startTime, String duration, String recurrence, boolean enabled, String roomUUID, String programURI, String programMetaData, AlarmPlayMode playMode, int volume, boolean includeLinkedZones){
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
        this.recurrence = recurrence;
        this.enabled = enabled;
        this.roomUUID = roomUUID;
        this.programURI = programURI;
        this.programMetaData = programMetaData;
        this.playMode = playMode;
        this.volume = volume;
        this.includeLinkedZones = includeLinkedZones;

        if (!RECURRENCE_PATTERN.matcher(recurrence).matches() || recurrence.equals("ON_"))
            throw new IllegalArgumentException("invalid recurrence: '" + recurrence + "'");
    }

    public AlarmDay[] getActiveDays() {
        List<AlarmDay> days = new ArrayList<>(7);
        for (AlarmDay day : AlarmDay.values()) {
            if (isOn(day)) {
                days.add(day);
            }
        }
        return days.toArray(new AlarmDay[0]);
    }

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

}

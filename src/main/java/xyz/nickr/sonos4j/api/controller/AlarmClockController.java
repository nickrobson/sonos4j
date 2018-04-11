package xyz.nickr.sonos4j.api.controller;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.exception.AlarmAlreadyExistsException;
import xyz.nickr.sonos4j.api.exception.SonosException;
import xyz.nickr.sonos4j.api.model.alarm.Alarm;
import xyz.nickr.sonos4j.api.model.alarm.AlarmPlayMode;
import xyz.nickr.sonos4j.api.model.service.ServiceRoute;

/**
 * @author Nick Robson
 */
@AllArgsConstructor
public class AlarmClockController {

    private final Speaker speaker;

    public List<Alarm> getAlarms() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "ListAlarms");

        Map<String, Object> result = route.request();
        Document doc = Util.parseDocument(result.get("CurrentAlarmList").toString());

        List<Alarm> alarms = new LinkedList<>();

        for (Element el : Util.cast(Util.getChildList(doc.getDocumentElement()), Element.class)) {
            long id = Long.parseLong(el.getAttribute("ID"));
            String startTime = el.getAttribute("StartTime");
            String duration = el.getAttribute("Duration");
            String recurrence = el.getAttribute("Recurrence");
            boolean enabled = "1".equals(el.getAttribute("Enabled"));
            String roomUUID = el.getAttribute("RoomUUID");
            String programURI = el.getAttribute("ProgramURI");
            String programMetaData = el.getAttribute("ProgramMetaData");
            AlarmPlayMode playMode = AlarmPlayMode.valueOf(el.getAttribute("PlayMode"));
            int volume = Integer.parseInt(el.getAttribute("Volume"));
            boolean includeLinkedZones = "1".equals(el.getAttribute("IncludeLinkedZones"));

            alarms.add(new Alarm(id, startTime, duration, recurrence, enabled, roomUUID, programURI, programMetaData, playMode, volume, includeLinkedZones));
        }

        return alarms;
    }

    public Alarm createAlarm(Alarm alarm) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "CreateAlarm");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("StartLocalTime", alarm.getStartTime()));
        vars.add(new AbstractMap.SimpleEntry<>("Duration", alarm.getDuration()));
        vars.add(new AbstractMap.SimpleEntry<>("Recurrence", alarm.getRecurrence()));
        vars.add(new AbstractMap.SimpleEntry<>("Enabled", alarm.isEnabled()));
        vars.add(new AbstractMap.SimpleEntry<>("RoomUUID", alarm.getRoomUUID()));
        vars.add(new AbstractMap.SimpleEntry<>("ProgramURI", alarm.getProgramURI()));
        vars.add(new AbstractMap.SimpleEntry<>("ProgramMetaData", alarm.getProgramMetaData()));
        vars.add(new AbstractMap.SimpleEntry<>("PlayMode", alarm.getPlayMode()));
        vars.add(new AbstractMap.SimpleEntry<>("Volume", alarm.getVolume()));
        vars.add(new AbstractMap.SimpleEntry<>("IncludeLinkedZones", alarm.isIncludeLinkedZones()));

        try {
            Map<String, Object> result = route.request(vars);
            long id = Long.parseLong(result.get("AssignedID").toString());

            return alarm.withId(id);
        } catch (SonosException ex) {
            throw new AlarmAlreadyExistsException(speaker, alarm);
        }
    }

    public void deleteAlarm(long id) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "DestroyAlarm");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("ID", id));

        route.request(vars);
    }

    public void deleteAlarm(Alarm alarm) {
        deleteAlarm(alarm.getId());
    }

    public void updateAlarm(long id, Alarm alarm) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "UpdateAlarm");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("ID", id));
        vars.add(new AbstractMap.SimpleEntry<>("StartLocalTime", alarm.getStartTime()));
        vars.add(new AbstractMap.SimpleEntry<>("Duration", alarm.getDuration()));
        vars.add(new AbstractMap.SimpleEntry<>("Recurrence", alarm.getRecurrence()));
        vars.add(new AbstractMap.SimpleEntry<>("Enabled", alarm.isEnabled()));
        vars.add(new AbstractMap.SimpleEntry<>("RoomUUID", alarm.getRoomUUID()));
        vars.add(new AbstractMap.SimpleEntry<>("ProgramURI", alarm.getProgramURI()));
        vars.add(new AbstractMap.SimpleEntry<>("ProgramMetaData", alarm.getProgramMetaData()));
        vars.add(new AbstractMap.SimpleEntry<>("PlayMode", alarm.getPlayMode()));
        vars.add(new AbstractMap.SimpleEntry<>("Volume", alarm.getVolume()));
        vars.add(new AbstractMap.SimpleEntry<>("IncludeLinkedZones", alarm.isIncludeLinkedZones()));

        route.request(vars);
    }

    public String getAlarmListVersion() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "ListAlarms");

        Map<String, Object> result = route.request();

        return result.get("CurrentAlarmListVersion").toString();
    }

    public String getTimeFormat() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetFormat");

        Map<String, Object> result = route.request();

        return result.get("CurrentTimeFormat").toString();
    }

    public String getDateFormat() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetFormat");

        Map<String, Object> result = route.request();

        return result.get("CurrentDateFormat").toString();
    }

    public void setTimeFormat(String timeFormat) {
        setFormat(timeFormat, getDateFormat());
    }

    public void setDateFormat(String dateFormat) {
        setFormat(getTimeFormat(), dateFormat);
    }

    public void setFormat(String timeFormat, String dateFormat) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetFormat");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("DesiredTimeFormat", timeFormat));
        vars.add(new AbstractMap.SimpleEntry<>("DesiredDateFormat", dateFormat));

        route.request(vars);
    }

    public String getDailyIndexRefreshTime() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetDailyIndexRefreshTime");

        Map<String, Object> result = route.request();

        return result.get("CurrentDailyIndexRefreshTime").toString();
    }

    public void setDailyIndexRefreshTime(String refreshTime) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetDailyIndexRefreshTime");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("DesiredDailyIndexRefreshTime", refreshTime));

        route.request(vars);
    }

    public String getHouseholdTimeAtStamp(String timeStamp) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetHouseholdTimeAtStamp");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("TimeStamp", timeStamp));

        Map<String, Object> result = route.request(vars);

        return result.get("HouseholdUTCTime").toString();
    }

    public String getTimeServer() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeServer");

        Map<String, Object> result = route.request();

        return result.get("CurrentTimeServer").toString();
    }

    public void setTimeServer(String timeServer) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetTimeServer");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("DesiredTimeServer", timeServer));

        route.request(vars);
    }

    public String getTimeZoneRule(int index) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZoneRule");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("Index", index));

        Map<String, Object> result = route.request(vars);

        return result.get("TimeZone").toString();
    }

    public int getTimeZoneIndex() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZone");

        Map<String, Object> result = route.request();

        return Integer.parseInt(result.get("Index").toString());
    }

    public boolean isTimeZoneAutoAdjustDST() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZone");

        Map<String, Object> result = route.request();

        return (boolean) result.get("AutoAdjustDst");
    }

    public String getTimeZone() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZoneAndRule");

        Map<String, Object> result = route.request();

        return result.get("CurrentTimeZone").toString();
    }

    public void setTimeZone(int index, boolean autoAdjustDST) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetTimeZone");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("Index", index));
        vars.add(new AbstractMap.SimpleEntry<>("AutoAdjustDst", autoAdjustDST));

        route.request(vars);
    }

    public String getCurrentUTCTime() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow");

        Map<String, Object> result = route.request();

        return result.get("CurrentUTCTime").toString();
    }

    public String getCurrentLocalTime() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow");

        Map<String, Object> result = route.request();

        return result.get("CurrentLocalTime").toString();
    }

    public String getCurrentTimeZone() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow");

        Map<String, Object> result = route.request();

        return result.get("CurrentTimeZone").toString();
    }

    public long getCurrentTimeGeneration() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow");

        Map<String, Object> result = route.request();

        return Long.parseLong(result.get("CurrentTimeGeneration").toString());
    }

    public void setTimeNow(String time, String timeZone) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetTimeNow");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("DesiredTime", time));
        vars.add(new AbstractMap.SimpleEntry<>("TimeZoneForDesiredTime", timeZone));

        route.request(vars);
    }

}

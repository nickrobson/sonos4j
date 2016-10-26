package xyz.nickr.sonos4j.api.controller;

import lombok.AllArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.model.Alarm;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.service.ServiceRoute;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
@AllArgsConstructor
public class AlarmClockController {

    private final Speaker speaker;

    public List<Alarm> getAlarms() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "ListAlarms", true);

        Map<String, Object> vars = new HashMap<>();

        Map<String, Object> result = route.request(speaker, vars);
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
            String playMode = el.getAttribute("PlayMode");
            int volume = Integer.parseInt(el.getAttribute("Volume"));
            boolean includeLinkedZones = "1".equals(el.getAttribute("IncludeLinkedZones"));

            alarms.add(new Alarm(id, startTime, duration, recurrence, enabled, roomUUID, programURI, programMetaData, playMode, volume, includeLinkedZones));
        }

        return alarms;
    }

    public Alarm createAlarm(Alarm alarm) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "CreateAlarm", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("StartLocalTime", alarm.getStartTime());
        vars.put("Duration", alarm.getDuration());
        vars.put("Recurrence", alarm.getRecurrence());
        vars.put("Enabled", alarm.isEnabled());
        vars.put("RoomUUID", alarm.getRoomUUID());
        vars.put("ProgramURI", alarm.getProgramURI());
        vars.put("ProgramMetaData", alarm.getProgramMetaData());
        vars.put("PlayMode", alarm.getPlayMode());
        vars.put("Volume", alarm.getVolume());
        vars.put("IncludeLinkedZones", alarm.isIncludeLinkedZones());

        Map<String, Object> result = route.request(speaker, vars);
        long id = Long.parseLong(result.get("AssignedID").toString());

        return alarm.withId(id);
    }

    public void updateAlarm(long id, Alarm alarm) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "UpdateAlarm", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("ID", id);
        vars.put("StartLocalTime", alarm.getStartTime());
        vars.put("Duration", alarm.getDuration());
        vars.put("Recurrence", alarm.getRecurrence());
        vars.put("Enabled", alarm.isEnabled());
        vars.put("RoomUUID", alarm.getRoomUUID());
        vars.put("ProgramURI", alarm.getProgramURI());
        vars.put("ProgramMetaData", alarm.getProgramMetaData());
        vars.put("PlayMode", alarm.getPlayMode());
        vars.put("Volume", alarm.getVolume());
        vars.put("IncludeLinkedZones", alarm.isIncludeLinkedZones());

        route.request(speaker, vars);
    }

    public String getAlarmListVersion() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "ListAlarms", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentAlarmListVersion").toString();
    }

    public String getTimeFormat() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetFormat", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentTimeFormat").toString();
    }

    public String getDateFormat() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetFormat", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentDateFormat").toString();
    }

    public void setTimeFormat(String timeFormat) {
        setFormat(timeFormat, getDateFormat());
    }

    public void setDateFormat(String dateFormat) {
        setFormat(getTimeFormat(), dateFormat);
    }

    public void setFormat(String timeFormat, String dateFormat) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetFormat", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("DesiredTimeFormat", timeFormat);
        vars.put("DesiredDateFormat", dateFormat);

        route.request(speaker, vars);
    }

    public String getDailyIndexRefreshTime() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetDailyIndexRefreshTime", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentDailyIndexRefreshTime").toString();
    }

    public void setDailyIndexRefreshTime(String refreshTime) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetDailyIndexRefreshTime", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("DesiredDailyIndexRefreshTime", refreshTime);

        route.request(speaker, vars);
    }

    public String getHouseholdTimeAtStamp(String timeStamp) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetHouseholdTimeAtStamp", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("TimeStamp", timeStamp);

        Map<String, Object> result = route.request(speaker, vars);

        return result.get("HouseholdUTCTime").toString();
    }

    public String getTimeServer() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeServer", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentTimeServer").toString();
    }

    public void setTimeServer(String timeServer) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetTimeServer", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("DesiredTimeServer", timeServer);

        route.request(speaker, vars);
    }

    public String getTimeZoneRule(int index) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZoneRule", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("Index", index);

        Map<String, Object> result = route.request(speaker, vars);

        return result.get("TimeZone").toString();
    }

    public String getTimeZone() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZoneRuleAndRule", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentTimeZone").toString();
    }

    public void setTimeZone(int index, boolean autoAdjustDST) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZoneRuleAndRule", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("Index", index);
        vars.put("AutoAdjustDst", autoAdjustDST);

        route.request(speaker, vars);
    }

    public int getTimeZoneIndex() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZoneRule", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return Integer.parseInt(result.get("Index").toString());
    }

    public boolean isTimeZoneAutoAdjustDST() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeZoneRule", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return (boolean) result.get("AutoAdjustDst");
    }

    public String getCurrentUTCTime() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentUTCTime").toString();
    }

    public String getCurrentLocalTime() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentLocalTime").toString();
    }

    public String getCurrentTimeZone() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("CurrentTimeZone").toString();
    }

    public long getCurrentTimeGeneration() {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "GetTimeNow", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return Long.parseLong(result.get("CurrentTimeGeneration").toString());
    }

    public void setTimeNow(String time, String timeZone) {
        ServiceRoute route = speaker.getRoute("/AlarmClock/Control", "SetTimeNow", true);

        Map<String, Object> vars = new HashMap<>();
        vars.put("DesiredTime", time);
        vars.put("TimeZoneForDesiredTime", timeZone);

        route.request(speaker, vars);
    }

}

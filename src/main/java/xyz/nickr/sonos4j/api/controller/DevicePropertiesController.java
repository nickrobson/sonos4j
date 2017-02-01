package xyz.nickr.sonos4j.api.controller;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.service.ServiceRoute;

/**
 * @author Nick Robson
 */
public class DevicePropertiesController {

    private final Speaker speaker;

    public DevicePropertiesController(Speaker speaker) {
        this.speaker = speaker;
    }

    public String getHouseholdId() {
        ServiceRoute route = speaker.getRoute("/DeviceProperties/Control", "GetHouseholdID");

        Map<String, Object> result = route.request();

        return result.get("CurrentHouseholdID").toString();
    }

    public boolean isLEDEnabled() {
        ServiceRoute route = speaker.getRoute("/DeviceProperties/Control", "GetLEDState");

        Map<String, Object> result = route.request();

        return "On".equals(result.get("CurrentLEDState"));
    }

    public void setLEDEnabled(boolean enabled) {
        ServiceRoute route = speaker.getRoute("/DeviceProperties/Control", "SetLEDState");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("DesiredLEDState", enabled ? "On" : "Off"));

        route.request(vars);
    }

}

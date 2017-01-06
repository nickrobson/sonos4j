package xyz.nickr.sonos4j.api.controller;

import lombok.AllArgsConstructor;
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
public class MusicServicesController {

    private final Speaker speaker;

    public String getSessionId(long serviceId, String username) {
        ServiceRoute route = speaker.getRoute("/MusicServices/Control", "GetSessionId", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);

        return result.get("SessionId").toString();
    }

    public List<String> getAvailableServiceDescriptors() {
        ServiceRoute route = speaker.getRoute("/MusicServices/Control", "ListAvailableServices", true);

        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> result = route.request(speaker, vars);
        for (Map.Entry<String, Object> e : result.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        return new LinkedList<>();
    }

    public void updateAvailableServices() {
        ServiceRoute route = speaker.getRoute("/MusicServices/Control", "UpdateAvailableServices", true);

        Map<String, Object> vars = new HashMap<>();
        route.request(speaker, vars);
    }

}

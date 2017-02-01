package xyz.nickr.sonos4j.api.controller;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.media.MusicService;
import xyz.nickr.sonos4j.api.model.service.ServiceRoute;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Robson
 */
public class MusicServicesController {

    private final Speaker speaker;
    private Map<String, MusicService> musicServices;

    public MusicServicesController(Speaker speaker) {
        this.speaker = speaker;
    }

    public String getSessionId(long serviceId, String username) {
        ServiceRoute route = speaker.getRoute("/MusicServices/Control", "GetSessionId");

        Map<String, Object> result = route.request();

        return result.get("SessionId").toString();
    }

    public void updateAvailableServices() {
        ServiceRoute route = speaker.getRoute("/MusicServices/Control", "UpdateAvailableServices");

        route.request();
    }

    private void loadMusicServices() {
        ServiceRoute route = speaker.getRoute("/MusicServices/Control", "ListAvailableServices");

        Map<String, Object> result = route.request();

        Map<String, MusicService> musicServices = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Document doc = Util.parseDocument(result.get("AvailableServiceDescriptorList").toString());

        List<Element> serviceElements = Util.cast(Util.getChildList(doc.getDocumentElement()), Element.class);

        for (Element serviceElement : serviceElements) {
            try {
                MusicService service = MusicService.parse(speaker, serviceElement);
                if (service != null)
                    musicServices.put(service.getName(), service);
            } catch (Exception ex) {
                System.err.println("Error while parsing element: " + Util.toString(serviceElement));
                ex.printStackTrace();
            }
        }

        this.musicServices = musicServices;
    }

    public Map<String, MusicService> getMusicServicesMap() {
        if (musicServices == null)
            loadMusicServices();
        return Collections.unmodifiableMap(musicServices);
    }

    public Set<String> getMusicServices() {
        return getMusicServicesMap().keySet();
    }

    public Optional<MusicService> getMusicService(String name) {
        return Optional.ofNullable(getMusicServicesMap().get(name));
    }

    public Optional<MusicService> getMusicServiceById(int id) {
        for (MusicService service : getMusicServicesMap().values())
            if (service.getId() == id)
                return Optional.of(service);
        return Optional.empty();
    }

    public Optional<MusicService> getMusicServiceByType(int serviceType) {
        for (MusicService service : getMusicServicesMap().values())
            if (service.getServiceType() == serviceType)
                return Optional.of(service);
        return Optional.empty();
    }

}

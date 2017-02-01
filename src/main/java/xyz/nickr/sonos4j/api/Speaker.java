package xyz.nickr.sonos4j.api;

import java.util.AbstractMap;
import java.util.LinkedList;
import lombok.AccessLevel;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.controller.AccountsController;
import xyz.nickr.sonos4j.api.controller.AlarmClockController;
import xyz.nickr.sonos4j.api.controller.DevicePropertiesController;
import xyz.nickr.sonos4j.api.controller.MusicServicesController;
import xyz.nickr.sonos4j.api.controller.SystemPropertiesController;
import xyz.nickr.sonos4j.api.exception.RouteMissingException;
import xyz.nickr.sonos4j.api.model.DeviceDescription;
import xyz.nickr.sonos4j.api.model.DeviceList;
import xyz.nickr.sonos4j.api.model.ServiceList;
import xyz.nickr.sonos4j.api.model.media.CurrentTrack;
import xyz.nickr.sonos4j.api.model.media.Track;
import xyz.nickr.sonos4j.api.model.service.ServiceRoute;
import xyz.nickr.sonos4j.api.model.service.ServiceSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Getter
public class Speaker {

    private final String ip;

    @Getter(AccessLevel.NONE)
    private final String url;

    private DeviceDescription description;

    @Getter(lazy = true)
    private final AlarmClockController alarmClockController = new AlarmClockController(this);

    @Getter(lazy = true)
    private final MusicServicesController musicServicesController = new MusicServicesController(this);

    @Getter(lazy = true)
    private final AccountsController accountsController = new AccountsController(this);

    @Getter(lazy = true)
    private final DevicePropertiesController devicePropertiesController = new DevicePropertiesController(this);

    @Getter(lazy = true)
    private final SystemPropertiesController systemPropertiesController = new SystemPropertiesController(this);

    public Speaker(String ip) {
        this.ip = ip;
        this.url = "http://" + ip + ":1400";
    }

    public void load() {
        getDescription();
    }

    public String getURL() {
        return url;
    }

    public String getRoomName() {
        return getDescription().getDevice().getRoomName();
    }

    public DeviceDescription getDescription() {
        if (description == null) {
            description = new DeviceDescription(this);
        }
        return description;
    }

    public ServiceList.Service getService(String endpoint) {
        for (ServiceList.Service service : getDescription().getDevice().getServiceList().getServices()) {
            if (endpoint.equals(service.getControlUrl())) {
                return service;
            }
        }
        for (DeviceList.Device device : getDescription().getDevice().getDeviceList().getDevices()) {
            ServiceList serviceList = device.getServiceList();
            for (ServiceList.Service service : serviceList.getServices()) {
                if (endpoint.equals(service.getControlUrl())) {
                    return service;
                }
            }
        }
        return null;
    }

    public ServiceRoute getRoute(String endpoint, String name) {
        ServiceList.Service service = getService(endpoint);
        if (service == null)
            return null;
        ServiceSchema schema = service.getSchema().load(this);
        ServiceRoute route = schema.getRoute(name);
        if (route == null) {
            throw new RouteMissingException(this, endpoint, name);
        }
        return route;
    }

    public CurrentTrack getCurrentTrack() {
        ServiceRoute route = getRoute("/MediaRenderer/AVTransport/Control", "GetPositionInfo");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("InstanceID", 0));

        Map<String, Object> result = route.request(vars);

        String trackMetadata = result.get("TrackMetaData").toString();
        if (trackMetadata.isEmpty())
            return null;

        Document meta = Util.parseDocument(trackMetadata);
        Element element = (Element) meta.getDocumentElement().getFirstChild();

        return new CurrentTrack((long) result.get("Track"), Track.parse(element));
    }

    public void getMediaInfo() {
        ServiceRoute route = getRoute("/MediaRenderer/AVTransport/Control", "GetMediaInfo");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();

        vars.add(new AbstractMap.SimpleEntry<>("InstanceID", 0));

        Map<String, Object> result = route.request(vars);

        for (Map.Entry<String, Object> e : result.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }

    public List<Track> getQueue() {
        return getQueue(0, 50);
    }

    public List<Track> getQueue(int start, int maxItems) {
        ServiceRoute route = getRoute("/MediaServer/ContentDirectory/Control", "Browse");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();

        vars.add(new AbstractMap.SimpleEntry<>("ObjectID", "Q:0"));
        vars.add(new AbstractMap.SimpleEntry<>("BrowseFlag", "BrowseDirectChildren"));
        vars.add(new AbstractMap.SimpleEntry<>("Filter", "dc:title,res,dc:creator,upnp:artist,upnp:album,upnp:albumArtURI"));
        vars.add(new AbstractMap.SimpleEntry<>("SortCriteria", ""));
        vars.add(new AbstractMap.SimpleEntry<>("StartingIndex", start));
        vars.add(new AbstractMap.SimpleEntry<>("RequestedCount", maxItems));

        String result = route.request(vars).get("Result").toString();
        Document response = Util.parseDocument(result);

        List<Track> queue = new ArrayList<>();
        for (Element element : Util.cast(Util.getChildList(response.getDocumentElement()), Element.class)) {
            try {
                queue.add(Track.parse(element));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return queue;
    }

}

package xyz.nickr.jsonos.api.speaker;

import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.jsonos.Util;
import xyz.nickr.jsonos.api.media.CurrentTrack;
import xyz.nickr.jsonos.api.media.Track;
import xyz.nickr.jsonos.api.speaker.service.ServiceRoute;
import xyz.nickr.jsonos.api.speaker.service.ServiceRouteDirection;
import xyz.nickr.jsonos.api.speaker.service.ServiceSchema;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nick Robson
 */
public class Speaker {

    public static final String SOAP_TEMPLATE = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body>%s</s:Body></s:Envelope>";
    public static final String GET_QUEUE_BODY_TEMPLATE = "<u:Browse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\"><ObjectID>Q:0</ObjectID><BrowseFlag>BrowseDirectChildren</BrowseFlag><Filter>dc:title,res,dc:creator,upnp:artist,upnp:album,upnp:albumArtURI</Filter><StartingIndex>%s</StartingIndex><RequestedCount>%s</RequestedCount><SortCriteria></SortCriteria></u:Browse>";

    @Getter
    private final String ip;

    private final String url;

    private DeviceDescription description;

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
        return schema.getRoute(name);
    }

    public CurrentTrack getCurrentTrack() {
        ServiceRoute route = getRoute("/MediaRenderer/AVTransport/Control", "GetPositionInfo");

        if (route == null) {
            throw new RuntimeException("No get position info route!");
        }

        Map<String, Object> vars = new HashMap<>();

        vars.put("InstanceID", 0);

        Map<String, Object> result = route.request(this, vars);

        Document meta = Util.parseDocument(result.get("TrackMetaData").toString());
        Element element = (Element) meta.getDocumentElement().getFirstChild();

        return new CurrentTrack((long) result.get("Track"), Track.parse(element));
    }

    public void getMediaInfo() {
        ServiceRoute route = getRoute("/MediaRenderer/AVTransport/Control", "GetMediaInfo");

        if (route == null) {
            throw new RuntimeException("No get media info route!");
        }

        Map<String, Object> vars = new HashMap<>();

        vars.put("InstanceID", 0);

        Map<String, Object> result = route.request(this, vars);

        result.forEach((var, obj) -> System.out.println(var + ": " + obj));
    }

    public List<Track> getQueue() {
        return getQueue(0, 50);
    }

    public List<Track> getQueue(int start, int maxItems) {
        ServiceRoute route = getRoute("/MediaServer/ContentDirectory/Control", "Browse");

        if (route == null) {
            throw new RuntimeException("No browse route!");
        }

        Map<String, Object> vars = new HashMap<>();

        vars.put("ObjectID", "Q:0");
        vars.put("BrowseFlag", "BrowseDirectChildren");
        vars.put("Filter", "");
        vars.put("SortCriteria", "");
        vars.put("StartingIndex", start);
        vars.put("RequestedCount", maxItems);

        String result = route.request(this, vars).get("Result").toString();
        Document response = Util.parseDocument(result);

        List<Track> queue = new LinkedList<>();

        try {
            for (Element element : Util.cast(Util.getChildList(response.getDocumentElement()), Element.class)) {
                queue.add(Track.parse(element));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return queue;
    }

    public String request(String endpoint, String action, String body) {
        try {
            URL url = new URL(getURL() + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("Content-Type", "text/xml");
            connection.addRequestProperty("SOAPACTION", action);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                body = String.format(SOAP_TEMPLATE, body);
                char[] data = body.toCharArray();
                writer.write(data, 0, data.length);
            }

            String out = "";
            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                char[] buf = new char[1024];
                while (true) {
                    int count = reader.read(buf, 0, buf.length);
                    if (count < 0)
                        break;
                    out += new String(buf, 0, count);
                }
            }
            return out;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

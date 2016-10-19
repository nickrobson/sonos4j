package xyz.nickr.sonos4j.api.speaker;

import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;

import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class DeviceDescription {

    public static final String DEVICE_DESCRIPTION_URL = "http://%s:1400/xml/device_description.xml";

    private final SpecVersion specVersion;
    private final Device device;

    public DeviceDescription(Speaker speaker) {
        String url = String.format(DEVICE_DESCRIPTION_URL, speaker.getIp());
        String pageContent = Util.getPageContent(url);
        Document document = Util.parseDocument(pageContent);

        Element root = document.getDocumentElement();
        Map<String, Node> children = Util.getChildren(root);

        this.specVersion = new SpecVersion(children.get("specVersion"));
        this.device = new Device(children.get("device"));
    }

}

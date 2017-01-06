package xyz.nickr.sonos4j.api.model;

import java.util.ArrayList;
import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class DeviceList {

    private final List<Device> devices;

    public DeviceList(Node node) {
        List<Element> children = Util.cast(Util.getChildList(node), Element.class);
        List<Device> devices = new ArrayList<>(children.size());
        for (Node child : children) {
            devices.add(new Device(child));
        }
        this.devices = Collections.unmodifiableList(devices);
    }

    @Data
    public static class Device {

        private final String deviceType;
        private final String friendlyName;
        private final String manufacturer, manufacturerUrl;
        private final String modelNumber, modelDescription, modelName, modelUrl;
        private final String uniqueDeviceNumber;

        private final IconList iconList;
        private final ServiceList serviceList;

        public Device(Node node) {
            Map<String, Node> children = Util.getChildren(node);

            this.deviceType = children.get("deviceType").getTextContent();
            this.friendlyName = children.get("friendlyName").getTextContent();
            this.manufacturer = children.get("manufacturer").getTextContent();
            this.manufacturerUrl = children.get("manufacturerURL").getTextContent();
            this.modelNumber = children.get("modelNumber").getTextContent();
            this.modelDescription = children.get("modelDescription").getTextContent();
            this.modelName = children.get("modelName").getTextContent();
            this.modelUrl = children.get("modelURL").getTextContent();
            this.uniqueDeviceNumber = children.get("UDN").getTextContent();

            this.iconList = children.containsKey("iconList") ? new IconList(children.get("iconList")) : null;
            this.serviceList = new ServiceList(children.get("serviceList"));
        }
    }

}

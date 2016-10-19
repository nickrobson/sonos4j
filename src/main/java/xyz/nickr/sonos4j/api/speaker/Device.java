package xyz.nickr.sonos4j.api.speaker;

import lombok.Data;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;

import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class Device {

    private final String deviceType;
    private final String friendlyName;
    private final String manufacturer, manufacturerUrl;
    private final String modelNumber, modelDescription, modelName, modelURL;
    private final String softwareVersion, hardwareVersion;
    private final String serialNumber;
    private final String uniqueDeviceNumber;
    private final String minCompatibleVersion, legacyCompatibleVersion;
    private final String displayVersion, extraVersion;
    private final String roomName;
    private final String displayName;
    private final int zoneType;
    private final int feature1, feature2, feature3;
    private final int internalSpeakerSize;
    private final double bassExtension;
    private final double satGainOffset;
    private final int memory, flash, ampOnTime;

    private final IconList iconList;
    private final ServiceList serviceList;
    private final DeviceList deviceList;

    public Device(Node node) {
        Map<String, Node> children = Util.getChildren(node);

        this.deviceType = children.get("deviceType").getTextContent();
        this.friendlyName = children.get("friendlyName").getTextContent();
        this.manufacturer = children.get("manufacturer").getTextContent();
        this.manufacturerUrl = children.get("manufacturerURL").getTextContent();

        this.modelNumber = children.get("modelNumber").getTextContent();
        this.modelDescription = children.get("modelDescription").getTextContent();
        this.modelName = children.get("modelName").getTextContent();
        this.modelURL = children.get("modelURL").getTextContent();

        this.softwareVersion = children.get("softwareVersion").getTextContent();
        this.hardwareVersion = children.get("hardwareVersion").getTextContent();

        this.serialNumber = children.get("serialNum").getTextContent();
        this.uniqueDeviceNumber = children.get("UDN").getTextContent();

        this.minCompatibleVersion = children.get("minCompatibleVersion").getTextContent();
        this.legacyCompatibleVersion = children.get("legacyCompatibleVersion").getTextContent();

        this.displayVersion = children.get("displayVersion").getTextContent();
        this.extraVersion = children.get("extraVersion").getTextContent();

        this.roomName = children.get("roomName").getTextContent();
        this.displayName = children.get("displayName").getTextContent();

        this.zoneType = Integer.parseInt(children.get("zoneType").getTextContent());

        this.feature1 = Integer.parseInt(children.get("feature1").getTextContent().substring(2), 16);
        this.feature2 = Integer.parseInt(children.get("feature2").getTextContent().substring(2), 16);
        this.feature3 = Integer.parseInt(children.get("feature3").getTextContent().substring(2), 16);

        this.internalSpeakerSize = Integer.parseInt(children.get("internalSpeakerSize").getTextContent());
        this.bassExtension = Double.parseDouble(children.get("bassExtension").getTextContent());
        this.satGainOffset = Double.parseDouble(children.get("satGainOffset").getTextContent());

        this.memory = Integer.parseInt(children.get("memory").getTextContent());
        this.flash = Integer.parseInt(children.get("flash").getTextContent());

        this.ampOnTime = Integer.parseInt(children.get("ampOnTime").getTextContent());

        this.iconList = new IconList(children.get("iconList"));
        this.serviceList = new ServiceList(children.get("serviceList"));
        this.deviceList = new DeviceList(children.get("deviceList"));
    }

}

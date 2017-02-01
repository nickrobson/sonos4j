package xyz.nickr.sonos4j.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;

/**
 * @author Nick Robson
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "speaker")
public class Account {

    private final Speaker speaker;
    private final int serviceType;
    private final String serialNumber;
    private final boolean deleted;
    private final String username;
    private final String nickname;
    private final String metadata;
    private String oaDeviceId;
    private String oaKey;
    private final String hash;

    @Override
    public String toString() {
        return String.format("Account{ %s:%s:%s }", this.serialNumber, this.serviceType, this.nickname);
    }

    public static Account parse(Speaker speaker, Element element) {
        int serviceType = Integer.valueOf(element.getAttribute("Type"));
        String serialNumber = element.getAttribute("SerialNum");
        boolean deleted = "1".equals(element.getAttribute("Deleted"));

        String username = "", nickname = "", metadata = "", oaDeviceId = "", oaKey = "", hash = "";
        List<Element> children = Util.cast(Util.getChildList(element), Element.class);

        for (Element e : children) {
            String text = e.getTextContent();
            switch (e.getNodeName()) {
                case "UN": username = text; break;
                case "NN": nickname = text; break;
                case "MD": metadata = text; break;
                case "OADevID": oaDeviceId = text; break;
                case "Key": oaKey = text; break;
                case "Hash": hash = text; break;
                default: {
                    System.out.println("Unexpected node name in Account data: " + e.getNodeName() + " (= '" + text + "')");
                    break;
                }
            }
        }
        return new Account(speaker, serviceType, serialNumber, deleted, username, nickname, metadata, oaDeviceId, oaKey, hash);
    }

}

package xyz.nickr.sonos4j.api.speaker;

import lombok.Data;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;

import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class SpecVersion {

    private final int majorVersion;
    private final int minorVersion;

    public SpecVersion(Node node) {
        Map<String, Node> children = Util.getChildren(node);

        this.majorVersion = Integer.parseInt(children.get("major").getTextContent());
        this.minorVersion = Integer.parseInt(children.get("minor").getTextContent());
    }

    public String toString() {
        return String.format("%s.%s", majorVersion, minorVersion);
    }

}

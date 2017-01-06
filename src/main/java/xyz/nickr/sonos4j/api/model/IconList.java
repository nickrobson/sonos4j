package xyz.nickr.sonos4j.api.model;

import java.util.ArrayList;
import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nick Robson
 */
@Data
public class IconList {

    private final List<Icon> icons;

    public IconList(Node node) {
        List<Element> children = Util.cast(Util.getChildList(node), Element.class);
        List<Icon> icons = new ArrayList<>(children.size());
        for (Element e : children)
            icons.add(new Icon(e));
        this.icons = Collections.unmodifiableList(icons);
    }

    @Data
    public static class Icon {

        private final int id;
        private final String mimeType;
        private final int width;
        private final int height;
        private final int depth;
        private final String url;

        public Icon(Node node) {
            Map<String, Node> children = Util.getChildren(node);

            this.id = children.containsKey("id") ? Integer.parseInt(children.get("id").getTextContent()) : -1;
            this.mimeType = children.get("mimetype").getTextContent();
            this.width = Integer.parseInt(children.get("width").getTextContent());
            this.height = Integer.parseInt(children.get("height").getTextContent());
            this.depth = Integer.parseInt(children.get("depth").getTextContent());
            this.url = children.get("url").getTextContent();
        }

    }

}

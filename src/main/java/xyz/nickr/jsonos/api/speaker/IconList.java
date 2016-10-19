package xyz.nickr.jsonos.api.speaker;

import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.jsonos.Util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class IconList {

    private final List<Icon> icons;

    public IconList(Node node) {
        List<Element> children = Util.cast(Util.getChildList(node), Element.class);
        List<Icon> icons = new LinkedList<>();

        for (Node child : children) {
            icons.add(new Icon(child));
        }

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

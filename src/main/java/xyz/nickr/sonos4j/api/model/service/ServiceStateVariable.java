package xyz.nickr.sonos4j.api.model.service;

import java.util.ArrayList;
import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;

import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class ServiceStateVariable {

    private final String name;
    private final ServiceStateVariableType type;
    private final boolean sendEvents;
    private final String defaultValue;
    private final List<String> allowedValues;

    public ServiceStateVariable(Element element) {
        this.sendEvents = "yes".equals(element.getAttribute("sendEvents"));

        Map<String, Node> children = Util.getChildren(element);

        this.name = children.get("name").getTextContent();
        this.type = ServiceStateVariableType.fromString(children.get("dataType").getTextContent());
        this.defaultValue = children.containsKey("defaultValue") ? children.get("defaultValue").getTextContent() : null;

        if (children.containsKey("allowedValueList")) {
            List<Element> avl = Util.cast(Util.getChildList(children.get("allowedValueList")), Element.class);
            List<String> allowedValues = new ArrayList<>(avl.size());
            for (Element e : avl) {
                allowedValues.add(e.getTextContent());
            }
            this.allowedValues = allowedValues;
        } else {
            this.allowedValues = null;
        }
    }

}

package xyz.nickr.sonos4j.api.model.service;

import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        this.sendEvents = element.hasAttribute("sendEvents") ? "yes".equals(element.getAttribute("sendEvents")) : false;

        Map<String, Node> children = Util.getChildren(element);

        this.name = children.get("name").getTextContent();
        this.type = ServiceStateVariableType.fromString(children.get("dataType").getTextContent());

        this.defaultValue = children.containsValue("defaultValue") ? children.get("defaultValue").getTextContent() : null;

        if (children.containsKey("allowedValueList")) {
            List<Element> avl = Util.cast(Util.getChildList(children.get("allowedValueList")), Element.class);
            this.allowedValues = avl.stream().map(e -> e.getTextContent()).collect(Collectors.toList());
        } else {
            this.allowedValues = null;
        }
    }

}

package xyz.nickr.sonos4j.api.model.service;

import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.exception.SonosException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import xyz.nickr.sonos4j.api.soap.SoapMessage;

/**
 * @author Nick Robson
 */
@Data
public class ServiceRoute {

    private final Speaker speaker;
    private final String name;
    private final String endpoint;
    private final String serviceType;
    private final String action;
    private final Map<String, ServiceRouteArgument> arguments;

    public ServiceRoute(Speaker speaker, String serviceType, String endpoint, Map<String, ServiceStateVariable> vars, Node node) {
        Map<String, Node> children = Util.getChildren(node);

        this.speaker = speaker;
        this.name = children.get("name").getTextContent();
        this.endpoint = endpoint;
        this.serviceType = serviceType;
        this.action = serviceType + "#" + this.name;

        Map<String, ServiceRouteArgument> arguments = new LinkedHashMap<>();

        Node nodeArgumentList = children.get("argumentList");
        if (nodeArgumentList != null) {
            List<Element> argumentList = Util.cast(Util.getChildList(nodeArgumentList), Element.class);

            for (Element element : argumentList) {
                Map<String, Node> elc = Util.getChildren(element);
                String argName = elc.get("name").getTextContent();
                String argDir = elc.get("direction").getTextContent();
                ServiceStateVariable argVariable = vars.get(elc.get("relatedStateVariable").getTextContent());
                if (argName != null && !argName.isEmpty() && (argDir.equals("in") || argDir.equals("out")) && argVariable != null) {
                    ServiceRouteDirection argDirection = ServiceRouteDirection.valueOf(argDir.toUpperCase());
                    arguments.put(argName, new ServiceRouteArgument(argName, argDirection, argVariable));
                }
            }
        }

        this.arguments = Collections.unmodifiableMap(arguments);
    }

    public Map<String, Object> request() {
        return request(Collections.emptyList());
    }

    public Map<String, Object> request(List<Map.Entry<String, Object>> args) {
        SoapMessage message = new SoapMessage(
                speaker.getURL() + endpoint,
                name,
                args,
                null,
                action,
                null,
                serviceType
        );

        return message.callAndParse(this.arguments);
    }

}

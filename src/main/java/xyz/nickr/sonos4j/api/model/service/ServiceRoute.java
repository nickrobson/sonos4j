package xyz.nickr.sonos4j.api.model.service;

import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Nick Robson
 */
@Data
public class ServiceRoute {

    private final String name;
    private final String endpoint;
    private final String serviceType;
    private final String action;
    private final Map<String, ServiceRouteArgument> arguments;

    public ServiceRoute(String serviceType, String endpoint, Map<String, ServiceStateVariable> vars, Node node) {
        Map<String, Node> children = Util.getChildren(node);

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

    public Map<String, Object> request(Speaker speaker, Map<String, Object> vars) {
        Document doc = Util.getDocumentBuilder().newDocument();

        Element root = doc.createElementNS(serviceType, this.name);
        root.setPrefix("u");
        doc.appendChild(root);

        for (Map.Entry<String, Object> var : vars.entrySet()) {
            Element sub = doc.createElement(var.getKey());
            Object val = var.getValue();
            if (val instanceof Boolean)
                val = (boolean) val ? "1" : "0";
            sub.setTextContent(val.toString());
            root.appendChild(sub);
        }

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return request(speaker, writer.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Map<String, Object> request(Speaker speaker, String body) {
        String res = speaker.request(endpoint, action, body);
        Document responseBody = Util.getResponseBody(res);

        Map<String, Object> vars = new LinkedHashMap<>();

        for (Element el : Util.cast(Util.getChildList(responseBody.getDocumentElement()), Element.class)) {
            ServiceRouteArgument arg = this.arguments.get(el.getNodeName());
            if (arg == null)
                continue;
            ServiceStateVariable var = arg.getVariable();
            vars.put(el.getNodeName(), var.getType().cast(el.getTextContent()));
        }

        return vars;
    }

}

package xyz.nickr.sonos4j.api.model.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.SpecVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class ServiceSchema {

    private final String schemaUrl;
    private final String serviceType;
    private final String controlUrl;

    private final Map<String, ServiceStateVariable> stateTable = new HashMap<>();
    private final Map<String, ServiceRoute> routes = new HashMap<>();

    @Setter(AccessLevel.PRIVATE)
    private SpecVersion specVersion;

    private boolean loaded = false;

    public ServiceSchema(String schemaUrl, String serviceType, String controlUrl) {
        this.schemaUrl = schemaUrl;
        this.serviceType = serviceType;
        this.controlUrl = controlUrl;
    }

    public ServiceSchema load(Speaker speaker) {
        if (loaded)
            return this;

        loaded = true;

        String pageContent = Util.getPageContent(speaker.getURL() + this.schemaUrl);
        Document document = Util.parseDocument(pageContent);

        Map<String, Node> children = Util.getChildren(document.getDocumentElement());

        this.specVersion = new SpecVersion(children.get("specVersion"));

        List<Element> stateVariables = Util.cast(Util.getChildList(children.get("serviceStateTable")), Element.class);

        for (Element stateVariable : stateVariables) {
            ServiceStateVariable var = new ServiceStateVariable(stateVariable);
            this.stateTable.put(var.getName(), var);
        }

        List<Element> actionList = Util.cast(Util.getChildList(children.get("actionList")), Element.class);

        for (Element action : actionList) {
            ServiceRoute route = new ServiceRoute(speaker, serviceType, controlUrl, stateTable, action);
            this.routes.put(route.getName(), route);
        }

        return this;
    }

    public ServiceStateVariable getStateVariable(String name) {
        if (!loaded)
            throw new IllegalStateException("Schema not loaded yet!");
        return stateTable.get(name);
    }

    public ServiceRoute getRoute(String name) {
        if (!loaded)
            throw new IllegalStateException("Schema not loaded yet!");
        return routes.get(name);
    }

}

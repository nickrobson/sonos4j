package xyz.nickr.sonos4j.api.model;

import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.model.service.ServiceSchema;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class ServiceList {

    private final List<Service> services;

    public ServiceList(Node node) {
        List<Element> children = Util.cast(Util.getChildList(node), Element.class);
        List<Service> services = new LinkedList<>();

        for (Node child : children) {
            services.add(new Service(child));
        }

        this.services = Collections.unmodifiableList(services);
    }

    @Data
    public static class Service {

        private final String serviceType;
        private final String serviceId;
        private final String controlUrl;
        private final String eventSubUrl;
        private final String scpdUrl;

        private final ServiceSchema schema;

        public Service(Node node) {
            Map<String, Node> children = Util.getChildren(node);

            this.serviceType = children.get("serviceType").getTextContent();
            this.serviceId = children.get("serviceId").getTextContent();
            this.controlUrl = children.get("controlURL").getTextContent();
            this.eventSubUrl = children.get("eventSubURL").getTextContent();
            this.scpdUrl = children.get("SCPDURL").getTextContent();

            this.schema = new ServiceSchema(this.scpdUrl, this.serviceType, this.controlUrl);
        }

    }

}

package xyz.nickr.sonos4j.api.model.service;

import lombok.Data;

/**
 * @author Nick Robson
 */
@Data
public class ServiceRouteArgument {

    private final String name;
    private final ServiceRouteDirection direction;
    private final ServiceStateVariable variable;

}

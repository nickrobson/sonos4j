package xyz.nickr.sonos4j.api.exception;

import lombok.Getter;
import xyz.nickr.sonos4j.api.Speaker;

/**
 * @author Nick Robson
 */
@Getter
public class RouteMissingException extends SonosException {

    private final String endpoint, name;

    public RouteMissingException(Speaker speaker, String endpoint, String name) {
        super(speaker, String.format("No %s : %s route!", endpoint, name));
        this.endpoint = endpoint;
        this.name = name;
    }

}

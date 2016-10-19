package xyz.nickr.jsonos.api.speaker.service;

/**
 * @author Nick Robson
 */
public enum ServiceRouteDirection {

    IN,
    OUT;

    public String toString() {
        return name().toLowerCase();
    }

}

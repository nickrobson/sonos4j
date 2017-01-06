package xyz.nickr.sonos4j.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Nick Robson
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Room {

    private final String name;
    private final List<Speaker> speakers;

}

package xyz.nickr.sonos4j.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.nickr.sonos4j.api.speaker.Speaker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nick Robson
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final String name;
    private final List<Speaker> speakers;

    public static List<Room> getRooms(List<Speaker> speakers) {
        Map<String, List<Speaker>> map = speakers.stream().collect(Collectors.groupingBy(s -> s.getDescription().getDevice().getRoomName(), Collectors.toList()));
        return map.entrySet().stream().map(e -> new Room(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

}

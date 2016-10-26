package xyz.nickr.sonos4j.api;

import org.junit.Test;
import xyz.nickr.sonos4j.api.model.ServiceList;
import xyz.nickr.sonos4j.api.model.service.ServiceSchema;

import java.util.Arrays;
import java.util.List;

/**
 * @author Nick Robson
 */
public class DiscoveryTest {

    @Test
    public void testFindSpeakers() {
        System.out.println("Finding speakers...");
        long start = System.currentTimeMillis();
        Speaker[] speakers = Discovery.getSpeakers();
        System.out.format("Took %sms to fetch speakers!\n", System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        for (Speaker speaker : speakers) {
            System.out.println(speaker.getDescription());
            System.out.format("Took %sms to fetch speaker info!\n", System.currentTimeMillis() - start);
            start = System.currentTimeMillis();
        }
    }

    @Test
    public void testFindRooms() {
        System.out.println("Finding rooms...");
        long start = System.currentTimeMillis();
        List<Speaker> speakers = Arrays.asList(Discovery.getSpeakers());
        System.out.format("Took %sms to fetch speakers!\n", System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        List<Room> rooms = Room.getRooms(speakers);
        System.out.format("Took %sms to fetch rooms!\n", System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        for (Room room : rooms) {
            System.out.println("Room: " + room.getName());
            for (Speaker speaker : room.getSpeakers()) {
                System.out.println(speaker.getDescription());
            }
            System.out.format("Took %sms to fetch room info!\n", System.currentTimeMillis() - start);
            start = System.currentTimeMillis();
        }
    }

    @Test
    public void testSchema() {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            ServiceList.Service srv = speaker.getDescription().getDevice().getServiceList().getServices().get(0);
            ServiceSchema schema = srv.getSchema();
            schema.load(speaker);
            break;
        }
    }

}

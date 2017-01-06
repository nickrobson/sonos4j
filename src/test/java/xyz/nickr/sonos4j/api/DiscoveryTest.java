package xyz.nickr.sonos4j.api;

import org.junit.BeforeClass;
import org.junit.Test;
import xyz.nickr.sonos4j.api.model.ServiceList;
import xyz.nickr.sonos4j.api.model.service.ServiceSchema;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * @author Nick Robson
 */
public class DiscoveryTest {

    private static Speaker[] speakers;
    private static List<Room> rooms;

    @BeforeClass
    public static void init() {
        System.out.println("Finding speakers...");
        long start = System.currentTimeMillis();
        speakers = Discovery.getSpeakers();
        if (speakers.length == 0)
            fail("No Sonos system found");
        System.out.format("Took %sms to fetch speakers!\n", System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        rooms = Discovery.getRooms(speakers);
        System.out.format("Took %sms to fetch rooms!\n", System.currentTimeMillis() - start);
    }

    @Test
    public void testFindSpeakers() {
        long start = System.currentTimeMillis();
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
        for (Room room : rooms) {
            System.out.println("Room: " + room.getName());
            for (Speaker speaker : room.getSpeakers()) {
                System.out.println("    " + speaker.getDescription());
            }
            System.out.format("Took %sms to fetch room info!\n", System.currentTimeMillis() - start);
            start = System.currentTimeMillis();
        }
    }

    @Test
    public void testSchema() {
        for (Speaker speaker : speakers) {
            ServiceList.Service srv = speaker.getDescription().getDevice().getServiceList().getServices().get(0);
            ServiceSchema schema = srv.getSchema();
            schema.load(speaker);
            System.out.println(schema);
        }
    }

}

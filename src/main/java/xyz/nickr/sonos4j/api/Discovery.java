package xyz.nickr.sonos4j.api;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
public class Discovery {

    public static final String PLAYER_SEARCH = "M-SEARCH * HTTP/1.1\nHOST: 239.255.255.250:reservedSSDPport\nMAN: ssdp:discover\nMX: 1\nST: urn:schemas-upnp-org:device:ZonePlayer:1";

    public static String[] getSpeakerIPs() {
        try {
            List<String> ips = new ArrayList<>();

            byte[] bytes = PLAYER_SEARCH.getBytes();

            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(1000);
            socket.setBroadcast(true);

            InetAddress mcast = InetAddress.getByName("239.255.255.250");

            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, mcast, 1900);

            socket.send(packet);

            while (true) {
                try {
                    socket.receive(packet);
                    ips.add(packet.getAddress().getHostAddress());
                } catch (Exception ex) {
                    break;
                }
            }

            socket.close();

            return ips.toArray(new String[0]);
        } catch (Exception ex) {
            return new String[0];
        }
    }

    public static Speaker[] getSpeakers() {
        return getSpeakers(false);
    }

    public static Speaker[] getSpeakers(boolean load) {
        String[] ids = getSpeakerIPs();
        Speaker[] speakers = new Speaker[ids.length];

        for (int i = 0, j = ids.length; i < j; i++) {
            speakers[i] = new Speaker(ids[i]);
            if (load) {
                speakers[i].load();
            }
        }

        return speakers;
    }

    public static List<Room> getRooms(List<Speaker> speakers) {
        Map<String, List<Speaker>> map = new HashMap<>();
        for (Speaker speaker : speakers) {
            String mapKey = speaker.getDescription().getDevice().getRoomName();
            List<Speaker> speakerList = map.get(mapKey);
            if (speakerList == null)
                speakerList = new ArrayList<>();
            speakerList.add(speaker);
            map.put(mapKey, speakerList);
        }
        List<Room> rooms = new ArrayList<>();
        for (Map.Entry<String, List<Speaker>> entry : map.entrySet()) {
            rooms.add(new Room(entry.getKey(), entry.getValue()));
        }
        return rooms;
    }

    public static List<Room> getRooms(Speaker[] speakers) {
        return getRooms(Arrays.asList(speakers));
    }

    public static List<Room> getRooms() {
        return getRooms(getSpeakers());
    }

}

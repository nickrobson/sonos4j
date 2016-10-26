package xyz.nickr.sonos4j.api;

import org.junit.Test;
import xyz.nickr.sonos4j.api.model.Alarm;
import xyz.nickr.sonos4j.api.model.media.CurrentTrack;
import xyz.nickr.sonos4j.api.model.media.Track;

import java.util.List;

/**
 * @author Nick Robson
 */
public class RequestTest {

    @Test
    public void testRequestQueue() {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            List<Track> queue = speaker.getQueue();
            for (int i = 0, j = queue.size(); i < j; i++) {
                Track track = queue.get(i);
                System.out.println();
                System.out.println(track.getTitle());
                System.out.println("-  " + track.getArtist());
                System.out.println("-  " + track.getAlbum());
                System.out.println("-  " + track.getDuration());
                System.out.println("-  " + track.getUri());
                System.out.println("-  " + track.getAlbumArtLink());
            }
            break;
        }
    }

    @Test
    public void testRequestTrack() {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            CurrentTrack current = speaker.getCurrentTrack();
            Track track = current.getTrack();
            System.out.println("#" + current.getPositionInPlaylist() + " in playlist");
            System.out.println(track.getTitle());
            System.out.println("-  " + track.getArtist());
            System.out.println("-  " + track.getAlbum());
            System.out.println("-  " + track.getDuration());
            System.out.println("-  " + track.getUri());
            System.out.println("-  " + track.getAlbumArtLink());
            break;
        }
    }

    @Test
    public void testListAlarms() {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            List<Alarm> alarms = speaker.getAlarmClockController().getAlarms();
            for (Alarm alarm : alarms) {
                System.out.println(alarm);
            }
            break;
        }
    }

    @Test
    public void testCreateAlarm() {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            Alarm alarm = new Alarm(-1, "11:00:00", "00:01:00", "DAILY", false, "RINCON_5CAAFD03F86E01400", "x-rincon-buzzer:0", "", "SHUFFLE_NOREPEAT", 25, false);
            Alarm alarm2 = speaker.getAlarmClockController().createAlarm(alarm);
            System.out.println("Created alarm: " + alarm2);
            System.out.println("    From: " + alarm);
            break;
        }
    }

    @Test
    public void testListMusicServices() {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            speaker.getMusicServicesController().getAvailableServiceDescriptors();
            break;
        }
    }

}

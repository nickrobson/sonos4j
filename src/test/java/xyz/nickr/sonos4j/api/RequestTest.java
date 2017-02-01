package xyz.nickr.sonos4j.api;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.BeforeClass;
import org.junit.Test;
import xyz.nickr.sonos4j.api.exception.AlarmAlreadyExistsException;
import xyz.nickr.sonos4j.api.model.Account;
import xyz.nickr.sonos4j.api.model.alarm.Alarm;
import xyz.nickr.sonos4j.api.model.alarm.AlarmPlayMode;
import xyz.nickr.sonos4j.api.model.media.CurrentTrack;
import xyz.nickr.sonos4j.api.model.media.MusicService;
import xyz.nickr.sonos4j.api.model.media.Track;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Nick Robson
 */
public class RequestTest {

    private static Speaker[] speakers;

    @BeforeClass
    public static void init() {
        speakers = Discovery.getSpeakers();
        if (speakers.length == 0)
            fail("No Sonos system found");
    }

    @Test
    public void testRequestQueue() {
        for (Speaker speaker : speakers) {
            List<Track> queue = speaker.getQueue();
            for (int i = 0, j = queue.size(); i < j; i++) {
                Track track = queue.get(i);
                System.out.println();
                System.out.println(i + ". " + track.getTitle());
                System.out.println("-  " + track.getArtist());
                System.out.println("-  " + track.getAlbum());
                System.out.println("-  " + track.getDuration());
                System.out.println("-  " + track.getUri());
                System.out.println("-  " + track.getAlbumArtLink());
            }
        }
    }

    @Test
    public void testRequestTrack() {
        for (Speaker speaker : speakers) {
            try {
                System.out.println(speaker.getRoomName());
                CurrentTrack current = speaker.getCurrentTrack();
                if (current != null) {
                    Track track = current.getTrack();
                    System.out.println("#" + current.getPositionInPlaylist() + " in playlist");
                    System.out.println(track.getTitle());
                    System.out.println("-  " + track.getArtist());
                    System.out.println("-  " + track.getAlbum());
                    System.out.println("-  " + track.getDuration());
                    System.out.println("-  " + track.getUri());
                    System.out.println("-  " + track.getAlbumArtLink());
                } else {
                    System.out.println("No track currently being played!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testListAlarms() {
        for (Speaker speaker : speakers) {
            List<Alarm> alarms = speaker.getAlarmClockController().getAlarms();
            for (Alarm alarm : alarms) {
                System.out.println(alarm);
            }
        }
    }

    @Test
    public void testCreateAlarm() {
        for (Speaker speaker : speakers) {
            try {
                Alarm alarm = new Alarm(-1, "11:00:00", "00:01:00", "DAILY", false, "RINCON_5CAAFD03F86E01400", "x-rincon-buzzer:0", "", AlarmPlayMode.SHUFFLE_NOREPEAT, 25, false);
                Alarm alarm2 = speaker.getAlarmClockController().createAlarm(alarm);
                System.out.println("Created alarm: " + alarm2);
                System.out.println("    From: " + alarm);
                assertEquals(alarm.withId(alarm2.getId()), alarm2);
            } catch (AlarmAlreadyExistsException ex) {
                System.out.println("Alarm already exists!");
            }
        }
    }

    @Test
    public void testListMusicServices() {
        for (Speaker speaker : speakers) {
            System.out.println(speaker.getRoomName());
            for (Map.Entry<String, MusicService> entry : speaker.getMusicServicesController().getMusicServicesMap().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue().getSearchCategories());
            }
        }
    }

    @Test
    public void testListAccounts() {
        for (Speaker speaker : speakers) {
            System.out.println(speaker.getRoomName());
            for (Map.Entry<String, Account> entry : speaker.getAccountsController().getAccountsMap().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

}

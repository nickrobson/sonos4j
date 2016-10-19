package xyz.nickr.jsonos.api;

import org.junit.Test;
import xyz.nickr.jsonos.api.media.CurrentTrack;
import xyz.nickr.jsonos.api.media.Track;
import xyz.nickr.jsonos.api.speaker.Speaker;

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
    public void testRequestMediaInfo() {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            speaker.getMediaInfo();
            break;
        }
    }

}

package xyz.nickr.sonos4j.api.media;

import lombok.Data;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.speaker.Speaker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Data
public class Track {

    private final String title;
    private final String artist;
    private final String album;
    private final String uri;
    private final String albumArtLink;
    private final String duration;

    public BufferedImage getAlbumArtImage(Speaker speaker) {
        if (albumArtLink == null)
            return null;
        try {
            URL url = new URL(speaker.getURL() + albumArtLink);
            return ImageIO.read(url);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Track parse(Element element) {
        Map<String, Node> children = Util.getChildren(element);

        String uri = children.get("res").getTextContent();
        String duration = ((Element) children.get("res")).getAttribute("duration");
        String albumArt = children.get("upnp:albumArtURI").getTextContent();
        String title = children.get("dc:title").getTextContent();
        String artist = children.get("dc:creator").getTextContent();
        String album = children.get("upnp:album").getTextContent();

        return new Track(title, artist, album, uri, albumArt, duration);
    }
}

package xyz.nickr.sonos4j.api.media;

import lombok.Data;

/**
 * @author Nick Robson
 */
@Data
public class CurrentTrack {

    private final long positionInPlaylist;
    private final Track track;
}

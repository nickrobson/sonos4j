package xyz.nickr.jsonos.api.media;

import lombok.Data;

/**
 * @author Nick Robson
 */
@Data
public class CurrentTrack {

    private final long positionInPlaylist;
    private final Track track;
}

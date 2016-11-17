package xyz.nickr.sonos4j.api.exception;

import lombok.Getter;
import xyz.nickr.sonos4j.api.Speaker;

/**
 * @author Nick Robson
 */
@Getter
public class SonosException extends RuntimeException {

    private final Speaker speaker;

    public SonosException(Speaker speaker) {
        this.speaker = speaker;
    }

    public SonosException(Speaker speaker, String message) {
        super(message);
        this.speaker = speaker;
    }

    public SonosException(Speaker speaker, String message, Throwable cause) {
        super(message, cause);
        this.speaker = speaker;
    }

    public SonosException(Speaker speaker, Throwable cause) {
        super(cause);
        this.speaker = speaker;
    }

}

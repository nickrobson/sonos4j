package xyz.nickr.sonos4j.api.exception;

import lombok.Getter;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.Alarm;

/**
 * @author Nick Robson
 */
@Getter
public class AlarmAlreadyExistsException extends SonosException {

    private final Alarm alarm;

    public AlarmAlreadyExistsException(Speaker speaker, Alarm alarm) {
        super(speaker, "Failed to create alarm: Perhaps the time clashes with another alarm?");
        this.alarm = alarm;
    }

}

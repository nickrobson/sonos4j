package xyz.nickr.sonos4j.api.exception;

import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.media.MusicService;

/**
 * @author Nick Robson
 */
public class InvalidSearchCategoryException extends SonosException {

    private final MusicService service;
    private final String category;

    public InvalidSearchCategoryException(Speaker speaker, MusicService service, String category) {
        super(speaker, service.getName() + " does not support searching for " + category);
        this.service = service;
        this.category = category;
    }

}

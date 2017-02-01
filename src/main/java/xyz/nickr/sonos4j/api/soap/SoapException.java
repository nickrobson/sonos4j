package xyz.nickr.sonos4j.api.soap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;
import xyz.nickr.sonos4j.Util;

/**
 * @author Nick Robson
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SoapException extends RuntimeException {

    private final String faultCode;
    private final String faultString;
    private final Element faultDetail;
    private final int errorCode;
    private final String errorDescription;

    @Override
    public String toString() {
        return String.format(
                "SoapException(faultCode=%s, faultString=%s, errorCode=%s, errorDescription=%s, faultDetail=%s)",
                faultCode,
                faultString,
                errorCode,
                errorDescription,
                Util.toString(faultDetail)
        );
    }

}

package xyz.nickr.sonos4j.api.soap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Wither;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.model.service.ServiceRouteArgument;

/**
 * @author Nick Robson
 */
@Data
@Wither
public class SoapMessage {

    @NonNull
    private final String url, method;

    @NonNull
    private final List<Map.Entry<String, Object>> args;

    private final Map<String, String> httpHeaders;

    private final String soapAction;

    private final String soapHeader;

    private final String namespace;

    public Map<String, String> getHttpHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/xml; charset=\"utf-8\"");
        if (soapAction != null) {
            headers.put("SOAPACTION", soapAction);
        }
        if (httpHeaders != null) {
            headers.putAll(httpHeaders);
        }
        return headers;
    }

    public String getSoapHeader() {
        return soapHeader != null ? "<s:Header>" + soapHeader + "</s:Header>" : "";
    }

    public String getSoapBody() {
        List<String> tags = new LinkedList<>();
        for (Map.Entry<String, Object> arg : args) {
            Object val = arg.getValue();
            if (val == null)
                continue;
            if (val instanceof Boolean)
                val = (boolean) val ? 1 : 0;
            String value = val.toString().replace("&", "&amp;").replace("\"", "&quot;");
            String tag = String.format(
                    "<%1$s>%2$s</%1$s>",
                    arg.getKey(),
                    value
            );
            tags.add(tag);
        }

        if (namespace != null) {
            return String.format(
                    "<u:%1$s xmlns:u=\"%2$s\">%3$s</u:%1$s>",
                    method,
                    namespace,
                    String.join("", tags)
            );
        } else {
            return String.format(
                    "<u:%1$s>%2$s</u:%1$s>",
                    method,
                    String.join("", tags)
            );
        }
    }

    public String getSoapEnvelope() {
        String template = "<?xml version=\"1.0\"?>" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                " s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "%s" +
                "<s:Body>%s</s:Body>" +
                "</s:Envelope>";

        return String.format(
                template,
                getSoapHeader(),
                getSoapBody()
        );
    }

    public Document call() throws SoapException {
        Map<String, String> headers = getHttpHeaders();
        String body = getSoapEnvelope();

        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            headers.forEach(connection::addRequestProperty);
            connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

            int statusCode = connection.getResponseCode();
            if (statusCode != 200 && statusCode != 500) {
                throw new RuntimeException("invalid response code: " + statusCode);
            }
            InputStream inputStream = statusCode == 200 ? connection.getInputStream() : connection.getErrorStream();
            StringWriter writer = new StringWriter();
            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                char[] chars = new char[1024];
                int c;
                while ((c = reader.read(chars)) > 0) {
                    writer.write(chars, 0, c);
                }
            }
            String responseContent = writer.toString();
            Document doc = Util.getResponseBody(responseContent);
            if (statusCode == 200) {
                return doc;
            } else {
                Element faultElement = doc.getDocumentElement();
                Map<String, Node> children = Util.getChildren(faultElement);
                String faultCode = children.get("faultcode").getTextContent();
                String faultString = children.get("faultstring").getTextContent();
                Element faultDetail = (Element) children.get("detail");
                Map<String, Node> faultDetailChildren = faultDetail.hasChildNodes() ? Util.getChildren(faultDetail.getFirstChild()) : Collections.emptyMap();
                int errorCode = faultDetailChildren.containsKey("errorCode") ? Integer.valueOf(faultDetailChildren.get("errorCode").getTextContent()) : -1;
                String errorDescription = faultDetailChildren.containsKey("errorDescription") ? faultDetailChildren.get("errorDescription").getTextContent() : null;
                throw new SoapException(faultCode, faultString, faultDetail, errorCode, errorDescription);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public Map<String, String> callAndParse() {
        Document responseBody = call();
        return Util.getChildList(responseBody.getDocumentElement())
                .stream()
                .filter(n -> n instanceof Element)
                .map(n -> (Element) n)
                .collect(Collectors.toMap(Node::getNodeName, Node::getTextContent));
    }

    public Map<String, Object> callAndParse(Map<String, ServiceRouteArgument> args) {
        Map<String, Object> out = new LinkedHashMap<>();
        callAndParse().forEach((k, v) -> {
            ServiceRouteArgument arg = args != null ? args.get(k) : null;
            out.put(k, arg != null ? arg.getVariable().getType().cast(v) : v);
        });
        return out;
    }

}

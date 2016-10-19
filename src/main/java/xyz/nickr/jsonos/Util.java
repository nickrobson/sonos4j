package xyz.nickr.jsonos;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Robson
 */
public class Util {

    private static final DocumentBuilder documentBuilder;

    static {
        DocumentBuilder db = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {}
        documentBuilder = db;
    }

    public static DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    public static Document parseDocument(String content) {
        try {
            return documentBuilder.parse(new InputSource(new StringReader(content)));
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPageContent(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            StringWriter writer = new StringWriter();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.append(line + "\n");
                }
            }
            return writer.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<String, Node> getChildren(Node node) {
        Map<String, Node> map = new HashMap<>();
        NodeList list = node.getChildNodes();
        for (int i = 0, j = list.getLength(); i < j; i++) {
            Node child = list.item(i);
            map.put(child.getNodeName(), child);
        }
        return map;
    }

    public static List<Node> getChildList(Node node) {
        List<Node> nodes = new LinkedList<>();
        NodeList children = node.getChildNodes();
        for (int i = 0, j = children.getLength(); i < j; i++) {
            nodes.add(children.item(i));
        }
        return nodes;
    }

    public static <T> List<T> cast(List<?> list, Class<T> clazz) {
        List<T> out = new LinkedList<>();
        for (Object o : list) {
            try {
                out.add(clazz.cast(o));
            } catch (Exception ex) {}
        }
        return out;
    }

    public static Document getResponseBody(String content) {
        Document doc = parseDocument(content);
        Node node = doc.getDocumentElement().getFirstChild().getFirstChild();
        Document newDocument = documentBuilder.newDocument();
        Node n = newDocument.adoptNode(node.cloneNode(true));
        newDocument.appendChild(n);
        return newDocument;
    }

    public static String toString(Node node) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));

            return writer.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}

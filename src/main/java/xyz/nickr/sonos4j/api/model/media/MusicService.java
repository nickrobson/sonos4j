package xyz.nickr.sonos4j.api.model.media;

import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.exception.InvalidSearchCategoryException;
import xyz.nickr.sonos4j.api.model.Account;
import xyz.nickr.sonos4j.api.soap.SoapException;
import xyz.nickr.sonos4j.api.soap.SoapMessage;

/**
 * @author Nick Robson
 */
@Data
@ToString(exclude = "speaker")
@EqualsAndHashCode(exclude = "speaker")
public class MusicService {

    private final Speaker speaker;
    private final int id;
    private final String name;
    private final String version;
    private final String uri;
    private final String secureUri;
    private final String containerType;
    private final int capabilities;
    private final String policyAuth;
    private final int policyPollInterval;
    private final String presentationStringsUri;
    private final String presentationMapUri;

    private Map<String, String> strings;
    private Map<String, String> searchCategories;
    private Account account;
    private String credentialsHeader;

    public Account getAccount() {
        if (account != null)
            return account;

        Optional<Account> opt = speaker.getAccountsController().getAccountByService(this.getServiceType());
        return account = opt.orElse(null);
    }

    public int getServiceType() {
        return this.id * 256 + 7; // magic
    }

    public String getCredentialsHeader() {
        if (this.credentialsHeader != null)
            return this.credentialsHeader;

        Document document = Util.getDocumentBuilder().newDocument();

        Element credentialsHeader = document.createElement("credentials");
        credentialsHeader.setAttribute("xmlns", "http://www.sonos.com/Services/1.1");

        Element deviceId = document.createElement("deviceId");
        deviceId.setTextContent(this.speaker.getSystemPropertiesController().getString("R_TrialZPSerial"));

        Element deviceProvider = document.createElement("deviceProvider");
        deviceProvider.setTextContent("Sonos");

        credentialsHeader.appendChild(deviceId);
        credentialsHeader.appendChild(deviceProvider);

        Account account = this.getAccount();
        if (account != null && !"".equals(account.getOaDeviceId())) {
            Element loginToken = document.createElement("loginToken");

            Element token = document.createElement("token");
            token.setTextContent(account.getOaDeviceId());

            Element key = document.createElement("key");
            key.setTextContent(account.getOaKey());

            Element householdId = document.createElement("householdId");
            householdId.setTextContent(this.speaker.getDevicePropertiesController().getHouseholdId());

            loginToken.appendChild(token);
            loginToken.appendChild(key);
            loginToken.appendChild(householdId);

            credentialsHeader.appendChild(loginToken);

        } else if (this.getPolicyAuth().equals("DeviceLink") || this.getPolicyAuth().equals("UserId")) {
            String sessionId = speaker.getMusicServicesController().getSessionId(this.getId(), account.getUsername());

            Element sessionElement = document.createElement("sessionId");
            sessionElement.setTextContent(sessionId);

            credentialsHeader.appendChild(sessionElement);
        }
        return this.credentialsHeader = Util.toString(document);
    }

    public Map<String, String> request(String method, List<Map.Entry<String, Object>> args) {
        String soapHeader = this.getCredentialsHeader();
        String soapAction = "http://www.sonos.com/Services/1.1#" + method;
        Map<String, String> httpHeaders = new HashMap<String, String>() {{
            put("Accept-Encoding", "gzip, deflate");
            put("User-Agent", "Linux UPnP/1.0 Sonos/26.99-12345");
        }};
        SoapMessage res = new SoapMessage(this.secureUri, method, args, httpHeaders, soapAction, soapHeader, null);
        try {
            return res.callAndParse();
        } catch (SoapException ex) {
            if (ex.getFaultCode().contains("Client.TokenRefreshRequired")) {
                Element faultDetail = ex.getFaultDetail();
                Element refreshAuthTokenResult = (Element) faultDetail.getFirstChild();
                if (refreshAuthTokenResult != null && "refreshAuthTokenResult".equals(refreshAuthTokenResult.getNodeName())) {
                    Map<String, Node> children = Util.getChildren(refreshAuthTokenResult);
                    String authToken = children.get("authToken").getTextContent();
                    String privateKey = children.get("privateKey").getTextContent();

                    Account account = getAccount();
                    account.setOaDeviceId(authToken);
                    account.setOaKey(privateKey);

                    this.credentialsHeader = null;
                    res = res.withSoapHeader(this.getCredentialsHeader());
                    return res.callAndParse();
                }
            }
            throw ex;
        }
    }

    public String getSonosURI(String itemId) {
        String quotedItemId = itemId;
        try {
            quotedItemId = URLEncoder.encode(itemId, "UTF-8");
        } catch (Exception ex) {
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        }

        return String.format(
                "sonos4j://%s?sid=%s&sn=%s",
                quotedItemId,
                this.getServiceType(),
                this.getAccount().getSerialNumber()
        );
    }

    public String getDescription() {
        Account account = getAccount();
        return String.format("SA_RINCON%s_%s", account.getServiceType(), account.getUsername());
    }

    public Map<String, String> getStringsMap() {
        if (strings == null) {
            strings = new HashMap<>();
            if (presentationStringsUri != null) {
                try {
                    String pmap = Util.getPageContent(presentationStringsUri);
                    Document pdoc = Util.parseDocument(pmap);
                    for (Element element : Util.cast(Util.getChildList(pdoc.getFirstChild()), Element.class)) {
                        if (element.hasAttribute("xml:lang") && element.getAttribute("xml:lang").equals("en-US")) {
                            for (Element stringElement : Util.cast(Util.getChildList(element), Element.class)) {
                                if (stringElement.hasAttribute("stringId")) {
                                    strings.put(stringElement.getAttribute("stringId"), stringElement.getTextContent());
                                }
                            }
                            break;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        return strings;
    }

    public String getString(String key, String def) {
        return this.getStringsMap().getOrDefault(key, def);
    }

    public String getString(String key) {
        return getString(key, key);
    }

    public Map<String, String> getSearchCategoriesMap() {
        if (searchCategories == null) {
            searchCategories = new HashMap<>();
            if (presentationMapUri != null) {
                try {
                    String pmap = Util.getPageContent(presentationMapUri);
                    Document pdoc = Util.parseDocument(pmap);
                    NodeList nodes = pdoc.getElementsByTagName("PresentationMap");
                    for (int i = 0, j = nodes.getLength(); i < j; i++) {
                        Node node = nodes.item(i);
                        if (node instanceof Element && ((Element) node).getAttribute("type").equals("Search")) {
                            Element matchElement = (Element) Util.getChildren(node).get("Match");
                            Element searchCategoriesElement = (Element) Util.getChildren(matchElement).get("SearchCategories");
                            NodeList categories = searchCategoriesElement.getChildNodes();
                            for (int k = 0, l = categories.getLength(); k < l; k++) {
                                Node n = categories.item(k);
                                if (n instanceof Element) {
                                    Element e = (Element) n;
                                    String idTag = e.hasAttribute("id") ? "id" : e.hasAttribute("stringId") ? "stringId" : null;
                                    if (idTag != null) {
                                        searchCategories.put(getString(e.getAttribute(idTag)), getString(e.getAttribute("mappedId")));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        return Collections.unmodifiableMap(searchCategories);
    }

    public Set<String> getSearchCategories() {
        return getSearchCategoriesMap().keySet();
    }

    public Map<String, String> search(String category, String term, int offset, int count) {
        String internalCategory = getSearchCategoriesMap().get(category);
        if (internalCategory == null)
            throw new InvalidSearchCategoryException(speaker, this, category);
        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("id", internalCategory));
        vars.add(new AbstractMap.SimpleEntry<>("term", term));
        vars.add(new AbstractMap.SimpleEntry<>("index", offset));
        vars.add(new AbstractMap.SimpleEntry<>("count", count));
        return request("search", vars);
    }

    public static MusicService parse(Speaker speaker, Element element) {
        int id = Integer.valueOf(element.getAttribute("Id"));
        String name = element.getAttribute("Name");
        String version = element.getAttribute("Version");
        String uri = element.getAttribute("Uri");
        String secureUri = element.getAttribute("SecureUri");
        String containerType = element.getAttribute("ContainerType");
        int capabilities = Integer.valueOf(element.getAttribute("Capabilities"));

        Map<String, Node> children = Util.getChildren(element);

        Element policyElement = (Element) children.get("Policy");
        String policyAuth = policyElement.getAttribute("Auth");
        int policyPollInterval = Integer.valueOf(policyElement.getAttribute("PollInterval"));

        Element presentationElement = (Element) children.get("Presentation");
        Map<String, Node> presentationChildren = Util.getChildren(presentationElement);

        Element stringsElement = (Element) presentationChildren.get("Strings");
        Element presentationMapElement = (Element) presentationChildren.get("PresentationMap");

        String presentationStringsUri = stringsElement != null ? stringsElement.getAttribute("Uri") : null;
        String presentationMapUri = presentationMapElement != null ? presentationMapElement.getAttribute("Uri") : null;

        return new MusicService(speaker, id, name, version, uri, secureUri, containerType, capabilities, policyAuth, policyPollInterval, presentationStringsUri, presentationMapUri);
    }
}

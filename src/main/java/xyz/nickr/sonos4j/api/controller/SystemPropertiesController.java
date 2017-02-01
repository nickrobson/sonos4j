package xyz.nickr.sonos4j.api.controller;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.service.ServiceRoute;

/**
 * @author Nick Robson
 */
public class SystemPropertiesController {

    private final Speaker speaker;

    public SystemPropertiesController(Speaker speaker) {
        this.speaker = speaker;
    }

    public String getString(String key) {
        ServiceRoute route = speaker.getRoute("/SystemProperties/Control", "GetString");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("VariableName", key));

        Map<String, Object> result = route.request(vars);
        return result.get("StringValue").toString();
    }

    public void setString(String key, String value) {
        ServiceRoute route = speaker.getRoute("/SystemProperties/Control", "SetString");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("VariableName", key));
        vars.add(new AbstractMap.SimpleEntry<>("StringValue", value));

        route.request(vars);
    }

    public void remove(String key) {
        ServiceRoute route = speaker.getRoute("/SystemProperties/Control", "Remove");

        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("VariableName", key));

        route.request(vars);
    }

    public String getWebCode(long accountType) {
        ServiceRoute route = speaker.getRoute("/SystemProperties/Control", "GetWebCode");
        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("AccountType", accountType));

        return route.request(vars).get("WebCode").toString();
    }

    public String provisionTrialAccount(int accountType) {
        ServiceRoute route = speaker.getRoute("/SystemProperties/Control", "ProvisionTrialAccount");
        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("AccountType", accountType));

        return route.request(vars).get("AccountUDN").toString();
    }

    public Map.Entry<String, Boolean> provisionCredentialedTrialAccount(int accountType, int accountId, String password) {
        ServiceRoute route = speaker.getRoute("/SystemProperties/Control", "ProvisionTrialAccount");
        List<Map.Entry<String, Object>> vars = new LinkedList<>();
        vars.add(new AbstractMap.SimpleEntry<>("AccountType", accountType));

        Map<String, Object> result = route.request(vars);
        String accountUDN = result.get("AccountUDN").toString();
        boolean isExpired = (boolean) result.get("IsExpired");

        return new AbstractMap.SimpleEntry<>(accountUDN, isExpired);
    }

}

package xyz.nickr.sonos4j.api.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xyz.nickr.sonos4j.Util;
import xyz.nickr.sonos4j.api.Speaker;
import xyz.nickr.sonos4j.api.model.Account;

/**
 * @author Nick Robson
 */
public class AccountsController {

    private final Speaker speaker;
    private Map<String, Account> accounts;

    public AccountsController(Speaker speaker) {
        this.speaker = speaker;
    }

    private void loadAccounts() {
        accounts = new HashMap<>();
        Account tuneIn = new Account(speaker,65031, "0", false, "", "", "", "", "", "");
        accounts.put(tuneIn.getSerialNumber(), tuneIn);

        String content = Util.getPageContent(speaker.getURL() + "/status/accounts");
        Document doc = Util.parseDocument(content);

        Element zpSupportInfo = Util.cast(Util.getChildList(doc), Element.class).get(0);
        Element accountsElement = Util.cast(Util.getChildList(zpSupportInfo), Element.class).get(0);

        List<Element> accountsList = Util.cast(Util.getChildList(accountsElement), Element.class);

        for (Element accountElement : accountsList) {
            try {
                Account account = Account.parse(speaker, accountElement);
                if (account != null)
                    accounts.put(account.getSerialNumber(), account);
            } catch (Exception ex) {
                System.err.println("Error while parsing element: " + Util.toString(accountElement));
                ex.printStackTrace();
            }
        }
    }

    public Map<String, Account> getAccountsMap() {
        if (accounts == null)
            loadAccounts();
        return Collections.unmodifiableMap(accounts);
    }

    public Optional<Account> getAccountByService(int serviceType) {
        for (Account acc : getAccountsMap().values())
            if (acc.getServiceType() == serviceType)
                return Optional.of(acc);
        return Optional.empty();
    }

}

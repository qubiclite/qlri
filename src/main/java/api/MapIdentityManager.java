package api;

import io.undertow.security.idm.IdentityManager;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.PasswordCredential;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapIdentityManager implements IdentityManager {

    private final Map<String, ApiAccount> accountMap = new HashMap<>();

    MapIdentityManager(final Iterable<ApiAccount> accounts) {
        for(ApiAccount account : accounts)
            accountMap.put(account.getPrincipal().getName(), account);
    }

    @Override
    public Account verify(Account account) {
        return account;
    }

    @Override
    public Account verify(String id, Credential credential) {
        Account account = getAccount(id);
        return verifyCredential(account, credential) ? account : null;
    }

    @Override
    public Account verify(Credential credential) {
        return null;
    }

    private boolean verifyCredential(Account account, Credential credential) {
        if (account instanceof ApiAccount && credential instanceof PasswordCredential) {
            char[] givenPassword = ((PasswordCredential) credential).getPassword();
            char[] expectedPassword = accountMap.get(account.getPrincipal().getName()).getPassword();
            return Arrays.equals(givenPassword, expectedPassword);
        }
        return false;
    }

    private Account getAccount(final String id) {
        if (accountMap.containsKey(id)) {
            return accountMap.get(id);
        }
        return null;
    }

    boolean hasAnyAccounts() {
        return accountMap.keySet().size() > 0;
    }
}
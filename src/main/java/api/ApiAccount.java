package api;

import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

public class ApiAccount implements Account {

    private final Principal principal;
    private final char[] password;

    public ApiAccount(String name, String password) {
        this.password = password.toCharArray();
        principal = new Principal() {
            @Override
            public String getName() {
                return name;
            }
        };
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public Set<String> getRoles() {
        return Collections.emptySet();
    }

    public char[] getPassword() {
        return password;
    }
}

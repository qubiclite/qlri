package api;

import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class ApiAccount implements Account {

    private Set<String> roles = new HashSet<>();

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
        return roles;
    }

    void addRole(String role) {
        roles.add(role);
    }

    char[] getPassword() {
        return password;
    }
}

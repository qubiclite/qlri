package api;

import commands.Command;
import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class PrivilegedApiAccount extends ApiAccount {

    public PrivilegedApiAccount(String name, String password) {
        super(name, password);
        for(Command c : Command.COMMANDS)
            addRole(c.getName());
    }
}

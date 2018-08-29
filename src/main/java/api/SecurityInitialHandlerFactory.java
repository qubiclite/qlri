package api;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;

import java.util.Collections;
import java.util.List;

public enum SecurityInitialHandlerFactory { ;

    public static HttpHandler create(HttpHandler httpHandler, MapIdentityManager identityManager) {
        if(!identityManager.hasAnyAccounts())
            return httpHandler;
        AuthenticationCallHandler authenticationCallHandler = new AuthenticationCallHandler(httpHandler);
        AuthenticationConstraintHandler authenticationConstraintHandler = new AuthenticationConstraintHandler(authenticationCallHandler);
        final List<AuthenticationMechanism> mechanisms = Collections.singletonList(new BasicAuthenticationMechanism("My Realm"));
        AuthenticationMechanismsHandler authenticationMechanismsHandler = new AuthenticationMechanismsHandler(authenticationConstraintHandler, mechanisms);
        return new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, authenticationMechanismsHandler);
    }
}
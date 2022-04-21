package org.pac4j.demo.spring;

import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Pac4jConfig {

    @Bean
    public Config config() {
        OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("javaDemoClient");
        oidcConfiguration.setSecret("secret");
        //oidcConfiguration.setPkceMethod(CodeChallengeMethod.S256);
        oidcConfiguration.setUseNonce(true);
        oidcConfiguration.setDiscoveryURI("https://qaautenticaciondigital.and.gov.co/.well-known/openid-configuration");
        //oidcClient.setPreferredJwsAlgorithm(JWSAlgorithm.RS256);
        oidcConfiguration.addCustomParam("prompt", "consent");

        final OidcClient oidcClient = new OidcClient(oidcConfiguration);
        final Clients clients = new Clients("http://localhost:9090/callback", oidcClient, new AnonymousClient());

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.addAuthorizer("mustBeAnon", new IsAnonymousAuthorizer("/?mustBeAnon"));
        config.addAuthorizer("mustBeAuth", new IsAuthenticatedAuthorizer("/?mustBeAuth"));
        config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.jsp$"));
        return config;
    }
}

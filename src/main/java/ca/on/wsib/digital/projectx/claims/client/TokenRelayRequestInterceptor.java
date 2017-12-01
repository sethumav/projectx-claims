package ca.on.wsib.digital.projectx.claims.client;

import ca.on.wsib.digital.projectx.claims.security.oauth2.AuthorizationHeaderUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class TokenRelayRequestInterceptor implements RequestInterceptor {

    public static final String AUTHORIZATION = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        template.header(AUTHORIZATION, AuthorizationHeaderUtil.getAuthorizationHeader());
    }
}

package com.sobow.secureweb.security;

import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationResult authorizationResult = authorize(authentication, object);
        return new AuthorizationDecision(authorizationResult.isGranted());
    }
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        
        Authentication auth = authentication.get();
        
        if (auth == null || !auth.isAuthenticated()) {return new AuthorizationDecision(false);}
        
        Object principal = auth.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) principal;
            String salary = customUserDetails.getUser().getProfile().getSalary().toString();
            if (salary.contains("9")) {
                return new AuthorizationDecision(true);
            } else {
                return new AuthorizationDecision(false);
            }
        }
        return new AuthorizationDecision(false);
    }
}

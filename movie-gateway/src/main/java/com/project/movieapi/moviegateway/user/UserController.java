package com.project.movieapi.moviegateway.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/profile")
    public Map<String, Object> getUser(@AuthenticationPrincipal OidcUser principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", principal.getFullName());
        response.put("email", principal.getEmail());
        response.put("picture", principal.getPicture());
        return response;
    }
}
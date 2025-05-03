package com.saninnovationslab.authws.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest model) {
        final TokenPair tokenPair = authService.authenticate(model);
        final ResponseCookie responseCookie = ResponseCookie
                .from(AuthConstant.REFRESH_TOKEN_PREFIX, tokenPair.getRefreshToken())
                .httpOnly(true).maxAge(AuthConstant.REFRESH_TOKEN_MAX_AGE)
                .secure(AuthConstant.IS_SECURE).build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthenticationResponse(tokenPair.getAccessToken()));
    }

    @PostMapping("refresh")
    public AuthenticationResponse refreshAccessToken(HttpServletRequest httpServletRequest) {
        final Cookie cookie = WebUtils.getCookie(httpServletRequest, AuthConstant.REFRESH_TOKEN_PREFIX);
        if (cookie != null) {
            final String accessToken = authService.issueAccessToken(cookie.getValue());
            return new AuthenticationResponse(accessToken);
        } else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

}

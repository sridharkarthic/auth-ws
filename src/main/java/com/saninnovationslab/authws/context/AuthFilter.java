package com.saninnovationslab.authws.context;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saninnovationslab.authws.auth.AuthConstant;
import com.saninnovationslab.authws.auth.JwtUtility;
import com.saninnovationslab.authws.auth.TokenType;
import com.saninnovationslab.authws.userdetail.UserBasicDetailModel;
import com.saninnovationslab.authws.userdetail.UserRole;
import com.saninnovationslab.authws.userdetail.UserStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;
  
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        boolean isOpenResource = AuthConstant.isOpenResource(requestUri);

        String refererHeader = request.getHeader("referer");
        boolean isRefererSwgger = false;

        if (refererHeader != null) {
            isRefererSwgger = AuthConstant.isRefererSwagger(refererHeader);
        }

        if (!isOpenResource) {

            String accessToken = request.getHeader(AuthConstant.ACCESS_TOKEN_PREFIX);
            TokenType tokenType = TokenType.ACCESS_TOKEN;

            // TO-DO Disable when deploying to the production
            if (isRefererSwgger) {
            Cookie cookie = Arrays.stream(request.getCookies())
            .filter(c -> c.getName().equals(AuthConstant.REFRESH_TOKEN_PREFIX))
            .findFirst()
            .get();
            accessToken = cookie.getValue();
            tokenType = TokenType.REFRESH_TOKEN;
            }

            if (accessToken == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getWriter(),
                        new AuthError(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value()));
                return;
            }

            DecodedJWT decodedJWT;
            try {
                decodedJWT = jwtUtility.verifyToken(accessToken, tokenType);
            } catch (JWTVerificationException exception) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getWriter(),
                        new AuthError(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value()));
                return;
            }

            UserBasicDetailModel userBasicDetailModel = new UserBasicDetailModel();
            userBasicDetailModel.setId(Integer.parseInt(decodedJWT.getSubject()));
            userBasicDetailModel.setUsername(decodedJWT.getClaim("username").asString());
            userBasicDetailModel.setRole(UserRole.valueOf(decodedJWT.getClaim("role").asString()));
            userBasicDetailModel.setStatus(UserStatus.valueOf(decodedJWT.getClaim("status").asString()));
            request.setAttribute(AuthConstant.CONTEXT_USER, userBasicDetailModel);
        }

        filterChain.doFilter(request, response);
    }

}

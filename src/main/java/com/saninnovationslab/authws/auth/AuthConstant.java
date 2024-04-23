package com.saninnovationslab.authws.auth;

public class AuthConstant {
    public static final String REFRESH_TOKEN_PREFIX = "refresh-token";
    public static final String ACCESS_TOKEN_PREFIX = "Authorization";
    public static final long REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24;
    public static final long REFRESH_TOKEN_EXPIRY = 1000 * 60 * 60 * 24;
    public static final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 10;
    public static final String PRIVATE_KEY_PATH = "classpath:pem/private.pem";
    public static final String PUBLIC_KEY_PATH = "classpath:pem/public.pem";
    public static final String JWT_ISSUER = "auth-ws";
    public static final String CONTEXT_USER = "context-user";

    public static final boolean IS_SECURE = false;

    public static boolean isOpenResource(String requestUri) {
        return requestUri.contains("create/user") || requestUri.contains("authenticate")
                || requestUri.contains("refresh") || requestUri.contains("swagger")
                || requestUri.contains("api-docs");
    }
}

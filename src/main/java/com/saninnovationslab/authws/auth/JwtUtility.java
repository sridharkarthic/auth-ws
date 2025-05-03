package com.saninnovationslab.authws.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.saninnovationslab.authws.userdetail.UserDetail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtUtility {

    @Autowired
    private RsaUtility rsaUtility;

    public String generateToken(UserDetail userDetail, long expiry, TokenType tokenType) throws JWTCreationException {
        return JWT.create().withIssuer(AuthConstant.JWT_ISSUER)
                .withSubject(userDetail.getId().toString())
                .withPayload(getPayload(userDetail))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expiry))
                .sign(rsaUtility.getEncryptionAlgorithm(tokenType));
    }

    public DecodedJWT verifyToken(String token, TokenType tokenType) throws JWTVerificationException {
        return JWT.require(rsaUtility.getDecryptionAlgorithm(tokenType)).withIssuer(AuthConstant.JWT_ISSUER).build()
                .verify(token);
    }

    public String generateAccessToken(DecodedJWT decodedJWT) throws JWTCreationException {
        return JWT.create().withIssuer(AuthConstant.JWT_ISSUER)
                .withSubject(decodedJWT.getSubject())
                .withPayload(getPayload(decodedJWT))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + AuthConstant.ACCESS_TOKEN_EXPIRY))
                .sign(rsaUtility.getEncryptionAlgorithm(TokenType.ACCESS_TOKEN));
    }

    private Map<String, String> getPayload(UserDetail userDetail) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("role", userDetail.getRole().name());
        payload.put("status", userDetail.getStatus().name());
        return payload;
    }

    private Map<String, String> getPayload(DecodedJWT decodedJWT) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("role", decodedJWT.getClaim("role").asString());
        payload.put("status", decodedJWT.getClaim("status").asString());
        return payload;
    }

}

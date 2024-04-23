package com.saninnovationslab.authws.auth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.auth0.jwt.algorithms.Algorithm;

@Service
public class RsaUtility {

    @Value(AuthConstant.PRIVATE_KEY_PATH)
    Resource privateKeyResource;

    @Value(AuthConstant.PUBLIC_KEY_PATH)
    Resource publicKeyResource;

    private RSAPrivateKey readPKCS8PrivateKey(String key) throws GeneralSecurityException, IOException {
        String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private RSAPublicKey readX509PublicKey(String key) throws GeneralSecurityException, IOException {
        String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "").replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private String getKey(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public Algorithm getEncryptionAlgorithm() {
        RSAPrivateKey rsaPrivateKey;
        RSAPublicKey rsaPublicKey;
        try {
            rsaPrivateKey = readPKCS8PrivateKey(getKey(privateKeyResource.getInputStream()));
            rsaPublicKey = readX509PublicKey(getKey(publicKeyResource.getInputStream()));
            return Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public Algorithm getDecryptionAlgorithm() {
        RSAPublicKey rsaPublicKey;
        try {
            rsaPublicKey = readX509PublicKey(getKey(publicKeyResource.getInputStream()));
            return Algorithm.RSA256(rsaPublicKey, null);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

}

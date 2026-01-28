package com.portfolio.app.util;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyUtil {

    public  PrivateKey loadPrivateKey(InputStream is) throws Exception {
        String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public  PublicKey loadPublicKey(InputStream is) throws Exception {
        String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }

    public  KeyPair loadKeyPair(InputStream publicKeyIs, InputStream privateKeyIs) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyIs);
        PrivateKey privateKey = loadPrivateKey(privateKeyIs);
        return new KeyPair(publicKey, privateKey);
    }
}

package com.portfolio.app.service;

import com.portfolio.app.util.KeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtUtility {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtUtility() throws Exception {
        this.privateKey = KeyUtil.loadPrivateKey("src/main/resources/keys/private_key.pem");
        this.publicKey = KeyUtil.loadPublicKey("src/main/resources/keys/public_key.pem");
    }


    public String generateToken(Map<String, Object> extraClaims, String username, long expiryMillis) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMillis))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
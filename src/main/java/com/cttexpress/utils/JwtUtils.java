package com.cttexpress.utils;

import com.cttexpress.persistence.ApiClientItem;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.binary.Base64;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.UUID;

public class JwtUtils {


    public static JwtBuilder jwsGenerate(ApiClientItem apiClientItem, String uri, String[] requestedScopes) throws Throwable {

        try {
            Base64 base64 = new Base64();
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(base64.decode(apiClientItem.getPrivkeyb4()));
            KeyFactory generator = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = generator.generatePrivate(privateKeySpec);

            final long MINUTES = apiClientItem.getTokenExpirationMinutes() * 60 * 1000; // in milli-seconds.
            Date issueTime = new Date();
            Date expirationTime = new Date(issueTime.getTime() + 1 * MINUTES);

            JwtBuilder jwtBuilder = Jwts.builder()
                    .setHeaderParam("kid", apiClientItem.getKeypairId())
                    .setIssuer(uri)
                    .setSubject(apiClientItem.getSubject())
                    .setAudience(apiClientItem.getAudience())
                    .setExpiration(expirationTime)
                    .setNotBefore(issueTime)
                    .setIssuedAt(issueTime)
                    .setId(UUID.randomUUID().toString())
                    .claim("scp", requestedScopes)
                    .claim("cid", apiClientItem.getClientId())
                    .signWith(privateKey);

            //jwtBuilder.compact();
            return jwtBuilder;

        } catch (Throwable ex) {
            throw ex;
        }
    }
}

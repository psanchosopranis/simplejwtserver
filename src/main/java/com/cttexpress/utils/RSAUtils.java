package com.cttexpress.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RSAUtils {

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.RS256;

    protected KeyPair keyPair;
    protected String keypairId;
    protected String signatureAlgorithm;

    public RSAUtils() {
        this.keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        this.keypairId = UUID.randomUUID().toString();
        this.signatureAlgorithm = SIGNATURE_ALGORITHM.getFamilyName();
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void newKeyPair() {
        this.keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    public String getKeypairId() {
        return keypairId;
    }

    public void newKeypairId() {
        this.keypairId = UUID.randomUUID().toString();
    }

    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    public String getPrivateKeyAsBase64String() {
        Base64 base64 = new Base64();
        return base64.encodeBase64String(this.keyPair.getPrivate().getEncoded());
    }

    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }

    public String getPublicKeyAsBase64String() {
        Base64 base64 = new Base64();
        return base64.encodeBase64String(this.keyPair.getPublic().getEncoded());
    }

    public static String getPublicKeyAsPEMfile(String publicKeyAsBase64String) {
        return getPublicKeyAsPEMfile(publicKeyAsBase64String, false);
    }

    public static String getPublicKeyAsPEMfile(String publicKeyAsBase64String, boolean asBinary) {
        List<String> ret = splitEqually(publicKeyAsBase64String, 64);
        String pemContent = "-----BEGIN PUBLIC KEY-----\n";
        for (String segmento: ret) {
            pemContent += segmento + "\n";
        }
        pemContent += "-----END PUBLIC KEY-----";
        if (asBinary) {
            return Base64.encodeBase64String(pemContent.getBytes());
        } else {
            return pemContent;
        }
    }

    public static RSAPublicKey getPublicKeyFromB64encoded(String publicKeyAsBase64String) throws Throwable {
        try {
            byte[] encoded = Base64.decodeBase64(publicKeyAsBase64String);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
            return pubKey;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    public static RSAPrivateKey getPrivateKeyFromB64encoded(String privateKeyAsBase64String) throws Throwable {
        try {
            byte[] encoded = Base64.decodeBase64(privateKeyAsBase64String);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
            return privKey;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    public static List<String> splitEqually(String text, int size) {
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }
}

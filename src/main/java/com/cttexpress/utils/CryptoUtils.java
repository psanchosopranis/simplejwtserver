package com.cttexpress.utils;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.UUID;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_1;

public class CryptoUtils {

    public static String newClientId() {
        return new DigestUtils(MD5).digestAsHex(UUID.randomUUID().toString());
    }

    public static String newClientSecret() {
        return new DigestUtils(SHA_1).digestAsHex(UUID.randomUUID().toString());
    }

}

package com.cttexpress.models;

import com.cttexpress.utils.CryptoUtils;
import com.cttexpress.utils.RSAUtils;
import java.util.Date;

public class ApiClientFactory {

    public static ApiClient getInstance(
            String subject,
            String audience,
            String[] scopes,
            int tokenExpirationMinutes,
            String status) throws Throwable {

        ApiClient instance = new ApiClient();

        try {
            instance.clientId = CryptoUtils.newClientId();
            instance.clientSecret = CryptoUtils.newClientSecret();

            instance.subject = subject;
            instance.audience = audience;
            instance.scopes = scopes;

            RSAUtils rsaUtils = new RSAUtils();

            instance.sigalg = rsaUtils.getSignatureAlgorithm();
            // ------------ IMPORTANTE -----------------------
            // La librería https://github.com/jwtk/jjwt
            // NO Permite obtener los Claims si no se verifica
            // la firma y para ello requiere conocer la clave
            // pública.
            // Por ello, a diferencia de un escenario real en
            // el que las claves tentrán un ID único, se
            // persistirán aparte (NO EN LA ENTRADA del
            // ApiClient, y se recuperarán mediante ese ID,
            // en esta POC se pondrá como KID el CID.
            // En un escenario real se usará ese KID para
            // recuperar la clave pública.
            //instance.keypairId = rsaUtils.getKeypairId();
            instance.keypairId = instance.clientId;
            instance.privkeyb4 = rsaUtils.getPrivateKeyAsBase64String();
            instance.pubkeyb4 = rsaUtils.getPublicKeyAsBase64String();

            instance.tokenExpirationMinutes = tokenExpirationMinutes;

            instance.since = new Date().toString();
            instance.status = status;

        } catch (Throwable ex) {
            throw ex;
        }
        return instance;
    }
}

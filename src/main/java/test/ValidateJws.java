package test;

import com.cttexpress.utils.RSAUtils;
import io.jsonwebtoken.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ValidateJws {

    public static void main(String[] args) {

        try {
            String pubkeyB64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl3Kdve6MFXRXPUZ1w8DMdkI7uF7AUf46ufXopcyUx3pmPXYTEvUcrIfhITcmNEmYnGOiHNGlVVUAILbUDr7NfggTIrunIaB2ZopZfC7m2s++yfQ1beWXSYeswRq57Hcne24Tf9GhfYt/4sBzkOkL8b/lQopE9IiZ4XlwmMftWWoRtDLA7tsmA1eQ29NFRGyHtGLRVD8XPeOrwCevx6JExfzoWk8XVpaNqi2ABGOf5wYYgUi9V44xL0Rj3nbR5XcxR1qBaDuIEkSpgheZIJY/Sk+cEtDevCEHr0VyrKZUsHd9th7mmS4VeqCz1jUSkeM5IWGYrqo12DvNmRO+MfEFmQIDAQAB";
            System.out.println(RSAUtils.getPublicKeyAsPEMfile(pubkeyB64));
            String jwsString = "eyJraWQiOiJkZDk4YzQ5Mi05YWIwLTQ2YTItODlhZi02YTZhZmEzMjAzYjkiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvb2F1dGgyL2RlZmF1bHQvdjEvdG9rZW4iLCJzdWIiOiJPUkFDTEUgQ1JNIEFQSSBBR0VOQ0lBUyBTR0MiLCJhdWQiOiJhcGk6Ly9zZ2MtYWdlbmNpZXMiLCJleHAiOjE1OTEwMTI0NDgsIm5iZiI6MTU5MTAxMDY0OCwiaWF0IjoxNTkxMDEwNjQ4LCJqdGkiOiIyYzdjMmM3Yi0wMmRjLTQ1ZDAtYjhiNC03OWU4NWMyMzA1ZWMiLCJzY3AiOlsib3JhY2xlLWNybS1maXJzdC1zY29wZSIsIm9yYWNsZS1jcm0tc2Vjb25kLXNjb3BlIl0sImNpZCI6IjU4ZmE0M2EyYTViMWM3NDM1Y2FlMGM4NTAzZDY0ZjUzIn0.eU120KqMRyB042ToU3jq04wiuRCZMH_BsZisiY3H1aIr6bxs1Xedki22QeQLIB5a0tZ2pOEg0bL8dinDL5ijJGHkuA7O3VaKlIGwonDSgOM7jFPxWsgzS1H_LXZ1xHf-o3BtK2Zl2bdjYIEmHqtgVOBDgsbKWAH2Binkig2kv9TME9ow0X84NoFSNSKX44leeJHQFkYKxLLuRxuktAyHfd-Ne3PN-7Z-Zryui9v2eyxRXIBCXGDEarX9Z3XFMjzvKxDamStd8YlgyaumaYkxE-zjxTZ8VxlktoH3FymP7ue9CNy84bOzL3jmGOWKJpPwmFejkkpt1GzuT7Q0vF6O2A";

            long seconds = 1 * 60; // 1 minuto
            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    //.setSigningKey(RSAUtils.getPublicKeyFromB64encoded(pubkeyB64))
                    .setAllowedClockSkewSeconds(seconds)
                    .build()
                    .parseClaimsJws(jwsString);


            Claims jwsBodyClaims = jwsClaims.getBody();
            System.out.println("iss: " + jwsBodyClaims.getIssuer());
            System.out.println("sub: " + jwsBodyClaims.getSubject());
            System.out.println("aud: " + jwsBodyClaims.getAudience());
            System.out.println("exp: " + jwsBodyClaims.getExpiration().toString());
            System.out.println("iat: " + jwsBodyClaims.getIssuedAt().toString());
            System.out.println("nbf: " + jwsBodyClaims.getNotBefore().toString());
            System.out.println("jti: " + jwsBodyClaims.getId());

            Object scopes = jwsBodyClaims.get("scp");
            System.out.println(scopes.getClass().getName());
            System.out.println("scp:");
            for (String scope: (ArrayList<String>) scopes) {
                System.out.println("\t" + scope);
            };
            System.out.println("cid: " + jwsBodyClaims.get("cid", String.class));

            Set<Map.Entry<String, Object>> entrySet = jwsBodyClaims.entrySet();

            for (Map.Entry entry : entrySet) {
                String key = (String) entry.getKey();
                System.out.println("Key[" + key + "]");
                Object value = entry.getValue();
                if (value instanceof String || value instanceof Integer) {
                    System.out.println("Valor[" + String.valueOf(value) + "] Valor Class[" + value.getClass().getName() + "]");
                } else {
                    System.out.println("Valor Class[" + value.getClass().getName() + "]");
                }
            }

        } catch (io.jsonwebtoken.ExpiredJwtException tex) {
            System.out.println("Token Expirado: " + tex.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException sex) {
            System.out.println("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}

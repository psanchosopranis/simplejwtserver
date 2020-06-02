package com.cttexpress.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class BasicAuth {

    protected String user;
    protected String password;
    protected boolean builtOK;

    public BasicAuth(String auth) {
        if (auth == null) {
            this.user = "N/D";
            this.password = "N/D";
            this.builtOK = false;
        } else {
            auth = auth.replaceAll("\\s", "@");
            String[] authComponents = auth.split("@");
            if (authComponents.length != 2 && !"BASIC".equalsIgnoreCase(authComponents[0])) {
                this.user = "N/D";
                this.password = "N/D";
                this.builtOK = false;
            } else {
                String basicAuthStringDecoded = null;
                try {
                    basicAuthStringDecoded = new String(Base64.decodeBase64(authComponents[1]), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    basicAuthStringDecoded = new String(Base64.decodeBase64(authComponents[1]));
                }
                String[] userAndPassword = basicAuthStringDecoded.split(":");
                if (userAndPassword.length != 2) {
                    this.user = "N/D";
                    this.password = "N/D";
                    this.builtOK = false;
                } else {
                    this.user = userAndPassword[0];
                    this.password = userAndPassword[1];
                    this.builtOK = true;
                }
            }
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isBuiltOK() {
        return builtOK;
    }
}

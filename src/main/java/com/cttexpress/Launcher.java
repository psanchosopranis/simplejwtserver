package com.cttexpress;

import org.apache.catalina.Context;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

public class Launcher {

    private static final String JERSEY_SERVLET_NAME = "jersey-container-servlet";

    public static void main(String[] args) throws Exception {
        new Launcher().start();
    }

    void start() throws Exception {

        String  httpScheme = System.getenv("HTTP_SCHEME");
        System.out.println("Env HTTP_SCHEME[" + httpScheme + "]");
        if (httpScheme != null && httpScheme.trim().equalsIgnoreCase("HTTP")) {
            httpScheme = "HTTP";
        } else if (httpScheme != null && httpScheme.trim().equalsIgnoreCase("HTTPS")) {
            httpScheme = "HTTPS";
        } else {
            httpScheme = "HTTP";
            System.out.println("Default HTTP_SCHEME[" + httpScheme + "]");
        }

        String contextPath = "";
        String appBase = ".";

        Tomcat tomcat = new Tomcat();
        Service service = tomcat.getService();
        if (httpScheme.equalsIgnoreCase("HTTPS")) {
            service.addConnector(getSslConnector());
        } else {
            service.addConnector(getNoSslConnector());
        }


        tomcat.getHost().setAppBase(appBase);

        Context context = tomcat.addContext(contextPath, appBase);
        Tomcat.addServlet(context, JERSEY_SERVLET_NAME,
                new ServletContainer(new JerseyConfiguration()));
        context.addServletMappingDecoded("/oauth2/default/v1/*", JERSEY_SERVLET_NAME);

        context.addApplicationListener(CommonAppServletContextListener.class.getName());

        tomcat.start();
        tomcat.getServer().await();
    }

    private static Connector getSslConnector() {

        String  portAsString = System.getenv("PORT");
        System.out.println("Env PORT[" + portAsString + "]");
        if (portAsString == null || portAsString.trim().length() == 0) {
            portAsString = "9090";
            System.out.println("Default PORT[" + portAsString + "]");
        }

        String  keyAlias =  System.getenv("KEYALIAS");
        System.out.println("Env KEYALIAS[" + keyAlias + "]");
        String  keystorePass =  System.getenv("KEYSTOREPASS");
        System.out.println("Env KEYSTOREPASS[" + keystorePass + "]");
        String  keystoreFile =  System.getenv("KEYSTOREFILE");
        System.out.println("Env KEYSTOREFILE[" + keystoreFile + "]");

        Integer port = new Integer(portAsString);

        Connector connector = new Connector();
        connector.setPort(port.intValue());
        connector.setSecure(true);
        connector.setScheme("https");
        connector.setAttribute("keyAlias", keyAlias);
        connector.setAttribute("keystorePass", keystorePass);
        connector.setAttribute("keystoreType", "JKS");
        connector.setAttribute("keystoreFile", keystoreFile);
        connector.setAttribute("clientAuth", "false");
        connector.setAttribute("protocol", "HTTP/1.1");
        connector.setAttribute("sslProtocol", "TLS");
        connector.setAttribute("maxThreads", "200");
        connector.setAttribute("protocol", "org.apache.coyote.http11.Http11AprProtocol");
        connector.setAttribute("SSLEnabled", true);
        return connector;
    }

    private static Connector getNoSslConnector() {

        String  portAsString = System.getenv("PORT");
        System.out.println("Env PORT[" + portAsString + "]");
        if (portAsString == null || portAsString.trim().length() == 0) {
            portAsString = "8080";
            System.out.println("Default PORT[" + portAsString + "]");
        }

        Integer port = new Integer(portAsString);

        Connector connector = new Connector();
        connector.setPort(port.intValue());
        connector.setSecure(false);
        connector.setScheme("http");
        connector.setAttribute("protocol", "HTTP/1.1");
        connector.setAttribute("maxThreads", "200");
        connector.setAttribute("protocol", "org.apache.coyote.http11.Http11AprProtocol");
        connector.setAttribute("SSLEnabled", false);
        return connector;
    }
}
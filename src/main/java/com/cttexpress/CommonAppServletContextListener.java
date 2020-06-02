package com.cttexpress;

import com.cttexpress.persistence.PersistenceController;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;

public class CommonAppServletContextListener
        implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("\n------------------------------------------");
        System.out.println("ServletContextListener destroyed");
        System.out.println("------------------------------------------\n");
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("\n------------------------------------------");
        System.out.println("ServletContextListener started");
        System.out.println("------------------------------------------\n");

        // Get persisntece.properties configuration
        try (InputStream input = CommonAppServletContextListener.class
                                .getClassLoader()
                                .getResourceAsStream("config/persistence.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("No se ha podido cargar el archivo de propiedades '/config/persistence.properties'.");
                System.exit(1);
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the properties value and print it out
            System.out.println("\nPropiedades de configuración recuperadas para 'Persistence Controller': ");
            System.out.println("\t - apiClientsYamlFilePath[" + prop.getProperty("apiClientsYamlFilePath") + "]");
            System.out.println("\t - tokensYamlFilePath[" + prop.getProperty("tokensYamlFilePath") + "]");

            System.out.println("\nConfiguración resultante para 'Persistence Controller': ");
            String apiClientsYamlFilePath = System.getenv("APICLIENTS_YAML_PATH");
            if (apiClientsYamlFilePath == null || apiClientsYamlFilePath.isEmpty()) {
                apiClientsYamlFilePath = prop.getProperty("apiClientsYamlFilePath");
                System.out.println("\t - apiClientsYamlFilePath (Desde archivo de Properties) [" + apiClientsYamlFilePath + "]\n");
            } else {
                System.out.println("\t - apiClientsYamlFilePath (Desde Variables de Entorno.) [" + apiClientsYamlFilePath + "]\n");
            }

            String tokensYamlFilePath = System.getenv("TOKENS_YAML_PATH");
            if (tokensYamlFilePath == null || tokensYamlFilePath.isEmpty()) {
                tokensYamlFilePath = prop.getProperty("tokensYamlFilePath");
                System.out.println("\t - tokensYamlFilePath (Desde archivo de Properties) [" + tokensYamlFilePath + "]\n");
            } else {
                System.out.println("\t - tokensYamlFilePath (Desde Variables de Entorno.) [" + tokensYamlFilePath + "]\n");
            }


            // Create your objects
            PersistenceController persistenceController =
                    new PersistenceController(apiClientsYamlFilePath, tokensYamlFilePath);
            persistenceController.init();

            sce.getServletContext().setAttribute("persistenceController", (Object) persistenceController);

        } catch (Throwable ex) {
            ex.printStackTrace();
            System.exit(2);
        }

    }
}
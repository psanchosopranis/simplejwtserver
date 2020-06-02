package com.cttexpress.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class PersistenceController {

    protected String apiClientsYamlFilePath;
    protected String tokensYamlFilePath;
    protected ApiClientsTable apiClientTable;

    private PersistenceController() {
        // NOT ACCESIBLE
    }

    public PersistenceController(String apiClientsYamlFilePath, String tokensYamlFilePath) {
        this.apiClientsYamlFilePath = apiClientsYamlFilePath;
        this.tokensYamlFilePath = tokensYamlFilePath;
    }

    public String getApiClientsYamlFilePath() { return apiClientsYamlFilePath; }

    public String getTokensYamlFilePath() { return tokensYamlFilePath; }

    public ApiClientsTable getApiClientTable() {
        return apiClientTable;
    }

    public void init() {
        try {
            File apiClientTableFile = new File(this.apiClientsYamlFilePath);
            if (!apiClientTableFile.exists()) {
                this.apiClientTable = generateInitialApiClientsTable();
            } else {
                this.apiClientTable = retrieveApiClientsTable();
            }
            System.out.println("PersistenceController: apiClientTable cargada satisfactoriamente");
            System.out.println("PersistenceController: apiClientTable contiene [ "
                    + String.valueOf(this.apiClientTable.getApiClientsHM().size()) + " ] entradas actualmente");
            System.out.println(this.apiClientTable.toString());

        } catch (Throwable ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private ApiClientsTable generateInitialApiClientsTable() throws Throwable {

        ApiClientItem apiClientItem = null;
        ApiClientsTable apiClientsTable = null;
        try {
            String nullClientId = "@NULL";
            apiClientItem = new ApiClientItem();
            apiClientsTable = new ApiClientsTable();
            apiClientItem.setClientId(nullClientId);
            apiClientsTable.getApiClientsHM().put(nullClientId, apiClientItem);
            persistApiClientsTable(apiClientsTable);
        } catch (Throwable ex) {
            throw ex;
        }
        return apiClientsTable;
    }

    public void persistApiClientsTable(ApiClientsTable apiClientsTable) throws Throwable {

        Yaml yaml = null;

        try {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            options.setIndent(4);
            yaml = new Yaml(options);
            String apiClientsAsYamlString = yaml.dump(apiClientsTable);
            persist(this.apiClientsYamlFilePath, apiClientsAsYamlString.getBytes("utf-8"));
        } catch (Throwable ex) {
            throw ex;
        }
    }

    public ApiClientsTable retrieveApiClientsTable() throws Throwable {

        Yaml yaml = null;

        ApiClientsTable apiClientsTable = null;
        try {
            FileInputStream archivoYAML = new FileInputStream(this.apiClientsYamlFilePath);
            yaml = new Yaml(new Constructor(ApiClientsTable.class));
            apiClientsTable = (ApiClientsTable) yaml.load(archivoYAML);
        } catch (Throwable ex) {
            throw ex;
        }
        return apiClientsTable;
    }

    protected void persist(String filePath, byte[] data) throws Throwable {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
            fos.flush();
            // System.out.println("---> PERSIST " + filePath );
            // System.out.println("---> DATA\n" + new String(data) + "\n");
        } catch (Throwable ex) {
            throw ex;
        }
    }

    protected byte[] retrieve(String filePath) throws Throwable {
        byte[] data = new byte[(int) filePath.length()];
        try (FileInputStream fis = new FileInputStream(filePath)) {
            fis.read(data);
        } catch (Throwable ex) {
            throw ex;
        }
        return data;
    }
}

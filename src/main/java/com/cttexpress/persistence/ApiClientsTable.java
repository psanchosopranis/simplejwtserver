package com.cttexpress.persistence;

import com.cttexpress.models.ApiClient;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;

public class ApiClientsTable {

    protected HashMap<String, ApiClientItem> apiClientsHM;

    public ApiClientsTable(HashMap<String, ApiClientItem> apiClientsHM) {
        this.apiClientsHM = apiClientsHM;
    }

    public ApiClientsTable() {
        this.apiClientsHM = new HashMap<String, ApiClientItem>();
    }

    public HashMap<String, ApiClientItem> getApiClientsHM() {
        return apiClientsHM;
    }

    public void setApiClientsHM(HashMap<String, ApiClientItem> apiClientsHM) {
        this.apiClientsHM = apiClientsHM;
    }

    public void addApiClientInstance (ApiClient apiClient) {
        ApiClientItem apiClientItem = new ApiClientItem();
        apiClientItem.setClientId(apiClient.getClientId());
        apiClientItem.setClientSecret(apiClient.getClientSecret());
        apiClientItem.setSubject(apiClient.getSubject());
        apiClientItem.setAudience(apiClient.getAudience());
        apiClientItem.setScopes(apiClient.getScopes());
        apiClientItem.setSigalg(apiClient.getSigalg());
        apiClientItem.setKeypairId(apiClient.getKeypairId());
        apiClientItem.setPrivkeyb4(apiClient.getPrivkeyb4());
        apiClientItem.setPubkeyb4(apiClient.getPubkeyb4());
        apiClientItem.setTokenExpirationMinutes(apiClient.getTokenExpirationMinutes());
        apiClientItem.setSince(apiClient.getSince());
        apiClientItem.setStatus(apiClient.getStatus());
        this.getApiClientsHM().put(apiClient.getClientId(), apiClientItem);
    }

    public void updateApiClientInstance (ApiClientItem apiClientItem) {
        this.getApiClientsHM().put(apiClientItem.getClientId(), apiClientItem);
    }

    public ApiClientItem getApiClientItemByClientid (String clientId) {
        return this.getApiClientsHM().get(clientId);
    }

    public ApiClientItem removeApiClientItemByClientid (String clientId) {
        // Si la entrada existe se devuelve su estado anterior,
        // en caso contrario se devuelve 'null'
        ApiClientItem apiClientItem = this.getApiClientsHM().get(clientId);
        if (apiClientItem !=null) {
            this.getApiClientsHM().remove(clientId);
            return apiClientItem;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

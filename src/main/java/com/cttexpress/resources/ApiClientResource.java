package com.cttexpress.resources;

import com.cttexpress.config.AdminUser;
import com.cttexpress.config.User;
import com.cttexpress.config.UserTable;
import com.cttexpress.models.*;
import com.cttexpress.persistence.ApiClientItem;
import com.cttexpress.persistence.ApiClientsTable;
import com.cttexpress.persistence.PersistenceController;
import com.cttexpress.utils.BasicAuth;
import com.cttexpress.utils.CryptoUtils;
import com.cttexpress.utils.RSAUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("api-clients")
public class ApiClientResource {

    @Context
    ServletContext context;

    private static boolean isAdmin(String auth) {
        BasicAuth basicAuth = new BasicAuth(auth);
        if (!basicAuth.isBuiltOK()) return false;
        if (!UserTable.getUsersHM().containsKey((String) basicAuth.getUser())) return false;
        User user = UserTable.getUsersHM().get((String) basicAuth.getUser());
        if (!user.getRole().equalsIgnoreCase("ADMINISTRATOR")) return false;
        if (!user.getPassword().equalsIgnoreCase(basicAuth.getPassword())) return false;
        return true;
    }

    private static boolean isScopeListValid (String[] scopes) {
        // Nota si está vacío el Array se devolverá TRUE
        // En POST primero se comprueba que no esté vacío
        // En PUT se admite vacío.
        String patternString = "^[a-zA-Z0-9\\-]+$";
        Pattern pattern = Pattern.compile(patternString);
        boolean isValid = true;
        Matcher matcher = null;
        for (int i = 0; i < scopes.length ; i++) {
            matcher = pattern.matcher(scopes[i]);
            if (!matcher.matches()) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    private static ApiClient apiClientItemToApiClient(ApiClientItem apiClientItem) {
        ApiClient apiClient = new ApiClient();
        apiClient.setClientId(apiClientItem.getClientId());
        apiClient.setClientSecret(apiClientItem.getClientSecret());
        apiClient.setSubject(apiClientItem.getSubject());
        apiClient.setAudience(apiClientItem.getAudience());
        apiClient.setScopes(apiClientItem.getScopes());
        apiClient.setSigalg(apiClientItem.getSigalg());
        apiClient.setKeypairId(apiClientItem.getKeypairId());
        apiClient.setPrivkeyb4(apiClientItem.getPrivkeyb4());
        apiClient.setPubkeyb4(apiClientItem.getPubkeyb4());
        apiClient.setSince(apiClientItem.getSince());
        apiClient.setTokenExpirationMinutes(apiClientItem.getTokenExpirationMinutes());
        apiClient.setStatus(apiClientItem.getStatus().toUpperCase());
        return apiClient;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response apiClientCreation(
            @HeaderParam("Authorization") String authorization,
            @Context UriInfo uriInfo,
            ApiClientRequest apiClientRequest) {

        try {
            if (authorization == null || !isAdmin(authorization)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-401",
                                "invalid-credentials",
                                "Credenciales o usuario no válidos para esta función"))
                        .build();
            }

            boolean isApiClientRequestOK = false;
            if (apiClientRequest != null
                    && apiClientRequest.getScopes() != null
                    && apiClientRequest.getScopes().length > 0
                    && isScopeListValid(apiClientRequest.getScopes())
                    && apiClientRequest.getSubject() != null
                    && apiClientRequest.getSubject().trim().length() != 0
                    && apiClientRequest.getAudience() != null
                    && apiClientRequest.getAudience().trim().length() != 0
                    && apiClientRequest.getTokenExpirationMinutes() > 0
                    && apiClientRequest.getStatus() != null) {
                String status = apiClientRequest.getStatus();
                if (status.equalsIgnoreCase("ACTIVE")
                        || status.equalsIgnoreCase("INACTIVE")
                        || status.equalsIgnoreCase("LOCKED")) {
                    isApiClientRequestOK = true;
                } else {
                    isApiClientRequestOK = false;
                }
            } else {
                isApiClientRequestOK = false;
            }

            if (!isApiClientRequestOK) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-400",
                                "invalid-data-sent",
                                "Datos de solicitud incorrectos."))
                        .build();
            }

            Object persistenceControllerCtx = context.getAttribute("persistenceController");
            if (persistenceControllerCtx == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-500-ICTX",
                                "invalid-context-persistence",
                                "No se puede acceder al contexto de persistencia."))
                        .build();
            }

            PersistenceController persistenceController = (PersistenceController) persistenceControllerCtx;
            ApiClientsTable apiClientTable = persistenceController.getApiClientTable();

            ApiClient apiClient = null;
            try {
                apiClient = ApiClientFactory.getInstance(
                        apiClientRequest.getSubject(),
                        apiClientRequest.getAudience(),
                        apiClientRequest.getScopes(),
                        apiClientRequest.getTokenExpirationMinutes(),
                        apiClientRequest.getStatus().toUpperCase());
                apiClientTable.addApiClientInstance(apiClient);
                persistenceController.persistApiClientsTable(apiClientTable);
            } catch (Throwable ex) {
                String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-500",
                                "apiclient-creation-exception",
                                message))
                        .build();
            }
            URI uri = uriInfo.getAbsolutePathBuilder().path(apiClient.getClientId()).build();
            return Response
                    .created(uri)
                    .entity(apiClient).build();
        } catch (Throwable ex) {
            String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500",
                            "apiclient-creation-exception",
                            message))
                    .build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response apiClientRetrieveList(
            @HeaderParam("Authorization") String authorization,
            @PathParam("clientId") String clientId
    ) {

        if (authorization == null || !isAdmin(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-401",
                            "invalid-credentials",
                            "Credenciales o usuario no válidos para esta función"))
                    .build();
        }

        Object persistenceControllerCtx = context.getAttribute("persistenceController");
        if (persistenceControllerCtx == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500-ICTX",
                            "invalid-context-persistence",
                            "No se puede acceder al contexto de persistencia."))
                    .build();
        }

        PersistenceController persistenceController = (PersistenceController) persistenceControllerCtx;
        ApiClientsTable apiClientTable = persistenceController.getApiClientTable();

        Collection<ApiClientItem> apiClientsItemCollection = apiClientTable.getApiClientsHM().values();
        ApiClient[] apiClients = new ApiClient[apiClientTable.getApiClientsHM().size() - 1]; // No se devuelve el elemento @NULL
        int index = 0;
        for (ApiClientItem apiClientItem : apiClientsItemCollection) {
            if (apiClientItem.getClientId().equalsIgnoreCase("@NULL"))
                continue;
            apiClients[index++] = apiClientItemToApiClient(apiClientItem);
        }

        ApiClientList apiClientList = new ApiClientList();
        apiClientList.setData(apiClients);
        Pagination pagination = new Pagination();
        pagination.setPageLimit(apiClients.length);
        PageOffsets pageOffsets = new PageOffsets();
        pageOffsets.setCurrent(0);
        pageOffsets.setLast(0);
        pageOffsets.setNext(0);
        pagination.setPageOffsets(pageOffsets);
        apiClientList.setPagination(pagination);

        return Response
                .status(Response.Status.OK)
                .entity(apiClientList).build();
    }

    @Path("/{clientId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response apiClientRetrieve(
            @HeaderParam("Authorization") String authorization,
            @PathParam("clientId") String clientId
    ) {

        if (authorization == null || !isAdmin(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-401",
                            "invalid-credentials",
                            "Credenciales o usuario no válidos para esta función"))
                    .build();
        }

        Object persistenceControllerCtx = context.getAttribute("persistenceController");
        if (persistenceControllerCtx == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500-ICTX",
                            "invalid-context-persistence",
                            "No se puede acceder al contexto de persistencia."))
                    .build();
        }

        PersistenceController persistenceController = (PersistenceController) persistenceControllerCtx;
        ApiClientsTable apiClientTable = persistenceController.getApiClientTable();

        ApiClientItem apiClientItem = apiClientTable.getApiClientItemByClientid(clientId);
        if (apiClientItem != null) {
            ApiClient apiClient = apiClientItemToApiClient(apiClientItem);
            return Response
                    .status(Response.Status.OK)
                    .entity(apiClient).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-404",
                            "resource-not-found",
                            "El recurso solicitado no existe."))
                    .build();
        }
    }

    @Path("/{clientId}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    public Response apiClientUpdate(
            @HeaderParam("Authorization") String authorization,
            @PathParam("clientId") String clientId,
            ApiClientRequestUpdate apiClientRequestUpdate) {
        try {
            if (authorization == null || !isAdmin(authorization)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-401",
                                "invalid-credentials",
                                "Credenciales o usuario no válidos para esta función"))
                        .build();
            }

            boolean isApiClientRequestOK = false;
            if (apiClientRequestUpdate != null
                    && apiClientRequestUpdate.getScopes() != null
                    && isScopeListValid(apiClientRequestUpdate.getScopes())
                    && apiClientRequestUpdate.getSubject() != null
                    && apiClientRequestUpdate.getSubject().trim().length() != 0
                    && apiClientRequestUpdate.getAudience() != null
                    && apiClientRequestUpdate.getAudience().trim().length() != 0
                    && apiClientRequestUpdate.getTokenExpirationMinutes() > 0
                    && apiClientRequestUpdate.getStatus() != null) {
                String status = apiClientRequestUpdate.getStatus();
                if (status.equalsIgnoreCase("ACTIVE")
                        || status.equalsIgnoreCase("INACTIVE")
                        || status.equalsIgnoreCase("LOCKED")) {
                    isApiClientRequestOK = true;
                } else {
                    isApiClientRequestOK = false;
                }
            } else {
                isApiClientRequestOK = false;
            }

            if (!isApiClientRequestOK) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-400",
                                "invalid-data-sent",
                                "Datos de solicitud incorrectos."))
                        .build();
            }

            Object persistenceControllerCtx = context.getAttribute("persistenceController");
            if (persistenceControllerCtx == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-500-ICTX",
                                "invalid-context-persistence",
                                "No se puede acceder al contexto de persistencia."))
                        .build();
            }

            PersistenceController persistenceController = (PersistenceController) persistenceControllerCtx;
            ApiClientsTable apiClientTable = persistenceController.getApiClientTable();

            ApiClientItem apiClientItem = apiClientTable.getApiClientItemByClientid(clientId);
            if (apiClientItem != null) {
                try {
                    apiClientItem.setSubject(apiClientRequestUpdate.getSubject());
                    apiClientItem.setAudience(apiClientRequestUpdate.getAudience());
                    apiClientItem.setScopes(apiClientRequestUpdate.getScopes());
                    apiClientItem.setTokenExpirationMinutes(apiClientRequestUpdate.getTokenExpirationMinutes());
                    apiClientItem.setStatus(apiClientRequestUpdate.getStatus().toUpperCase());
                    apiClientItem.setSince(new Date().toString());

                    if (apiClientRequestUpdate.isRenewClientSecret()) {
                        apiClientItem.setClientSecret(CryptoUtils.newClientSecret());
                    }

                    if (apiClientRequestUpdate.isRenewKeyPair()) {
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
                        RSAUtils rsaUtils = new RSAUtils();
                        apiClientItem.setSigalg(rsaUtils.getSignatureAlgorithm());
                        //apiClientItem.setKeypairId(rsaUtils.getKeypairId());
                        apiClientItem.setKeypairId(apiClientItem.getClientId());
                        apiClientItem.setPrivkeyb4(rsaUtils.getPrivateKeyAsBase64String());
                        apiClientItem.setPubkeyb4(rsaUtils.getPublicKeyAsBase64String());
                    }

                    apiClientTable.updateApiClientInstance(apiClientItem);
                    persistenceController.persistApiClientsTable(apiClientTable);

                    ApiClient apiClient = apiClientItemToApiClient(apiClientItem);
                    return Response
                            .status(Response.Status.OK)
                            .entity(apiClient).build();
                } catch (Throwable ex) {
                    String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-500",
                                    "apiclient-creation-exception",
                                    message))
                            .build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-404",
                                "resource-not-found",
                                "El recurso solicitado no existe."))
                        .build();
            }
        } catch (Throwable ex) {
            String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500",
                            "apiclient-creation-exception",
                            message))
                    .build();
        }
    }

    @Path("/{clientId}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public Response apiClientDelete(
            @HeaderParam("Authorization") String authorization,
            @PathParam("clientId") String clientId
    ) {

        if (authorization == null || !isAdmin(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-401",
                            "invalid-credentials",
                            "Credenciales o usuario no válidos para esta función"))
                    .build();
        }

        Object persistenceControllerCtx = context.getAttribute("persistenceController");
        if (persistenceControllerCtx == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500-ICTX",
                            "invalid-context-persistence",
                            "No se puede acceder al contexto de persistencia."))
                    .build();
        }

        PersistenceController persistenceController = (PersistenceController) persistenceControllerCtx;
        ApiClientsTable apiClientTable = persistenceController.getApiClientTable();

        ApiClientItem apiClientItem = apiClientTable.removeApiClientItemByClientid(clientId);
        if (apiClientItem != null) {
            try {
                persistenceController.persistApiClientsTable(apiClientTable);
                ApiClient apiClient = apiClientItemToApiClient(apiClientItem);
                return Response
                        .status(Response.Status.OK)
                        .entity(apiClient).build();

            } catch (Throwable ex) {
                String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-500",
                                "apiclient-creation-exception",
                                message))
                        .build();
            }

        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-404",
                            "resource-not-found",
                            "El recurso solicitado no existe."))
                    .build();
        }
    }

}

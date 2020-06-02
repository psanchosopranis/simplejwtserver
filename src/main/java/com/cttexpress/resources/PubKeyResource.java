package com.cttexpress.resources;

import com.cttexpress.config.User;
import com.cttexpress.config.UserTable;
import com.cttexpress.models.*;
import com.cttexpress.persistence.ApiClientItem;
import com.cttexpress.persistence.ApiClientsTable;
import com.cttexpress.persistence.PersistenceController;
import com.cttexpress.utils.BasicAuth;
import com.cttexpress.utils.RSAUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;

@Path("pubkey")
public class PubKeyResource {

    @Context
    ServletContext context;

    private static boolean isAutorizedUser(String auth) {
        BasicAuth basicAuth = new BasicAuth(auth);
        if (!basicAuth.isBuiltOK()) return false;
        if (!UserTable.getUsersHM().containsKey((String) basicAuth.getUser())) return false;
        User user = UserTable.getUsersHM().get((String) basicAuth.getUser());
        if (!user.getRole().equalsIgnoreCase("ADMINISTRATOR") &&
                !user.getRole().equalsIgnoreCase("SYSTEM_USER")) return false;
        if (!user.getPassword().equalsIgnoreCase(basicAuth.getPassword())) return false;
        return true;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response introspectToken(@FormParam("keyId") String keyId,
                                    @HeaderParam("Authorization") String authorization,
                                    @Context UriInfo uriInfo) {

        try {
            URI uri = uriInfo.getAbsolutePathBuilder().build();

            if (!isAutorizedUser(authorization)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-401-VALD0",
                                "invalid-credentials",
                                "Credenciales no válidas para esta función"))
                        .build();
            }

            if (keyId == null || keyId.trim().length() == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-400-VALD",
                                "invalid-data-sent",
                                "Datos de solicitud incorrectos."))
                        .build();
            }

            Object persistenceControllerCtx = context.getAttribute("persistenceController");
            if (persistenceControllerCtx == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-500-VALD",
                                "invalid-context-persistence",
                                "No se puede acceder al contexto de persistencia."))
                        .build();
            }

            PersistenceController persistenceController = (PersistenceController) persistenceControllerCtx;
            ApiClientsTable apiClientTable = persistenceController.getApiClientTable();

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
            ApiClientItem apiClientItem = apiClientTable.getApiClientItemByClientid(keyId);

            if (apiClientItem != null) {
                try {
                    PubkeyResponse pubkeyResponse = new PubkeyResponse();
                    pubkeyResponse.setKeyId(keyId);
                    pubkeyResponse.setSigalg(apiClientItem.getSigalg());
                    pubkeyResponse.setPubkeyb4(apiClientItem.getPubkeyb4());
                    pubkeyResponse.setPubkeyAsPem(RSAUtils.getPublicKeyAsPEMfile(apiClientItem.getPubkeyb4(), true));

                    return Response
                            .status(Response.Status.OK)
                            .entity(pubkeyResponse).build();

                } catch (Throwable ex) {
                    String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-500-PBKY1",
                                    "pubkey-retrieve-exception",
                                    message))
                            .build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-404-PBKY1",
                                "pubkey-not-found",
                                "No se encuentra ninguna clave con el 'keyId' solicitado"))
                        .build();
            }

        } catch (Throwable ex) {
            String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500-PBKY",
                            "pubkey-retrieve-exception",
                            message))
                    .build();
        }
    }

}

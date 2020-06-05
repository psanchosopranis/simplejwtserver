package com.cttexpress.resources;

import com.cttexpress.models.ErrorResponseFactory;
import com.cttexpress.models.TokenResponse;
import com.cttexpress.models.TokenResponseFactory;
import com.cttexpress.persistence.ApiClientItem;
import com.cttexpress.persistence.ApiClientsTable;
import com.cttexpress.persistence.PersistenceController;
import com.cttexpress.persistence.TokenItem;
import com.cttexpress.utils.BasicAuth;
import com.cttexpress.utils.JwtUtils;
import com.cttexpress.utils.RSAUtils;
import io.jsonwebtoken.JwtBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("token")
public class TokenResource {

    @Context
    ServletContext context;

    protected boolean isScopeSetValid (String[] requestedScopes, String[] authorizedScopes) {
        Set<String> requestedScopesSet = new HashSet<String>(Arrays.asList(requestedScopes));
        Set<String> authorizedScopesSet = new HashSet<String>(Arrays.asList(authorizedScopes));
        return authorizedScopesSet.containsAll(requestedScopesSet);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newToken(@FormParam("grant_type") String grantType,
                             @FormParam("scopes") String scopes,
                             @HeaderParam("Authorization") String authorization,
                             @Context UriInfo uriInfo) {

        try {
            URI uri = uriInfo.getAbsolutePathBuilder().build();

            BasicAuth basicAuth = new BasicAuth(authorization);
            if (!basicAuth.isBuiltOK()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-401-TKN0",
                                "invalid-credentials",
                                "Credenciales no válidas para esta función"))
                        .build();
            }

            String clientId = basicAuth.getUser();
            String clientSecret = basicAuth.getPassword();

            if (grantType == null || !grantType.equalsIgnoreCase("client_credentials") ||
                scopes == null || scopes.trim().length() == 0 ) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-400-TKN0",
                                "invalid-data-sent",
                                "Datos de solicitud incorrectos."))
                        .build();
            }

            String patternString = "^[a-zA-Z0-9\\-,]+$";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(scopes);
            boolean matches = matcher.matches();
            if (!matches) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-400-TKN1",
                                "invalid-data-sent",
                                "Datos de solicitud (scopes) incorrectos."))
                        .build();
            }

            String[] requestedScopes = scopes.split(",");

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
                if (!apiClientItem.getClientSecret().equalsIgnoreCase(clientSecret)) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-401-TOKEN-TKN1",
                                    "invalid-credentials",
                                    "Credenciales no válidas para esta función"))
                            .build();
                }

                if (!apiClientItem.getStatus().equalsIgnoreCase("ACTIVE")) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-401-TOKEN-TKN2",
                                    "invalid-credentials",
                                    "Credenciales no disponibles (NO ACTIVAS) para esta función"))
                            .build();
                }

                if (!isScopeSetValid (requestedScopes, apiClientItem.getScopes())) {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-403-TOKEN-TKN-SCOPES",
                                    "scope-set-not-authorized",
                                    "Alguno o varios de los scopes solicitados no están autorizados para esta función"))
                            .build();
                }

                String jwtId = UUID.randomUUID().toString();

                JwtBuilder jwtBuilder =
                        JwtUtils.jwsGenerate(
                                jwtId,
                                apiClientItem,
                                uri.toASCIIString(),
                                requestedScopes);


                TokenResponse tokenResponse = TokenResponseFactory.getInstance(
                        "Bearer",
                        apiClientItem.getTokenExpirationMinutes(),
                        jwtBuilder.compact(),
                        scopes);

                TokenItem tokenItem = new TokenItem(
                        tokenResponse,
                        jwtId,
                        apiClientItem.getClientId(),
                        apiClientItem.getPubkeyb4(),
                        RSAUtils.getPublicKeyAsPEMfile(apiClientItem.getPubkeyb4(), true),
                        apiClientItem.getKeypairId()
                );

                persistenceController.persistTokenItem(tokenItem);


                return Response
                        .status(Response.Status.OK)
                        .entity(tokenResponse).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-401-TOKEN-TKN3",
                                "invalid-credentials",
                                "Credenciales no disponibles para esta función"))
                        .build();
            }


        } catch (Throwable ex) {
            String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500-TKN",
                            "token-creation-exception",
                            message))
                    .build();
        }
    }
}

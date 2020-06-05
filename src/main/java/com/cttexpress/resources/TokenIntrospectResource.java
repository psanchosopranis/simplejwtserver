package com.cttexpress.resources;

import com.cttexpress.models.*;
import com.cttexpress.persistence.ApiClientItem;
import com.cttexpress.persistence.ApiClientsTable;
import com.cttexpress.persistence.PersistenceController;
import com.cttexpress.utils.BasicAuth;
import com.cttexpress.utils.JwtUtils;
import com.cttexpress.utils.RSAUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("introspect")
public class TokenIntrospectResource {

    @Context
    ServletContext context;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response introspectToken(@FormParam("token") String token,
                             @FormParam("token_type") String token_type,
                             @HeaderParam("Authorization") String authorization,
                             @Context UriInfo uriInfo) {

        try {
            URI uri = uriInfo.getAbsolutePathBuilder().build();

            BasicAuth basicAuth = new BasicAuth(authorization);
            if (!basicAuth.isBuiltOK()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-401-INTR0",
                                "invalid-credentials",
                                "Credenciales no válidas para esta función"))
                        .build();
            }

            String clientId = basicAuth.getUser();
            String clientSecret = basicAuth.getPassword();

            if (token_type == null || !token_type.equalsIgnoreCase("access-token") ||
                    token == null || token.trim().length() == 0 ) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-400-INTR",
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
                if (!apiClientItem.getClientSecret().equalsIgnoreCase(clientSecret)) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-401-TOKEN-INTR1",
                                    "invalid-credentials",
                                    "Credenciales no válidas para esta función"))
                            .build();
                }

                if (!apiClientItem.getStatus().equalsIgnoreCase("ACTIVE")) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-401-TOKEN-INTR2",
                                    "invalid-credentials",
                                    "Credenciales no disponibles (NO ACTIVAS) para esta función"))
                            .build();
                }


                try {
                    long seconds = 1 * 60; // 1 minuto
                    Jws<Claims> jwsClaims = Jwts.parserBuilder()
                            .setSigningKey(RSAUtils.getPublicKeyFromB64encoded(apiClientItem.getPubkeyb4()))
                            .setAllowedClockSkewSeconds(seconds)
                            .build()
                            .parseClaimsJws(token);

                    Claims jwsBodyClaims = jwsClaims.getBody();
                    TokenIntrospectResponse tir = new TokenIntrospectResponse();
                    tir.setActive(true);
                    tir.setValidSignature(true);
                    tir.setIss(jwsBodyClaims.getIssuer());
                    tir.setSub(jwsBodyClaims.getSubject());
                    tir.setAud(jwsBodyClaims.getAudience());
                    tir.setExp(jwsBodyClaims.getExpiration().getTime());
                    tir.setIat(jwsBodyClaims.getIssuedAt().getTime());
                    tir.setNbf(jwsBodyClaims.getNotBefore().getTime());
                    tir.setJti(jwsBodyClaims.getId());

                    Object scopes = jwsBodyClaims.get("scp");
                    tir.setScp((ArrayList<String>) scopes);
                    tir.setCid(jwsBodyClaims.get("cid", String.class));
                    tir.setTokenType(token_type);

                    return Response
                            .status(Response.Status.OK)
                            .entity(tir).build();

                } catch (io.jsonwebtoken.ExpiredJwtException tex) {
                    return Response
                            .status(Response.Status.CONFLICT)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-409-INTR1",
                                    "token-expired",
                                    tex.getMessage()))
                            .build();
                } catch (io.jsonwebtoken.security.SignatureException sex) {
                    return Response
                            .status(Response.Status.CONFLICT)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-409-INTR2",
                                    "invalid-token-signature",
                                    sex.getMessage()))
                            .build();
                } catch (Throwable ex) {
                    String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(ErrorResponseFactory.getInstance(
                                    "ERR-500-INTR1",
                                    "token-introspection-exception",
                                    message))
                            .build();
                }

            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-401-TOKEN-INTR3",
                                "invalid-credentials",
                                "Credenciales no disponibles para esta función"))
                        .build();
            }


        } catch (Throwable ex) {
            String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500-INTR",
                            "token-introspection-exception",
                            message))
                    .build();
        }
    }
}

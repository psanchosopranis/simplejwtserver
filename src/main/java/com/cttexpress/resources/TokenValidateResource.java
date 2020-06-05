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

@Path("validate")
public class TokenValidateResource {

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
    public Response introspectToken(@FormParam("token") String token,
                                    @FormParam("token_type") String token_type,
                                    @FormParam("token_pubkey") String token_pubkey,
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

            if (token_type == null || !token_type.equalsIgnoreCase("access-token") ||
                    token == null || token.trim().length() == 0 ||
                    token_pubkey == null || token_pubkey.trim().length() == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-400-VALD",
                                "invalid-data-sent",
                                "Datos de solicitud incorrectos."))
                        .build();
            }

            try {
                long seconds = 1 * 60; // 1 minuto
                Jws<Claims> jwsClaims = Jwts.parserBuilder()
                        .setSigningKey(RSAUtils.getPublicKeyFromB64encoded(token_pubkey))
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

                return Response
                        .status(Response.Status.OK)
                        .entity(tir).build();

            } catch (io.jsonwebtoken.ExpiredJwtException tex) {
                TokenValidateExpiredResponse tkve = new TokenValidateExpiredResponse();
                tkve.setActive(false);
                tkve.setRemarks(tex.getMessage());
                return Response
                        .status(Response.Status.CONFLICT)
                        .entity(tkve).build();
            } catch (io.jsonwebtoken.security.SignatureException sex) {
                TokenValidateNotValidResponse tkvnv = new TokenValidateNotValidResponse();
                tkvnv.setValidSignature(false);
                tkvnv.setRemarks(sex.getMessage());
                return Response
                        .status(Response.Status.CONFLICT)
                        .entity(tkvnv).build();
            } catch (Throwable ex) {
                String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(ErrorResponseFactory.getInstance(
                                "ERR-500-VALD1",
                                "token-validation-exception",
                                message))
                        .build();
            }

        } catch (Throwable ex) {
            String message = ex.getClass().getName() + " : " + (ex.getMessage() != null ? ex.getMessage() : "(Causa no disponible)");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponseFactory.getInstance(
                            "ERR-500-VALD",
                            "token-validation-exception",
                            message))
                    .build();
        }
    }

}

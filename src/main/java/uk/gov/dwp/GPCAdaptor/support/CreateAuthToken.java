package uk.gov.dwp.GPCAdaptor.support;

import io.jsonwebtoken.Jwts;
import java.util.Date;

import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;

public class CreateAuthToken {

    //Create authorisation token from jwt
    public static BearerTokenAuthInterceptor createAuthInterceptor(boolean write) {
        String jwt = buildJWT(write);
        return new BearerTokenAuthInterceptor(jwt);
    }

    // JSON Web Token (JWT) is generated on Server side (not Browser), then returns to the Browser. Browser sends the JWT on the Auth Header
    // Server checks JWT signature and sends response to the client
    // Use example - https://github.com/jwtk/jjwt/
    private static String buildJWT(boolean write) {

        Date exp = new Date(System.currentTimeMillis() + 300000);
        Date iat = new Date(System.currentTimeMillis());

        // Build registered and custom Claims.
        CreatePayloadData createPayloadData = new CreatePayloadData();
        String jsonString = createPayloadData.buildPayloadData(exp, iat, write);

        System.out.println(jsonString);

        // Use example - https://github.com/jwtk/jjwt/
        String compactJws = Jwts.builder()
                .setHeaderParam("alg", "none")
                .setHeaderParam("typ", "JWT")
                .setPayload(jsonString)
                .compact();

        System.out.println("JSON Web Token : " + compactJws);

        return compactJws;
    }
}

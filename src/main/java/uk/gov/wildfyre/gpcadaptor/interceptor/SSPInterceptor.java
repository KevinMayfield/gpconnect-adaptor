package uk.gov.wildfyre.gpcadaptor.interceptor;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import io.jsonwebtoken.Jwts;
import uk.gov.wildfyre.gpcadaptor.HapiProperties;

import java.io.IOException;
import java.util.*;

public class SSPInterceptor implements IClientInterceptor {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SSPInterceptor.class);

    private static String kSSPInteractionId = "Ssp-InteractionID";
    private static String kAccept = "Accept";
    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {

        boolean isDSTU2 = false;

        // TODO read payload and retrieve nhsNumber
        String nhsNumber = HapiProperties.getNhsNumber();

        iHttpRequest.addHeader("Ssp-From", HapiProperties.getGpConnectAsidFrom());


        if (iHttpRequest.getUri().contains("Patient/$gpc.getstructuredrecord")) {
            iHttpRequest.addHeader(kSSPInteractionId, "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getstructuredrecord-1");
        }
        if (iHttpRequest.getUri().contains("Patient/$gpc.getcarerecord")) {
            iHttpRequest.addHeader(kSSPInteractionId, "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord");
            isDSTU2 = true;
        }
        if (iHttpRequest.getUri().contains("Patient?identifier")) {
            iHttpRequest.addHeader(kSSPInteractionId, "urn:nhs:names:services:gpconnect:fhir:rest:search:patient-1");
        }
        if (iHttpRequest.getUri().contains("metadata")) {
            iHttpRequest.addHeader(kSSPInteractionId, "urn:nhs:names:services:gpconnect:fhir:rest:read:metadata-1");
        }
        if (iHttpRequest.getUri().contains("Practitioner/")) {
            iHttpRequest.addHeader(kSSPInteractionId, "urn:nhs:names:services:gpconnect:fhir:rest:read:practitioner-1");
        }
        if (iHttpRequest.getUri().contains("Organization/")) {
            iHttpRequest.addHeader(kSSPInteractionId, "urn:nhs:names:services:gpconnect:fhir:rest:read:organization-1");
        }
        if (iHttpRequest.getUri().contains("Location/")) {
            iHttpRequest.addHeader(kSSPInteractionId, "urn:nhs:names:services:gpconnect:fhir:rest:read:location-1");
        }
        iHttpRequest.addHeader("Ssp-To",HapiProperties.getGpConnectAsidTo());

        iHttpRequest.addHeader("Ssp-TraceID", UUID.randomUUID().toString());


        iHttpRequest.removeHeaders(kAccept);
        if (isDSTU2) {
            iHttpRequest.addHeader(kAccept, "application/json+fhir");
            iHttpRequest.removeHeaders("User-Agent");
            iHttpRequest.removeHeaders("Accept-Charset");
            iHttpRequest.removeHeaders("Accept-Encoding");
        } else {
            iHttpRequest.addHeader(kAccept, "application/fhir+json");
        }


        if (isDSTU2) {
            jwtdstu2(iHttpRequest, nhsNumber);
        } else {
            jwtstu3(iHttpRequest);
        }

    }

    private void jwtdstu2 (IHttpRequest iHttpRequest, String nhsNumber) {
        Date exp = new Date(System.currentTimeMillis() + 300000);
        Date iat = new Date(System.currentTimeMillis());

        // Build registered and custom Claims.
        CreatePayloadDataV0 createPayloadData = new CreatePayloadDataV0();
        String jsonString = createPayloadData.buildPayloadData(exp, iat, nhsNumber);
        String compactJws = Jwts.builder()
                .setHeaderParam("alg", "none")
                .setHeaderParam("typ", "JWT")
                .setPayload(jsonString)
                .compact();
        log.trace("DSTU2 JWT Created");
        iHttpRequest.addHeader("Authorization", "Bearer " + compactJws);
    }

    private void jwtstu3 (IHttpRequest iHttpRequest) {
        Date exp = new Date(System.currentTimeMillis() + 300000);
        Date iat = new Date(System.currentTimeMillis());

        // Build registered and custom Claims.
        CreatePayloadData createPayloadData = new CreatePayloadData();
        String jsonString = createPayloadData.buildPayloadData(exp, iat);
        String compactJws = Jwts.builder()
                .setHeaderParam("alg", "none")
                .setHeaderParam("typ", "JWT")
                .setPayload(jsonString)
                .compact();
        log.trace("STU3 JWT Created");
        iHttpRequest.addHeader("Authorization", "Bearer " + compactJws);
    }

    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {
        // No action
    }


}

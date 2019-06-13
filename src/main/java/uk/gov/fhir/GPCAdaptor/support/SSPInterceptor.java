package uk.gov.fhir.GPCAdaptor.support;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import io.jsonwebtoken.Jwts;
import uk.gov.fhir.GPCAdaptor.HapiProperties;

import java.io.IOException;
import java.util.*;

public class SSPInterceptor implements IClientInterceptor {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SSPInterceptor.class);

    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {

        Boolean isDSTU2 = false;

        // TODO read payload and retrieve nhsNumber
        String nhsNumber = HapiProperties.getNhsNumber();
        log.trace("SSP NHS Number = "+nhsNumber);

      //  System.out.println(iHttpRequest.getUri());
        if (iHttpRequest.getHttpVerbName().equals("GET")) {

            iHttpRequest.addHeader("Ssp-From", HapiProperties.getGpConnectAsidFrom());
        } else {
            iHttpRequest.addHeader("Ssp-From", HapiProperties.getGpConnectAsidFrom());

        }
        if (iHttpRequest.getUri().contains("Patient/$gpc.getstructuredrecord")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getstructuredrecord-1");
        }
        if (iHttpRequest.getUri().contains("Patient/$gpc.getcarerecord")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord");
            isDSTU2 = true;
        }
        if (iHttpRequest.getUri().contains("Patient?identifier")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:rest:search:patient-1");
        }
        if (iHttpRequest.getUri().contains("metadata")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:rest:read:metadata-1");
        }
        if (iHttpRequest.getUri().contains("Practitioner/")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:rest:read:practitioner-1");
        }
        if (iHttpRequest.getUri().contains("Organization/")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:rest:read:organization-1");
        }
        if (iHttpRequest.getUri().contains("Location/")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:rest:read:location-1");
        }
        iHttpRequest.addHeader("Ssp-To",HapiProperties.getGpConnectAsidTo());

        iHttpRequest.addHeader("Ssp-TraceID", UUID.randomUUID().toString());


        iHttpRequest.removeHeaders("Accept");
        if (isDSTU2) {
            iHttpRequest.addHeader("Accept", "application/json+fhir");
           // iHttpRequest.removeHeaders("Content-Type");
            iHttpRequest.removeHeaders("User-Agent");
            iHttpRequest.removeHeaders("Accept-Charset");
            iHttpRequest.removeHeaders("Accept-Encoding");
        } else {
            iHttpRequest.addHeader("Accept", "application/fhir+json");
        }


        if (isDSTU2) {
            Date exp = new Date(System.currentTimeMillis() + 300000);
            Date iat = new Date(System.currentTimeMillis());

            // Build registered and custom Claims.
            CreatePayloadDataV0 createPayloadData = new CreatePayloadDataV0();
            String jsonString = createPayloadData.buildPayloadData(exp, iat, nhsNumber,false);
            String compactJws = Jwts.builder()
                    .setHeaderParam("alg", "none")
                    .setHeaderParam("typ", "JWT")
                    .setPayload(jsonString)
                    .compact();
            log.trace("DSTU2 JWT Created");
            iHttpRequest.addHeader("Authorization", "Bearer " + compactJws);
        } else {
            Date exp = new Date(System.currentTimeMillis() + 300000);
            Date iat = new Date(System.currentTimeMillis());

            // Build registered and custom Claims.
            CreatePayloadData createPayloadData = new CreatePayloadData();
            String jsonString = createPayloadData.buildPayloadData(exp, iat, false);
            String compactJws = Jwts.builder()
                    .setHeaderParam("alg", "none")
                    .setHeaderParam("typ", "JWT")
                    .setPayload(jsonString)
                    .compact();
            log.trace("STU3 JWT Created");
            iHttpRequest.addHeader("Authorization", "Bearer " + compactJws);
        }


        Map<String, List<String>> headers = iHttpRequest.getAllHeaders();
        Iterator it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
          //  System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }

    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {

    }
}

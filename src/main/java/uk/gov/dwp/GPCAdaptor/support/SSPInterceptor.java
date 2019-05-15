package uk.gov.dwp.GPCAdaptor.support;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import uk.gov.dwp.GPCAdaptor.HapiProperties;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SSPInterceptor implements IClientInterceptor {
    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {

        if (iHttpRequest.getHttpVerbName().equals("GET")) {

            iHttpRequest.addHeader("Ssp-From", HapiProperties.getGpConnectAsidFrom());
        } else {
            iHttpRequest.addHeader("Ssp-From", HapiProperties.getGpConnectAsidFrom());

        }
        if (iHttpRequest.getUri().contains("Patient/$gpc.getstructuredrecord")) {
            iHttpRequest.addHeader("Ssp-InteractionID", "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getstructuredrecord-1");
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


      //  System.out.println(iHttpRequest.getUri());
        iHttpRequest.removeHeaders("Accept");
        iHttpRequest.addHeader("Accept", "application/fhir+json");
        /*
        try {
            System.out.println(iHttpRequest.getRequestBodyFromStream());
        } catch (Exception ex) {

        }

         */

        Map<String, List<String>> headers = iHttpRequest.getAllHeaders();
        Iterator it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }

    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {

    }
}

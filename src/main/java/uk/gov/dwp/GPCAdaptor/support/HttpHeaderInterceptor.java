package uk.gov.dwp.GPCAdaptor.support;


import ca.uhn.fhir.rest.client.apache.ApacheHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.interceptor.CookieInterceptor;
import uk.gov.dwp.GPCAdaptor.HapiProperties;

import java.util.UUID;

public class HttpHeaderInterceptor extends CookieInterceptor {

    private final String localCookie;

    public HttpHeaderInterceptor(String headerCookie) {
        super(headerCookie);
        this.localCookie = headerCookie;
    }

    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        super.interceptRequest(theRequest);
        String[] parts = localCookie.split("=");
        if (theRequest.getAllHeaders().get(parts[0]) == null) {
            theRequest.addHeader(parts[0], parts[1]);
        } else if (parts[0].equals("Accept")) {
            ApacheHttpRequest request = (ApacheHttpRequest) theRequest;
            request.getApacheRequest().removeHeaders("Accept");
            theRequest.addHeader(parts[0], parts[1]);
        }
        theRequest.addHeader("Ssp-TraceID",UUID.randomUUID().toString());
        theRequest.addHeader("Ssp-From", HapiProperties.getGpConnectAsidFrom());
        theRequest.addHeader("Ssp-To", HapiProperties.getGpConnectAsidTo());
    }

}

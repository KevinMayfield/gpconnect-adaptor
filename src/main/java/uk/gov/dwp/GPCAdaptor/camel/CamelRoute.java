package uk.gov.dwp.GPCAdaptor.camel;


import ca.uhn.fhir.context.FhirContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.dwp.GPCAdaptor.camel.interceptor.GatewayPostProcessor;
import uk.gov.dwp.GPCAdaptor.camel.interceptor.GatewayPreProcessor;

@Component
public class CamelRoute extends RouteBuilder {


	
    @Override
    public void configure() {

		GatewayPreProcessor camelProcessor = new GatewayPreProcessor();

		GatewayPostProcessor camelPostProcessor = new GatewayPostProcessor();


		FhirContext ctx = FhirContext.forDstu3();

	}
}

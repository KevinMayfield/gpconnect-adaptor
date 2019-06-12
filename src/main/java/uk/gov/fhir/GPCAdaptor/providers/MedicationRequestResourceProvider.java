package uk.gov.fhir.GPCAdaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.fhir.GPCAdaptor.dao.IMedicationRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class MedicationRequestResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IGenericClient client;

    @Autowired
    IMedicationRequest resourceDao;

    private static final Logger log = LoggerFactory.getLogger(MedicationRequestResourceProvider.class);

    @Override
    public Class<MedicationRequest> getResourceType() {
        return MedicationRequest.class;
    }


    @Search
    public List<MedicationRequest> search(HttpServletRequest httpRequest,
                                                               @OptionalParam(name = MedicationRequest.SP_PATIENT) ReferenceParam patient
    ) throws Exception {

        return resourceDao.search(client, patient);


    }


    /*

    Original camel version lifted from careconnect-reference-implementation a2si branch

        InputStream inputStream = null;
        if (httpRequest != null) {
            inputStream = (InputStream) template.sendBody("direct:FHIRMedicationRequest",
                    ExchangePattern.InOut,httpRequest);
        } else {
            Exchange exchange = template.send("direct:FHIRMedicationRequest",ExchangePattern.InOut, new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(Exchange.HTTP_QUERY, "?patient="+patient.getIdPart());
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_PATH, "MedicationRequest");
                }
            });
            inputStream = (InputStream) exchange.getIn().getBody();
        }
        Bundle bundle = null;

        Reader reader = new InputStreamReader(inputStream);
        IBaseResource resource = null;
        try {
            resource = ctx.newJsonParser().parseResource(reader);
        } catch(Exception ex) {
            log.error("JSON Parse failed " + ex.getMessage());
            throw new InternalErrorException(ex.getMessage());
        }
        if (resource instanceof Bundle) {
            bundle = (Bundle) resource;
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                MedicationRequest statement = (MedicationRequest) entry.getResource();
                results.add(statement);
            }
        } else {
            ProviderResponseLibrary.createException(ctx,resource);
        }

         */




}

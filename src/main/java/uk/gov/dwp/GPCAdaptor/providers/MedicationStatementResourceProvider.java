package uk.gov.dwp.GPCAdaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.GPCAdaptor.dao.IMedicationStatement;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class MedicationStatementResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IGenericClient client;

    @Autowired
    IMedicationStatement resourceDao;

    private static final Logger log = LoggerFactory.getLogger(MedicationStatementResourceProvider.class);

    @Override
    public Class<MedicationStatement> getResourceType() {
        return MedicationStatement.class;
    }


    @Search
    public List<MedicationStatement> search(HttpServletRequest httpRequest,
                                                               @OptionalParam(name = MedicationStatement.SP_PATIENT) ReferenceParam patient
    ) throws Exception {

        return resourceDao.search(ctx,patient);


    }


    /*

    Original camel version lifted from careconnect-reference-implementation a2si branch

        InputStream inputStream = null;
        if (httpRequest != null) {
            inputStream = (InputStream) template.sendBody("direct:FHIRMedicationStatement",
                    ExchangePattern.InOut,httpRequest);
        } else {
            Exchange exchange = template.send("direct:FHIRMedicationStatement",ExchangePattern.InOut, new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(Exchange.HTTP_QUERY, "?patient="+patient.getIdPart());
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_PATH, "MedicationStatement");
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
                MedicationStatement statement = (MedicationStatement) entry.getResource();
                results.add(statement);
            }
        } else {
            ProviderResponseLibrary.createException(ctx,resource);
        }

         */




}

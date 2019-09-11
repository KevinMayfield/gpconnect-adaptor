package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IReferralRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class ReferralRequestResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    @Qualifier("CLIENTDSTU2")
    IGenericClient client;

    @Autowired
    IReferralRequest resourceDao;

    @Override
    public Class<ReferralRequest> getResourceType() {
        return ReferralRequest.class;
    }


    @Search
    public List<ReferralRequest> search(HttpServletRequest httpRequest,
                                                               @OptionalParam(name = ReferralRequest.SP_PATIENT) ReferenceParam patient
    ) throws Exception {

        return resourceDao.search(client,patient);


    }

}

package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IMedicationRequest;

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

    @Override
    public Class<MedicationRequest> getResourceType() {
        return MedicationRequest.class;
    }


    @Search
    public List<Resource> search(HttpServletRequest httpRequest,
                                 @OptionalParam(name = MedicationRequest.SP_PATIENT) ReferenceParam patient
    ) throws Exception {

        return resourceDao.search(client, patient);


    }




}

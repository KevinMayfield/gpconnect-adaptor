package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IObservation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class ObservationResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    @Qualifier("CLIENTDSTU2")
    IGenericClient client;

    @Autowired
    IObservation resourceDao;

    @Override
    public Class<Observation> getResourceType() {
        return Observation.class;
    }


    @Search
    public List<Observation> search(HttpServletRequest httpRequest,
                                                               @OptionalParam(name = Observation.SP_PATIENT) ReferenceParam patient
    )  {

        return resourceDao.search(client,patient);


    }





}

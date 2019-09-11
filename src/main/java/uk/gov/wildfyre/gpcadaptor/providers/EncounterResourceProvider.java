package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Encounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IEncounter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class EncounterResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    @Qualifier("CLIENTDSTU2")
    IGenericClient client;

    @Autowired
    IEncounter resourceDao;

    @Override
    public Class<Encounter> getResourceType() {
        return Encounter.class;
    }


    @Search
    public List<Encounter> search(HttpServletRequest httpRequest,
                                                               @OptionalParam(name = Encounter.SP_PATIENT) ReferenceParam patient
    )  {

        return resourceDao.search(client,patient);


    }



}

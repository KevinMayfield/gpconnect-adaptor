package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Immunization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IImmunization;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class ImmunizationResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    @Qualifier("CLIENTDSTU2")
    IGenericClient client;

    @Autowired
    IImmunization resourceDao;

    @Override
    public Class<Immunization> getResourceType() {
        return Immunization.class;
    }


    @Search
    public List<Immunization> search(HttpServletRequest httpRequest,
                                                               @OptionalParam(name = Immunization.SP_PATIENT) ReferenceParam patient
    )  {

        return resourceDao.search(client,patient);


    }

}

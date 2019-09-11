package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.ICondition;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class ConditionResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    @Qualifier("CLIENTDSTU2")
    IGenericClient client;

    @Autowired
    ICondition resourceDao;

    @Override
    public Class<Condition> getResourceType() {
        return Condition.class;
    }


    @Search
    public List<Condition> search(HttpServletRequest httpRequest,
                                                               @OptionalParam(name = Condition.SP_PATIENT) ReferenceParam patient
    ) throws Exception {

        return resourceDao.search(client,patient);


    }

}

package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IMedicationStatement;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Component
public class MedicationStatementResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IGenericClient client;

    @Autowired
    IMedicationStatement resourceDao;

    @Override
    public Class<MedicationStatement> getResourceType() {
        return MedicationStatement.class;
    }


    @Search
    public List<Resource> search(HttpServletRequest httpRequest,
                                 @OptionalParam(name = MedicationStatement.SP_PATIENT) ReferenceParam patient
            , @IncludeParam(allow= {
            "MedicationStatement:medication",
            "MedicationStatement:based-on"}) Set<Include> includes

    ) throws Exception {

        return resourceDao.search(client, patient, includes);

    }

}

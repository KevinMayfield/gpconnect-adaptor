package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IPractitioner;
import uk.gov.wildfyre.gpcadaptor.support.OperationOutcomeFactory;


@Component
public class PractitionerResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IPractitioner practitionerDao;

    @Autowired
    IGenericClient client;

    @Override
    public Class<Practitioner> getResourceType() {
        return Practitioner.class;
    }


    @Read
    public Practitioner read(@IdParam IdType internalId) {

/*
        Practitioner practitioner = practitionerDao.read(client,internalId);
        if (practitioner == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No practitioner details found for practitioner ID: " + internalId.getIdPart()),
                     OperationOutcome.IssueType.NOTFOUND);
        }
*/
        return null;
    }



}

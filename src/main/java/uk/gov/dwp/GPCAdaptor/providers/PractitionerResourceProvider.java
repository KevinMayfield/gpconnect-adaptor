package uk.gov.dwp.GPCAdaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.GPCAdaptor.dao.IPractitioner;
import uk.gov.dwp.GPCAdaptor.support.OperationOutcomeFactory;


@Component
public class PractitionerResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IPractitioner practitionerDao;

    @Autowired
    IGenericClient client;


    private static final Logger log = LoggerFactory.getLogger(PractitionerResourceProvider.class);

    @Override
    public Class<Practitioner> getResourceType() {
        return Practitioner.class;
    }


    @Read
    public Practitioner read(@IdParam IdType internalId) {


        Practitioner practitioner = practitionerDao.read(client,internalId);
        if (practitioner == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No practitioner details found for practitioner ID: " + internalId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }

        return practitioner;
    }



}

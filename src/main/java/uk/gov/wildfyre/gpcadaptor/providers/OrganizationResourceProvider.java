package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IOrganisation;
import uk.gov.wildfyre.gpcadaptor.support.OperationOutcomeFactory;


@Component
public class OrganizationResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IOrganisation organisationDao;

    @Autowired
    IGenericClient client;


    @Override
    public Class<Organization> getResourceType() {
        return Organization.class;
    }


    @Read
    public Organization read(@IdParam IdType internalId) {


        Organization organisation = organisationDao.read(client,internalId);
        if (organisation == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No organisation details found for organisation ID: " + internalId.getIdPart()),
                     OperationOutcome.IssueType.NOTFOUND);
        }

        return organisation;
    }



}

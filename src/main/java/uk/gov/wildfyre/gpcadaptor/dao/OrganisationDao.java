package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.stereotype.Component;


@Component
public class OrganisationDao implements IOrganisation {


    @Override
    public Organization read(IGenericClient client, IdType internalId) {
        return client.read()
                .resource(Organization.class)
                .withId(internalId.getIdPart())
                .execute();
    }
}

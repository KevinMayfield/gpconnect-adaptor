package uk.gov.dwp.GPCAdaptor.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.dwp.GPCAdaptor.HapiProperties;
import uk.gov.dwp.GPCAdaptor.support.CreateAuthToken;
import uk.gov.dwp.GPCAdaptor.support.SSPInterceptor;

@Component
public class OrganisationDao implements IOrganisation {

    private static final Logger log = LoggerFactory.getLogger(OrganisationDao.class);

    @Override
    public Organization read(IGenericClient client, IdType internalId) {
        return client.read()
                .resource(Organization.class)
                .withId(internalId.getIdPart())
                .execute();
    }
}

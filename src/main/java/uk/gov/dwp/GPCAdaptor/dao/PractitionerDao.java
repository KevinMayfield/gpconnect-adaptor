package uk.gov.dwp.GPCAdaptor.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.dwp.GPCAdaptor.HapiProperties;
import uk.gov.dwp.GPCAdaptor.support.CreateAuthToken;
import uk.gov.dwp.GPCAdaptor.support.SSPInterceptor;

@Component
public class PractitionerDao implements IPractitioner {

    private static final Logger log = LoggerFactory.getLogger(PractitionerDao.class);

    @Override
    public Practitioner read(IGenericClient client, IdType internalId) {



        return client.read()
                .resource(Practitioner.class)
                .withId(internalId.getIdPart())
                .execute();


    }


}

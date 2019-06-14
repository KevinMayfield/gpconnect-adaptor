package uk.gov.wildfyre.GPCAdaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

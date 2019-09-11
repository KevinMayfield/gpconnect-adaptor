package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.springframework.stereotype.Component;

@Component
public class PractitionerDao implements IPractitioner {


    @Override
    public Practitioner read(IGenericClient client, IdType internalId) {



        return client.read()
                .resource(Practitioner.class)
                .withId(internalId.getIdPart())
                .execute();


    }


}

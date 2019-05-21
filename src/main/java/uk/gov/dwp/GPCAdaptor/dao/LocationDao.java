package uk.gov.dwp.GPCAdaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class LocationDao implements ILocation {

    private static final Logger log = LoggerFactory.getLogger(LocationDao.class);

    @Override
    public Location read(IGenericClient client, IdType internalId) {
        return client.read()
                .resource(Location.class)
                .withId(internalId.getIdPart())
                .execute();
    }
}

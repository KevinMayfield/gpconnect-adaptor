package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.ILocation;
import uk.gov.wildfyre.gpcadaptor.support.OperationOutcomeFactory;


@Component
public class LocationResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    ILocation locationDao;

    @Autowired
    IGenericClient client;

    @Override
    public Class<Location> getResourceType() {
        return Location.class;
    }


    @Read
    public Location read(@IdParam IdType internalId) {

/*
        Location location = locationDao.read(client,internalId);
        if (location == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No location details found for location ID: " + internalId.getIdPart()),
                    OperationOutcome.IssueType.NOTFOUND);
        }

        return location;*/

        return null;
    }



}

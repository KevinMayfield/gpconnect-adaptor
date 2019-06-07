package uk.gov.fhir.GPCAdaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.fhir.GPCAdaptor.dao.ILocation;
import uk.gov.fhir.GPCAdaptor.support.OperationOutcomeFactory;


@Component
public class LocationResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    ILocation locationDao;

    @Autowired
    IGenericClient client;


    private static final Logger log = LoggerFactory.getLogger(LocationResourceProvider.class);

    @Override
    public Class<Location> getResourceType() {
        return Location.class;
    }


    @Read
    public Location read(@IdParam IdType internalId) {


        Location location = locationDao.read(client,internalId);
        if (location == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No location details found for location ID: " + internalId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }

        return location;
    }



}

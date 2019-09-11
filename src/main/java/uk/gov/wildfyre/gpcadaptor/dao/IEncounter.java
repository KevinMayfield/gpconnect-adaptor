package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.Encounter;

import java.util.List;

public interface IEncounter {


    List<Encounter> search(IGenericClient client, ReferenceParam patient) throws Exception;

}

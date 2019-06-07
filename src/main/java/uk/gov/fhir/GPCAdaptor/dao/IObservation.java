package uk.gov.fhir.GPCAdaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.Observation;

import java.util.List;

public interface IObservation {


    List<Observation> search(IGenericClient client, ReferenceParam patient) throws Exception;

}

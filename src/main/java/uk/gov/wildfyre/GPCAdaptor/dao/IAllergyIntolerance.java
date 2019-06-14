package uk.gov.wildfyre.GPCAdaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;

import java.util.List;

public interface IAllergyIntolerance {


    List<AllergyIntolerance> search(IGenericClient client, ReferenceParam patient) throws Exception;

}

package uk.gov.fhir.GPCAdaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.MedicationStatement;

import java.util.List;

public interface IMedicationRequest {


    List<MedicationRequest> search(IGenericClient client, ReferenceParam patient) throws Exception;

}

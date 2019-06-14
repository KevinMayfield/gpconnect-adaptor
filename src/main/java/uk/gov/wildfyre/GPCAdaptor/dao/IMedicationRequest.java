package uk.gov.wildfyre.GPCAdaptor.dao;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.Resource;

import java.util.List;
import java.util.Set;

public interface IMedicationRequest {


    List<Resource> search(IGenericClient client, ReferenceParam patient) throws Exception;

    List<Resource> extractMedicationRequest(Bundle result);

}

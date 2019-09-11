package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Resource;

import java.util.List;

public interface IMedicationRequest {


    List<Resource> search(IGenericClient client, ReferenceParam patient) throws Exception;

    List<Resource> extractMedicationRequest(Bundle result);

}

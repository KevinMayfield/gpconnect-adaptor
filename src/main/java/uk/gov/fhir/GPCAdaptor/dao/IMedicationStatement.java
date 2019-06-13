package uk.gov.fhir.GPCAdaptor.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;

import java.util.List;
import java.util.Set;

public interface IMedicationStatement {


    List<Resource> search(IGenericClient client, ReferenceParam patient, Set<Include> includes) throws Exception;

    List<Resource> extractMedicationStatement(Bundle result);

    List<Resource> extractMedication(Bundle result);
}

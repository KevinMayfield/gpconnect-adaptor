package uk.gov.GPCAdaptor.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.Condition;

import java.util.List;

public interface ICondition {


    List<Condition> search(IGenericClient client, ReferenceParam patient) throws Exception;

}

package uk.gov.wildfyre.GPCAdaptor.dao;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;

public interface IPractitioner {


    Practitioner read(IGenericClient client, IdType internalId);

}

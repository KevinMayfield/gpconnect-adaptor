package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IPatient;
import uk.gov.wildfyre.gpcadaptor.support.OperationOutcomeFactory;


@Component
public class PatientResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IPatient patientDao;

    @Autowired
    IGenericClient client;


    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }


    @Read
    public Patient read(@IdParam IdType internalId) {


        Patient patient = patientDao.read(client, internalId);
        if (patient == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No patient details found for patient ID: " + internalId.getIdPart()),
                     OperationOutcome.IssueType.NOTFOUND);
        }

        return patient;
    }



}

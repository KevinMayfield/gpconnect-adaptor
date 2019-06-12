package uk.gov.fhir.GPCAdaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.fhir.GPCAdaptor.support.StructuredRecord;

import java.util.ArrayList;
import java.util.List;

@Component
public class MedicationRequestDao implements IMedicationRequest {

    private static final Logger log = LoggerFactory.getLogger(MedicationRequestDao.class);

    @Override
    public List<MedicationRequest> search(IGenericClient client, ReferenceParam patient) throws Exception {


        List<MedicationRequest> medications = new ArrayList<>();

        log.trace(patient.getIdPart() );

        Parameters parameters = StructuredRecord.getStructuredRecordParameters(patient.getValue(),false, false, new DateType(1980, 5, 5));
        Bundle result = client.operation().onType(Patient.class)
                .named("$gpc.getstructuredrecord")
                .withParameters(parameters)
                .returnResourceType(Bundle.class)
                .execute();
       // System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(result));
        for(Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() instanceof MedicationRequest) {
                MedicationRequest prescription = (MedicationRequest) entry.getResource();
                if (prescription.hasMedicationReference() && prescription.getMedicationReference().getDisplay() == null) {
                    // Attempt to make the MedicationRequest more useful to calling systems.
                    Medication medication = getMedication(prescription,result);
                    if (medication != null) {
                        if (medication.hasCode()) {
                            if (medication.getCode().hasCoding()) {
                                prescription.getMedicationReference().setDisplay(medication.getCode().getCoding().get(0).getDisplay());
                            }
                            else {
                                prescription.getMedicationReference().setDisplay(medication.getCode().getText());
                            }
                        }
                    }
                }
                medications.add(prescription);
            }
        }
        return medications;
    }

    private Medication getMedication(MedicationRequest prescription, Bundle result) {

        for (Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() instanceof Medication) {
                Medication med = (Medication) entry.getResource();
                if (prescription.hasMedicationReference()) {
                    if (prescription.getMedicationReference().getReference().equals(med.getId())) {
                        return med;
                    }
                }
            }
            }

        return null;
    }

}

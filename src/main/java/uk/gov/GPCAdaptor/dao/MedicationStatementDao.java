package uk.gov.GPCAdaptor.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.GPCAdaptor.support.StructuredRecord;
import java.util.ArrayList;
import java.util.List;

@Component
public class MedicationStatementDao implements IMedicationStatement {

    private static final Logger log = LoggerFactory.getLogger(MedicationStatementDao.class);

    @Override
    public List<MedicationStatement> search(IGenericClient client, ReferenceParam patient) throws Exception {


        List<MedicationStatement> medications = new ArrayList<>();


        log.trace(patient.getIdPart() );

        Parameters parameters = StructuredRecord.getStructuredRecordParameters(patient.getValue(),false, false, new DateType(1980, 5, 5));
        Bundle result = client.operation().onType(Patient.class)
                .named("$gpc.getstructuredrecord")
                .withParameters(parameters)
                .returnResourceType(Bundle.class)
                .execute();
       // System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(result));
        for(Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() instanceof MedicationStatement) {
                MedicationStatement statement = (MedicationStatement) entry.getResource();
                if (statement.hasMedicationReference() && statement.getMedicationReference().getDisplay() == null) {
                    // Attempt to make the MedicationStatement more useful to calling systems.
                    Medication medication = getMedication(statement,result);
                    if (medication != null) {
                        if (medication.hasCode()) {
                            if (medication.getCode().hasCoding()) {
                                statement.getMedicationReference().setDisplay(medication.getCode().getCoding().get(0).getDisplay());
                            }
                            else {
                                statement.getMedicationReference().setDisplay(medication.getCode().getText());
                            }
                        }
                    }
                }
                medications.add(statement);
            }
        }
        return medications;
    }

    private Medication getMedication(MedicationStatement statement, Bundle result) {

        for (Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() instanceof Medication) {
                Medication med = (Medication) entry.getResource();
                if (statement.hasMedicationReference()) {
                    if (statement.getMedicationReference().getReference().equals(med.getId())) {
                        return med;
                    }
                }
            }
            }

        return null;
    }

}

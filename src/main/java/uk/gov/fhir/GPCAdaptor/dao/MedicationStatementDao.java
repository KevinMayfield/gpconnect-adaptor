package uk.gov.fhir.GPCAdaptor.dao;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.fhir.GPCAdaptor.support.StructuredRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class MedicationStatementDao implements IMedicationStatement {

    private static final Logger log = LoggerFactory.getLogger(MedicationStatementDao.class);

    @Autowired
    IMedicationRequest medicationRequestDAO;

    @Override
    public List<Resource> search(IGenericClient client, ReferenceParam patient,  Set<Include> includes) throws Exception {

        log.trace(patient.getIdPart() );

        Parameters parameters = StructuredRecord.getStructuredRecordParameters(patient.getValue(),false, false, new DateType(1980, 5, 5));
        Bundle result = client.operation().onType(Patient.class)
                .named("$gpc.getstructuredrecord")
                .withParameters(parameters)
                .returnResourceType(Bundle.class)
                .execute();

        List<Resource> resources = extractMedicationStatement(result);


        if (includes != null) {
            for (Include include : includes) {
                switch (include.getValue()) {
                    case "MedicationStatement:medication":
                        resources.addAll(extractMedication(result));
                        break;
                    case "MedicationStatement:based-on":
                        resources.addAll(medicationRequestDAO.extractMedicationRequest(result));
                        break;
                    case "*":
                        //log.info(String.valueOf(resources.size()));
                        resources.addAll(medicationRequestDAO.extractMedicationRequest(result));
                        //log.info(String.valueOf(resources.size()));
                        resources.addAll(extractMedication(result));
                        //log.info(String.valueOf(resources.size()));
                        break;
                }
            }
        }

        return resources;
    }

    public List<Resource> extractMedicationStatement(Bundle result) {
        List<Resource> medications = new ArrayList<>();
        for(Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() instanceof MedicationStatement) {
                MedicationStatement statement = (MedicationStatement) entry.getResource();
                if (statement.hasMedicationReference() && statement.getMedicationReference().getDisplay() == null) {
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

    public List<Resource> extractMedication(Bundle result) {
        List<Resource> medications = new ArrayList<>();
        for(Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() instanceof Medication) {
                medications.add(entry.getResource());
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

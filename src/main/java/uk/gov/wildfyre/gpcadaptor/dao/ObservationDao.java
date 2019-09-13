package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.support.StructuredRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class ObservationDao implements IObservation {

    SimpleDateFormat
            format = new SimpleDateFormat("dd-MMM-yyyy");

    @Override
    public List<Observation> search(IGenericClient client, ReferenceParam patient)  {

        if (patient == null) {
            return Collections.emptyList();
        }
        String sectionCode="OBS";


        Parameters parameters  = StructuredRecord.getUnStructuredRecordParameters(patient.getValue(),sectionCode);
        Bundle result = null;
        try {
            result = client.operation().onType(Patient.class)
                    .named("$gpc.getcarerecord")
                    .withParameters(parameters)
                    .returnResourceType(Bundle.class)
                    .encodedJson()
                    .execute();
        } catch (Exception ignore) {
            // No action
        }

        return processBundle(result,patient,sectionCode);
    }
    private List<Observation> processBundle(Bundle result, ReferenceParam patient, String sectionCode) {
        List<Observation> observations = new ArrayList<>();
        if (result != null) {
            for (Bundle.Entry entry : result.getEntry()) {
                if (entry.getResource() instanceof Composition) {
                    Composition doc = (Composition) entry.getResource();

                    for (Composition.Section
                            section : doc.getSection()) {
                        if (section.getCode().getCodingFirstRep().getCode().equals(sectionCode)) {
                            observations = extractObservations(section, patient);
                        }
                    }
                }
            }

        }
        return observations;

    }
    private List<Observation> extractObservations(Composition.Section section,ReferenceParam patient) {
        List<Observation> observations = new ArrayList<>();

        NarrativeDt text = section.getText();


        Document doc = Jsoup.parse(text.getDivAsString());
        org.jsoup.select.Elements rows = doc.select("tr");
        boolean problems = false;
        int h=1;
        for(org.jsoup.nodes.Element row :rows)
        {
            org.jsoup.select.Elements columns = row.select("th");

            for (org.jsoup.nodes.Element column:columns)
            {

                    if (column.text().equals("Details")) {
                        problems = true;
                    } else {
                        problems = false;
                    }

            }
            if (problems) {
                columns = row.select("td");
                Observation observation = new Observation();
                observation.setId("#"+h);
                observation.setSubject(new Reference
                        ("Patient/"+patient.getIdPart()));

                h++;
                int g = 0;
                for (org.jsoup.nodes.Element column : columns) {

                    processCols(observation,column,g);
                    g++;
                }
                if (observation.hasCode() )
                    observations.add(observation);
            }

        }

        return observations;
    }


    private void processCols(Observation observation,
                             org.jsoup.nodes.Element column,
                             int g) {
        if (g==0) {
            try {
                Date date = format.parse ( column.text() );

                observation.setEffective(new DateTimeType(date));
            }
            catch (Exception ignore) {
                // No action
            }
        }
        if (g==1) {
            CodeableConcept code = new CodeableConcept();
            code.setText(column.text());
            observation.setCode(code);
        }

        if (g==2) {
            CodeableConcept code = new CodeableConcept();
            code.setText(column.text());
            observation.setValue(code);

        }
        if (g==3) {
            CodeableConcept code = new CodeableConcept();
            code.setText(column.text());
            observation.setValue(code);

        }
    }
}



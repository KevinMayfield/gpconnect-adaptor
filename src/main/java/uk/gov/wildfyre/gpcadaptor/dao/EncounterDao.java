package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.support.StructuredRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class EncounterDao implements IEncounter {

    private static final Logger log = LoggerFactory.getLogger(EncounterDao.class);

    SimpleDateFormat
            format = new SimpleDateFormat("dd-MMM-yyyy");

    @Override
    public List<Encounter> search(IGenericClient client, ReferenceParam patient) {

        if (patient == null) {
            return Collections.emptyList();
        }
        String sectionCode="ENC";

        return processBundle(
                StructuredRecord.getRecord(client,
                        StructuredRecord.getUnStructuredRecordParameters(patient.getValue(),sectionCode)),
                        patient,
                        sectionCode);
    }
        private List<Encounter> processBundle(Bundle result, ReferenceParam patient, String sectionCode) {
            List<Encounter> encounters = new ArrayList<>();
            if (result != null) {
                for (Bundle.Entry entry : result.getEntry()) {
                    if (entry.getResource() instanceof Composition) {

                        Composition doc = (Composition) entry.getResource();

                        for (Composition.Section
                                section : doc.getSection()) {
                            if (section.getCode().getCodingFirstRep().getCode().equals(sectionCode)) {
                                log.info("Processing Section ENC");
                                encounters = extractEncounters(section, patient);
                            }
                        }
                    }
                }

            }
            return encounters;
        }

    private List<Encounter> extractEncounters(Composition.Section section,ReferenceParam patient) {
        List<Encounter> encounters = new ArrayList<>();

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

                processProblems(problems, row, patient, h, encounters);
                h++;
            }


        }

        return encounters;
    }

    private void processProblems(boolean problems,org.jsoup.nodes.Element row,ReferenceParam patient, int h, List<Encounter> encounters) {
        if (problems) {
            org.jsoup.select.Elements columns = row.select("td");
            Encounter encounter = new Encounter();
            encounter.setId("#"+h);
            encounter.setSubject(new Reference
                    ("Patient/"+patient.getIdPart()));

            processColumns(columns, encounter);

            if (encounter.hasType() || encounter.hasReason())
                encounters.add(encounter);
        }
    }

    private void processColumns(org.jsoup.select.Elements columns, Encounter encounter) {
        int g = 0;
        for (org.jsoup.nodes.Element column : columns) {

            if (g==0) {
                try {
                    Date date = format.parse ( column.text() );
                    encounter.getPeriod().setStart(date);
                }
                catch (Exception ignore) {
                    // No action
                }
            }
            if (g==1) {
                CodeableConcept code = new CodeableConcept();
                code.setText(column.text());
                encounter.addType(code);
            }

            if (g==2) {

                encounter.addReason()
                        .setText(column.text());
            }
            g++;
        }
    }
}



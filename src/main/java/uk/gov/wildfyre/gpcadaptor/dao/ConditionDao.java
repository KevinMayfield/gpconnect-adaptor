package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;

import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;

import org.hl7.fhir.dstu3.model.Reference;
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
public class ConditionDao implements ICondition {

    SimpleDateFormat
            format = new SimpleDateFormat("dd-MMM-yyyy");

    @Override
    public List<Condition> search(IGenericClient client, ReferenceParam patient)  {


        if (patient == null) {
            return Collections.emptyList();
        }
        String sectionCode="SUM";


        return processBundle(
                StructuredRecord.getRecord(client,
                        StructuredRecord.getUnStructuredRecordParameters(patient.getValue(),sectionCode)),patient,sectionCode);

    }

    private List<Condition> processBundle(Bundle result, ReferenceParam patient, String sectionCode) {

        List<Condition> conditions = new ArrayList<>();

        if (result != null && result.getEntry().isEmpty()) {
            for (Bundle.Entry entry : result.getEntry()) {
                if (entry.getResource() instanceof Composition) {
                    Composition doc = (Composition) entry.getResource();
                    processSections(conditions, doc, sectionCode, patient);
                }
            }
        }

        return conditions;
    }

    private void processSections(List<Condition> conditions, Composition doc, String sectionCode,ReferenceParam patient) {
        for (Composition.Section section : doc.getSection()) {
            if (section.getCode().getCodingFirstRep().getCode().equals(sectionCode)) {
                extractConditions(conditions, section, patient);
            }
        }
    }

    private List<Condition> extractConditions(List<Condition> conditions, Composition.Section section,ReferenceParam patient) {

        NarrativeDt text = section.getText();

        Document doc = Jsoup.parse(text.getDivAsString());
        org.jsoup.select.Elements rows = doc.select("tr");
        boolean problems = false;
        int h=1;
        for(org.jsoup.nodes.Element row :rows)
        {
            org.jsoup.select.Elements columns = row.select("th");
            int f=0;
            for (org.jsoup.nodes.Element column:columns)
            {

                if (f==2) {
                    if (column.text().equals("Significance")) {
                        problems = true;
                    } else {
                        problems = false;
                    }
                }
                f++;
            }
            if (problems) {
              processProblem(row, h, patient, conditions);
              h++;
            }

        }

        return conditions;
    }

    private void processProblem(org.jsoup.nodes.Element row, int h, ReferenceParam patient, List<Condition> conditions) {
        org.jsoup.select.Elements columns = row.select("td");
        Condition condition = new Condition();
        condition.setId("#"+h);
        condition.setSubject(new Reference
                ("Patient/"+patient.getIdPart()));


        processProblemEntries(columns, condition);
        if (condition.getCode() != null && condition.getCode().getText() != null) conditions.add(condition);
    }
    private void processProblemEntries(org.jsoup.select.Elements columns, Condition condition) {
        condition.setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE);
        int g = 0;
        for (org.jsoup.nodes.Element column : columns) {

            if (g==0) {
                try {
                    Date date = format.parse ( column.text() );
                    condition.setAssertedDate(date);
                }
                catch (Exception ignored) {
                    // No action
                }
            }
            if (g==1) {
                CodeableConcept code = new CodeableConcept();
                code.setText(column.text());
                condition.setCode(code);
            }
            if (g==2 && column.text().contains("Major")) {
                condition.getSeverity()
                        .addCoding()
                        .setSystem("http://snomed.info/sct")
                        .setCode("24484000")
                        .setDisplay("Severe");

            }
            g++;
        }

    }
}



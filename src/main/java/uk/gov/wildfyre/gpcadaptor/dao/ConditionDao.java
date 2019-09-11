package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;

import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
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

    @Override
    public List<Condition> search(IGenericClient client, ReferenceParam patient)  {

        List<Condition> conditions = new ArrayList<>();
        String sectionCode="SUM";
        if (patient == null) {
            return Collections.emptyList();
        }
        Parameters parameters  = StructuredRecord.getUnStructuredRecordParameters(patient.getValue(),sectionCode);
        Bundle result = null;
        try {
            result = client.operation().onType(Patient.class)
                    .named("$gpc.getcarerecord")
                    .withParameters(parameters)
                    .returnResourceType(Bundle.class)
                    .encodedJson()
                    .execute();
        } catch (Exception ignored) {
        }

        if (result != null && result.getEntry().isEmpty()) {
            for (Bundle.Entry entry : result.getEntry()) {
                if (entry.getResource() instanceof Composition) {
                    Composition doc = (Composition) entry.getResource();
                    for (Composition.Section
                            section : doc.getSection()) {
                        if (section.getCode().getCodingFirstRep().getCode().equals(sectionCode)) {
                            conditions = extractConditions(section, patient);
                        }
                    }
                }
            }
        }

        return conditions;
    }

    private List<Condition> extractConditions(Composition.Section section,ReferenceParam patient) {
        List<Condition> conditions = new ArrayList<>();

        NarrativeDt text = section.getText();
        SimpleDateFormat
                format = new SimpleDateFormat("dd-MMM-yyyy");

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
                columns = row.select("td");
                Condition condition = new Condition();
                condition.setId("#"+h);
                condition.setSubject(new Reference
                        ("Patient/"+patient.getIdPart()));
                condition.setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE);
                h++;
                int g = 0;
                for (org.jsoup.nodes.Element column : columns) {

                    if (g==0) {
                        try {
                            Date date = format.parse ( column.text() );
                            condition.setAssertedDate(date);
                        }
                        catch (Exception ignored) {
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
                if (condition.getCode() != null && condition.getCode().getText() != null) conditions.add(condition);
            }

        }

        return conditions;
    }

}



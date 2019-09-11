package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.support.StructuredRecord;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class AllergyIntoleranceDao implements IAllergyIntolerance {

    private static final Logger log = LoggerFactory.getLogger(AllergyIntoleranceDao.class);

    @Override
    public List<AllergyIntolerance> search(IGenericClient client, ReferenceParam patient) throws Exception {

        if (patient == null) {
            return Collections.emptyList();
        }
        List<AllergyIntolerance> allergys = new ArrayList<>();

        Parameters parameters  = StructuredRecord.getUnStructuredRecordParameters(patient.getValue(),"ALL");

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

        if (result != null) {
            for (Bundle.Entry entry : result.getEntry()) {
                if (entry.getResource() instanceof Composition) {
                    Composition doc = (Composition) entry.getResource();
                    for (Composition.Section
                            section : doc.getSection()) {
                        if (section.getCode().getCodingFirstRep().getCode().equals("ALL")) {
                            log.info("Processing Section ALL");
                            allergys = extractAllergyIntolerances(section, patient);
                        }
                    }
                }
            }

        }

        return allergys;
    }

    private List<AllergyIntolerance> extractAllergyIntolerances(Composition.Section section,ReferenceParam patient) {
        List<AllergyIntolerance> allergys = new ArrayList<>();

        NarrativeDt text = section.getText();
        SimpleDateFormat
                format = new SimpleDateFormat("dd-MMM-yyyy");

        Document doc = Jsoup.parse(text.getDivAsString());
        org.jsoup.select.Elements rows = doc.select("tr");
        boolean current = false;
        boolean past = false;
        int h=1;
        for(org.jsoup.nodes.Element row :rows)
        {
            org.jsoup.select.Elements columns = row.select("th");
            int f=0;
            for (org.jsoup.nodes.Element column:columns)
            {

                if (column.text().equals("Details")) {
                    switch (f) {
                        case 1:
                            current = true;
                            break;
                        case 2:
                            past = true;
                            break;
                        default:
                            current = false;
                            past = false;
                    }
                }

                f++;
            }
            if (current) {
                columns = row.select("td");
                AllergyIntolerance allergy = new AllergyIntolerance();
                allergy.setClinicalStatus(AllergyIntolerance.AllergyIntoleranceClinicalStatus.ACTIVE);

                allergy.setId("#"+h);
                allergy.setPatient(new Reference
                        ("Patient/"+patient.getIdPart()));

                h++;
                int g = 0;
                Period period = new Period();

                for (org.jsoup.nodes.Element column : columns) {

                    if (g==0) {
                        try {
                            Date date = format.parse ( column.text() );

                            period.setStart(date);
                        }
                        catch (Exception ignored) {
                        }
                    }


                    if (g==1) {

                        allergy.setOnset(period);
                        allergy.getCode()
                                    .setText(column.text());
                    }
                    g++;
                }
                if (allergy.hasCode() )
                    allergys.add(allergy);
            }


            if (past) {
                columns = row.select("td");
                AllergyIntolerance allergy = new AllergyIntolerance();
                allergy.setClinicalStatus(AllergyIntolerance.AllergyIntoleranceClinicalStatus.INACTIVE);
                allergy.setId("#"+h);
                allergy.setPatient(new Reference
                        ("Patient/"+patient.getIdPart()));

                h++;
                int g = 0;
                Period period = new Period();

                for (org.jsoup.nodes.Element column : columns) {

                    if (g==0) {
                        try {
                            Date date = format.parse ( column.text() );

                            period.setStart(date);
                        }
                        catch (Exception ignore) {}
                    }
                    if (g==1) {
                        try {
                            Date date = format.parse ( column.text() );

                            period.setEnd(date);
                        }
                        catch (Exception ignored) {  }
                    }

                    if (g==2) {

                        allergy.setOnset(period);
                        allergy.getCode()
                                .setText(column.text());
                    }
                    g++;
                }
                if (allergy.hasCode() )
                    allergys.add(allergy);
            }


        }

        return allergys;
    }

}



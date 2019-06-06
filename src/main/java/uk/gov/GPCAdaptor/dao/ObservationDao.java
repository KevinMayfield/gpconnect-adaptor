package uk.gov.GPCAdaptor.dao;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.GPCAdaptor.HapiProperties;
import uk.gov.GPCAdaptor.support.StructuredRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ObservationDao implements IObservation {

    private static final Logger log = LoggerFactory.getLogger(ObservationDao.class);

    @Override
    public List<Observation> search(IGenericClient client, ReferenceParam patient) throws Exception {

        String sectionCode="OBS";
        List<Observation> observations = new ArrayList<>();

        Parameters parameters  = StructuredRecord.getUnStructuredRecordParameters(patient.getValue(),sectionCode,false, false, null);
        FhirContext ctx = FhirContext.forDstu2();
        Bundle result = null;
        try {
            result = client.operation().onType(Patient.class)
                    .named("$gpc.getcarerecord")
                    .withParameters(parameters)
                    .returnResourceType(Bundle.class)
                    .encodedJson()
                    .execute();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        if (result != null) {
            for (Bundle.Entry entry : result.getEntry()) {
                if (entry.getResource() instanceof Composition) {
                    log.info(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(entry.getResource()));
                    Composition doc = (Composition) entry.getResource();

                    for (Composition.Section
                            section : doc.getSection()) {
                        if (section.getCode().getCodingFirstRep().getCode().equals(sectionCode)) {
                            log.info("Processing Section OBS");
                            observations = extractObservations(section, patient);
                        }
                    }
                }
            }
            //System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(result));
        }

        return observations;
    }

    private List<Observation> extractObservations(Composition.Section section,ReferenceParam patient) {
        List<Observation> observations = new ArrayList<>();

        NarrativeDt text = section.getText();
        SimpleDateFormat
                format = new SimpleDateFormat("dd-MMM-yyyy");

        Document doc = Jsoup.parse(text.getDivAsString());
        org.jsoup.select.Elements rows = doc.select("tr");
        Boolean problems = false;
        Integer h=1;
        for(org.jsoup.nodes.Element row :rows)
        {
            org.jsoup.select.Elements columns = row.select("th");
            Integer f=0;
            for (org.jsoup.nodes.Element column:columns)
            {
               log.info("th "+f + " - " + column.text());

                    if (column.text().equals("Details")) {
                        problems = true;
                    } else {
                        problems = false;
                    }
                f++;
            }
            if (problems) {
                columns = row.select("td");
                Observation observation = new Observation();
                observation.setId("#"+h);
                observation.setSubject(new Reference
                        ("Patient/"+patient.getIdPart()));

                h++;
                Integer g = 0;
                for (org.jsoup.nodes.Element column : columns) {
                   // System.out.print(column.text());
                    if (g==0) {
                        try {
                            Date date = format.parse ( column.text() );
                            observation.getEffectivePeriod().setStart(date);
                        }
                        catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    if (g==1) {
                        CodeableConcept code = new CodeableConcept();
                        code.setText(column.text());
                        observation.setCode(code);
                    }

                    if (g==2) {

                        // observation.addReason().setText(column.text());
                    }
                    g++;
                }
                if (observation.hasCode() )
                    observations.add(observation);
            }

        }

        return observations;
    }

}



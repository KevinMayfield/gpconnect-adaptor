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
import org.hl7.fhir.dstu3.model.ReferralRequest;
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
public class ReferralRequestDao implements IReferralRequest {

    private static final Logger log = LoggerFactory.getLogger(ReferralRequestDao.class);

    @Override
    public List<ReferralRequest> search(IGenericClient client, ReferenceParam patient) throws Exception {

        String sectionCode="REF";
        List<ReferralRequest> referrals = new ArrayList<>();

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
                            log.info("Processing Section REF");
                            referrals = extractReferralRequests(section, patient);
                        }
                    }
                }
            }
            //System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(result));
        }

        return referrals;
    }

    private List<ReferralRequest> extractReferralRequests(Composition.Section section,ReferenceParam patient) {
        List<ReferralRequest> referrals = new ArrayList<>();

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
                ReferralRequest referral = new ReferralRequest();
                referral.setId("#"+h);
                referral.setSubject(new Reference
                        ("Patient/"+patient.getIdPart()));

                h++;
                Integer g = 0;
                for (org.jsoup.nodes.Element column : columns) {
                   // System.out.print(column.text());
                    if (g==0) {
                        try {
                            Date date = format.parse ( column.text() );
                            referral.setAuthoredOn(date);
                        }
                        catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    if (g==1) {
                        ReferralRequest.ReferralRequestRequesterComponent ref = referral.getRequester();
                        ref.getAgent().setDisplay(column.text());
                    }
                    if (g==2) {
                        Reference ref = referral.addRecipient();
                        ref.setDisplay(column.text());
                    }
                    if (g==4) {

                        referral.setDescription(column.text());
                    }


                    g++;
                }
                if (referral.hasDescription())
                    referrals.add(referral);
            }

        }

        return referrals;
    }

}



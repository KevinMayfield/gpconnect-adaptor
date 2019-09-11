package uk.gov.wildfyre.gpcadaptor.dao;

import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.ReferralRequest;
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
public class ReferralRequestDao implements IReferralRequest {

    private static final Logger log = LoggerFactory.getLogger(ReferralRequestDao.class);

    @Override
    public List<ReferralRequest> search(IGenericClient client, ReferenceParam patient) {

        if (patient == null) {
            return Collections.emptyList();
        }
        String sectionCode="REF";
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

        }

        return processBundle(result,patient,sectionCode);
    }

    private List<ReferralRequest> processBundle(Bundle result, ReferenceParam patient, String sectionCode) {
        List<ReferralRequest> referrals = new ArrayList<>();
            if (result != null) {
                for (Bundle.Entry entry : result.getEntry()) {
                    if (entry.getResource() instanceof Composition) {

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
                ReferralRequest referral = new ReferralRequest();
                referral.setId("#"+h);
                referral.setSubject(new Reference
                        ("Patient/"+patient.getIdPart()));

                h++;
                int g = 0;
                for (org.jsoup.nodes.Element column : columns) {
                    if (g==0) {
                        try {
                            Date date = format.parse ( column.text() );
                            referral.setAuthoredOn(date);
                        }
                        catch (Exception ignore) {

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



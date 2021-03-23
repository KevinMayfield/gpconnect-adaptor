package uk.gov.wildfyre.gpcadaptor.transforms;


import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.convertors.VersionConvertor_30_40;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class STU3MedicationtoR4MedicationRequest implements Transformer<org.hl7.fhir.dstu3.model.MedicationRequest, MedicationRequest> {

    private static final String UUID_OID = "urn:oid:";
    private static final Logger log = LoggerFactory.getLogger(STU3MedicationtoR4MedicationRequest.class);
/*
     <Location>
            <DBID>4884</DBID>
            <RefID>4884</RefID>
            <GUID>37088c97-82fc-4b3e-bdbc-28f2c03b6cb8</GUID>
            <LocationName>Partner Programme Test 17</LocationName>
        </Location>
 */
    @Override
    public MedicationRequest transform(org.hl7.fhir.dstu3.model.MedicationRequest stu3medicationRequest) {

        VersionConvertor_30_40 convertor = new VersionConvertor_30_40();

        MedicationRequest r4medicationRequest = (MedicationRequest) convertor.convertResource(stu3medicationRequest,true);
        r4medicationRequest.setMeta(null);

        List<Extension> extensions = new ArrayList<>();
        for (Extension extension : r4medicationRequest.getExtension()) {
            if (extension.getUrl().equals("https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationRepeatInformation-1")) {
                extension.setUrl("https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation");
                extensions.add(extension);
            }
            if (extension.getUrl().equals("https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-PrescriptionType-1")) {
                if (extension.hasValue() && extension.getValue() instanceof CodeableConcept) {
                    CodeableConcept codeableConcept = (CodeableConcept) extension.getValue();
                    if (codeableConcept.hasCoding() && codeableConcept.getCodingFirstRep().getSystem().equals("https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-PrescriptionType-1")) {
                        switch (codeableConcept.getCodingFirstRep().getCode()) {
                            case "repeat":
                                r4medicationRequest.setCourseOfTherapyType(
                                        new CodeableConcept().addCoding(
                                                new Coding()
                                                        .setSystem("http://terminology.hl7.org/CodeSystem/medicationrequest-course-of-therapy")
                                                        .setCode("continuous")
                                                        .setDisplay("Continuous long term therapy")
                                        )
                                );
                                break;

                            case "acute":
                                r4medicationRequest.setCourseOfTherapyType(
                                        new CodeableConcept().addCoding(
                                                new Coding()
                                                        .setSystem("http://terminology.hl7.org/CodeSystem/medicationrequest-course-of-therapy")
                                                        .setCode("acute")
                                                        .setDisplay("Short course (acute) therapy")
                                        )
                                );
                                break;

                            case "repeat-dispensing":
                                r4medicationRequest.setCourseOfTherapyType(
                                        new CodeableConcept().addCoding(
                                                new Coding()
                                                        .setSystem("https://fhir.nhs.uk/CodeSystem/medicationrequest-course-of-therapy")
                                                        .setCode("continuous-repeat-dispensing")
                                                        .setDisplay("Continuous long term (repeat dispensing)")
                                        )
                                );
                                break;
                            case "acute-handwritten" :
                            case "delayed-prescribing":
                                // not mapped!!
                        }
                    }
                }

            }
        }
        r4medicationRequest.setExtension(extensions);


        return r4medicationRequest;
    }
}

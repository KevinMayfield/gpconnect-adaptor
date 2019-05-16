package uk.gov.dwp.GPCAdaptor.support;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Parameters;

public abstract class StructuredRecord {

    public static Parameters getStructuredRecordParameters(String nhsNumber,boolean resolevAllergies, boolean prescriptionIssues, DateType fromDate) {
        final Parameters theParameters = new Parameters();
        final Parameters.ParametersParameterComponent param = theParameters.addParameter();
        param.setName("patientNHSNumber");
        final Identifier identifier = new Identifier();

        identifier.setValue(nhsNumber);
        identifier.setSystem("https://fhir.nhs.uk/Id/nhs-number");
        param.setValue(identifier);

        final Parameters.ParametersParameterComponent paramAllergies = theParameters.addParameter();
        paramAllergies.setName("includeAllergies");
        final Parameters.ParametersParameterComponent paramAllergiesResolved = paramAllergies.addPart();
        paramAllergiesResolved.setName("includeResolvedAllergies");
        paramAllergiesResolved.setValue(new BooleanType(resolevAllergies));

        final Parameters.ParametersParameterComponent paramMeds = theParameters.addParameter();
        paramMeds.setName("includeMedication");
        final Parameters.ParametersParameterComponent paramPrescriptionIssues = paramMeds.addPart();
        paramPrescriptionIssues.setName("includePrescriptionIssues");
        paramPrescriptionIssues.setValue(new BooleanType(prescriptionIssues));
        final Parameters.ParametersParameterComponent paramSearchFrom = paramMeds.addPart();
        paramSearchFrom.setName("medicationSearchFromDate");
        paramSearchFrom.setValue(fromDate);

        return theParameters;
    }

    public static ca.uhn.fhir.model.dstu2.resource.Parameters getUnStructuredRecordParameters(String nhsNumber,boolean resolevAllergies, boolean prescriptionIssues, DateType fromDate) {
        final ca.uhn.fhir.model.dstu2.resource.Parameters theParameters = new ca.uhn.fhir.model.dstu2.resource.Parameters();
        ca.uhn.fhir.model.dstu2.resource.Parameters.Parameter param = theParameters.addParameter();
        param.setName("patientNHSNumber");
        param.setValue(
                new IdentifierDt()
                        .setSystem("http://fhir.nhs.net/Id/nhs-number")
                .setValue(nhsNumber)
        );
        param = theParameters.addParameter();
        param.setName("recordSection");
        CodeableConceptDt code = new CodeableConceptDt();
        code.addCoding()
                .setSystem("http://fhir.nhs.net/ValueSet/gpconnect-record-section-1")
                .setCode("SUM");
        param.setValue(code);

        return theParameters;

    }
}

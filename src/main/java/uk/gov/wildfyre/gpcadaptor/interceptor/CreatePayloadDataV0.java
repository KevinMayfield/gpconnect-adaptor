package uk.gov.wildfyre.gpcadaptor.interceptor;

import com.google.gson.JsonObject;
import org.jglue.fluentjson.JsonBuilderFactory;
import org.jglue.fluentjson.JsonObjectBuilder;

import java.util.Date;

public class CreatePayloadDataV0 {

    public String buildPayloadData(Date exp, Date iat, String nhsNumber) {

        return "{\n" +
                " \"iss\": \"https://orange.testlab.nhs.uk/\",\n" +
                " \"sub\": \"1\",\n" +
                " \"aud\": \"https://authorize.fhir.nhs.net/token\",\n" +
                " \"exp\": \""+ exp.getTime()/1000 +"\",\n" +
                " \"iat\": \""+ iat.getTime()/1000 +"\",\n" +
                " \"reason_for_request\": \"directcare\",\n" +
                " \"requested_record\": {\n" +
                "  \"resourceType\": \"Patient\",\n" +
                "  \"identifier\": [\n" +
                "   {\n" +
                "    \"system\": \"http://fhir.nhs.net/Id/nhs-number\",\n" +
                "    \"value\": \""+nhsNumber+"\"\n" +
                "   }\n" +
                "  ]\n" +
                " },\n" +
                " \"requested_scope\": \"patient/*.read\",\n" +
                " \"requesting_device\": {\n" +
                "  \"resourceType\": \"Device\",\n" +
                "  \"id\": \"1\",\n" +
                "  \"identifier\": [\n" +
                "    {\n" +
                "      \"system\": \"https://orange.testlab.nhs.uk/gpconnect-demonstrator/Id/local-system-instance-id\",\n" +
                "      \"value\": \"gpcdemonstrator-0-orange\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"model\": \"GP Connect Demonstrator\",\n" +
                "  \"version\": \"0.5.2\"\n" +
                " },\n" +
                " \"requesting_organization\": {\n" +
                "  \"resourceType\": \"Organization\",\n" +
                "  \"id\": \"1\",\n" +
                "  \"identifier\": [\n" +
                "   {\n" +
                "    \"system\": \"http://fhir.nhs.net/Id/ods-organization-code\",\n" +
                "    \"value\": \"A11111\"\n" +
                "   }\n" +
                "  ],\n" +
                "  \"name\": \"Consumer organisation name\"\n" +
                " },\n" +
                " \"requesting_practitioner\": {\n" +
                "  \"resourceType\": \"Practitioner\",\n" +
                "  \"id\": \"1\",\n" +
                "  \"identifier\": [\n" +
                "   {\n" +
                "    \"system\": \"http://fhir.nhs.net/sds-user-id\",\n" +
                "    \"value\": \"G13579135\"\n" +
                "   },\n" +
                "   {\n" +
                "    \"system\": \"https://orange.testlab.nhs.uk/gpconnect-demonstrator/Id/local-user-id\",\n" +
                "    \"value\": \"1\"\n" +
                "   }\n" +
                "  ],\n" +
                "  \"name\": {\n" +
                "    \"family\": [\n" +
                "      \"Demonstrator\"\n" +
                "    ],\n" +
                "    \"given\": [\n" +
                "      \"GPConnect\"\n" +
                "    ],\n" +
                "    \"prefix\": [\n" +
                "      \"Mr\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"practitionerRole\": [\n" +
                "    {\n" +
                "      \"role\": {\n" +
                "        \"coding\": [\n" +
                "          {\n" +
                "            \"system\": \"http://fhir.nhs.net/ValueSet/sds-job-role-name-1\",\n" +
                "            \"code\": \"sds-job-role-name\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                " }\n" +
                "}";

    }


    public JsonObjectBuilder<?,JsonObject> getName(String nameType, String name) {
        return JsonBuilderFactory.buildObject()
                .addArray(nameType) //
                .add(name)
                .end();
    }
}

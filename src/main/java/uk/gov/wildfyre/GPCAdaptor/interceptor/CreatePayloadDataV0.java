package uk.gov.wildfyre.GPCAdaptor.interceptor;

import com.google.gson.JsonObject;
import org.jglue.fluentjson.JsonBuilderFactory;
import org.jglue.fluentjson.JsonObjectBuilder;

import java.util.Date;

public class CreatePayloadDataV0 {

    public String buildPayloadData(Date exp, Date iat, String nhsNumber, boolean write) {

        System.out.println("Payload 0");

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
        // Fluent Json is a Java fluent builder for creating JSON using Google Gson
        /*
        JsonObject jsonObject = JsonBuilderFactory.buildObject() //

                // Registered Claims
                .add("iss", "https://orange.testlab.nhs.uk/") //
                .add("sub", "1") //
                .add("aud", "https://authorize.fhir.nhs.net/token") //
                .add("exp", exp.getTime()/1000) //
                .add("iat", iat.getTime()/1000) //

                // Custom Claims
                .add("reason_for_request", "directcare") //


                .addObject("requested_record")
                    .add("resourceType","Patient")
                    .addArray("identifier")
                        .addObject()
                            .add("system","http://fhir.nhs.net/Id/nhs-number")
                            .add("value","9658218873")
                        .end()
                    .end()
                .end()

                .add("requested_scope", "patient/*.read") //

                .addObject("requesting_device") //
                    .add("resourceType", "Device") //
                    .add("id", "1") //
                    .addArray("identifier") //
                        .addObject()
                            .add("system","https://orange.testlab.nhs.uk/gpconnect-demonstrator/Id/local-system-instance-id") //
                            .add("value","gpcdemonstrator-0-orange") //
                        .end() //
                    .end()
                    .add("model", "DWP-Demonstrator") //
                    .add("version", "3.8.0-SNAPSHOT") //
                .end()//


                .addObject("requesting_organization") //
                    .add("resourceType", "Organization") //
                    .add("id", "1") //
                    .addArray("identifier") //
                        .addObject()
                            .add("system","http://fhir.nhs.net/Id/ods-organization-code") //
                            .add("value","A11111") //
                        .end() //
                    .end()
                    .add("name", "GP, HCP or Patient") //
                .end()//

                .addObject("requesting_practitioner") //
                    .add("resourceType", "Practitioner") //
                    .add("id", "1") //
                    .addArray("identifier") //
                        .addObject()
                            .add("system","http://fhir.nhs.net/sds-user-id") //
                            .add("value","G13579135") //
                        .end() //
                        .addObject()
                            .add("system","https://orange.testlab.nhs.uk/gpconnect-demonstrator/Id/local-user-id") //
                            .add("value","1") //
                        .end() //
                    .end()
                    .addObject("name")

                         .addArray("family")
                            .add("Demonstrator")
                         .end()
                        .addArray("given")
                            .add("DWPConnect")
                        .end()
                        .addArray("prefix")
                            .add("Ms")
                        .end()
                    .end()
                    .addArray("practitionerRole")
                        .addObject()
                            .addObject("role")
                                .addArray("coding")
                                    .addObject()
                                        .add("system","http://fhir.nhs.net/ValueSet/sds-job-role-name-1") //
                                        .add("value","sds-job-role-name") //
                                    .end()
                                .end()
                            .end()
                        .end()
                    .end()
                .end()//


                .getJson();

        String json = jsonObject. toString();

        return json;

         */
    }


    private String getScope(boolean write) {
        String scope = "patient/*.read";
        if (write) {
            scope = "patient/*.write";
        }
        return scope;
    }

    public JsonObjectBuilder<?,JsonObject> getName(String nameType, String name) {
        return JsonBuilderFactory.buildObject()
                .addArray(nameType) //
                .add(name)
                .end();
    }
}

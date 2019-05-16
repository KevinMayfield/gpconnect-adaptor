package uk.gov.dwp.GPCAdaptor.support;

import com.google.gson.JsonObject;
import org.jglue.fluentjson.JsonBuilderFactory;
import org.jglue.fluentjson.JsonObjectBuilder;

import java.util.Date;

public class CreatePayloadDataV0 {

    public String buildPayloadData(Date exp, Date iat, boolean write) {

        // Fluent Json is a Java fluent builder for creating JSON using Google Gson
        JsonObject jsonObject = JsonBuilderFactory.buildObject() //

                // Registered Claims
                .add("iss", "http://localhost:8182") //
                .add("sub", "1") //
                .add("aud", "https://demo.gov.uk/gpc-adaptor") //
                .add("exp", exp.getTime()/1000) //
                .add("iat", iat.getTime()/1000) //

                // Custom Claims
                .add("reason_for_request", "directcare") //


                .addObject("requested_record")
                    .add("resourceType","Organization")
                    .add("id","1")
                    .addArray("identifier")
                        .addObject()
                            .add("system","http://fhir.nhs.net/Id/ods-organization-code")
                            .add("value","A11111")
                        .end()
                    .end()
                .end()

                .add("requested_scope", "organization/*.read") //

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
                            .add("system","https://fhir.nhs.uk/Id/ods-organization-code") //
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
                    .addArray("name")
                        .addObject()
                            .add("family", "Assurance")
                        .end()
                        .add(getName("given", "GpConnect"))
                        .add(getName("prefix", "Ms"))
                    .end()
                    .addArray("practitionerRole")
                        .addObject()
                            .addArray("role")
                            .end()
                        .end()
                    .end()
                .end()//


                .getJson();

        String json = jsonObject. toString();

        return json;
    }

    /*


     */
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

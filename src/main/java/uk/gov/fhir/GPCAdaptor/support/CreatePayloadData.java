package uk.gov.fhir.GPCAdaptor.support;

import java.util.Date;

import org.jglue.fluentjson.JsonBuilderFactory;
import org.jglue.fluentjson.JsonObjectBuilder;

import com.google.gson.JsonObject;

public class CreatePayloadData {

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

                .add("requested_scope", "organization/*.read") //
                .addObject("requesting_device") //
                .add("resourceType", "Device") //
             // KGM   .add("id", "1") //
                .addArray("identifier") //
                .addObject()
                .add("system","https://demo.gov.uk/gpc-adaptor") //
                .add("value","DWP-Client-Demo-App") //
                .end() //
                .end()
                /*
                .addObject("type")
                .addArray("coding") //
                .addObject()
                .add("system","DeviceIdentifierSystem") //
                .add("code","DeviceIdentifier") //
                .end()
                .end()
                .end() */
                .add("model", "DWP-Demonstrator") //
                .add("version", "3.8.0-SNAPSHOT") //
                .end()//

                .addObject("requesting_organization") //
                .add("resourceType", "Organization") //
                // KGM .add("id", "1") //
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
                .add("system","https://fhir.nhs.uk/Id/sds-user-id") //
                .add("value","111111111111") //
                .end() //
                .addObject()
                .add("system","https://fhir.nhs.uk/Id/sds-role-profile-id") //
                .add("value","22222222222222") //
                .end() //
                .addObject()
                .add("system","https://orange.testlab.nhs.uk/gpconnect-demonstrator/Id/local-user-id") //
                .add("value","1") //
                .end() //
                .end()
                .addArray("name")
                .addObject()
                .add("family", "Assurance")
                .addArray("given")
                .add("DWPConnect")
                .end()
                .addArray("prefix")
                .add("Ms")
                .end()
                .end()
                .end()//


                .getJson();

        String json = jsonObject. toString();

        return json;
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

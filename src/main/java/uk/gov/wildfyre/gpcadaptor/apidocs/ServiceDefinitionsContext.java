package uk.gov.wildfyre.gpcadaptor.apidocs;

import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import uk.gov.wildfyre.gpcadaptor.HapiProperties;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceDefinitionsContext {


    public List<SwaggerResource> getSwaggerDefinitions() {

        List<SwaggerResource> resources = new ArrayList<>();
        SwaggerResource resource = new SwaggerResource();
        resource.setLocation("/apidocs" );
        resource.setName(HapiProperties.getServerName());
        resource.setSwaggerVersion("2.0");
        resources.add(resource);
        return  resources;
    }
}

package uk.gov.wildfyre.gpcadaptor;

import ca.uhn.fhir.context.FhirContext;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import uk.gov.wildfyre.gpcadaptor.support.CorsFilter;
import uk.gov.wildfyre.gpcadaptor.interceptor.SSPInterceptor;

@SpringBootApplication
public class GPCAdaptor {

    @Autowired
    ApplicationContext context;


    public static void main(String[] args) {
        System.setProperty("hawtio.authenticationEnabled", "false");
        System.setProperty("management.security.enabled","false");
        System.setProperty("management.contextPath","");
        SpringApplication.run(GPCAdaptor.class, args);

    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CustomRestfulServer(context), "/R4/*");
        registration.setName("FhirServlet");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    CorsConfigurationSource
    corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    @Primary
    public FhirContext fhirContextBean() {
        return FhirContext.forR4();
    }

    @Bean("r3ctx")
    public FhirContext fhirContextBeanDSTU2() {
        return FhirContext.forDstu3();
    }


    @Bean
    @Primary
    public IGenericClient getGPCConnection(@Qualifier("r3ctx") FhirContext ctx) {
        SSPInterceptor sspInterceptor = new SSPInterceptor();

        IGenericClient client = ctx.newRestfulGenericClient(HapiProperties.getGpConnectServer());

        client.registerInterceptor(sspInterceptor );
        return client;
    }


    @Bean
    public FilterRegistrationBean corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter());
        bean.setOrder(0);
        return bean;
    }

}

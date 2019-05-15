package uk.gov.dwp.GPCAdaptor.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.dwp.GPCAdaptor.HapiProperties;
import uk.gov.dwp.GPCAdaptor.support.CreateAuthToken;
import uk.gov.dwp.GPCAdaptor.support.SSPInterceptor;
import uk.gov.dwp.GPCAdaptor.support.StructuredRecord;

import java.util.List;

@Component
public class MedicationStatementDao implements IMedicationStatement {

    private static final Logger log = LoggerFactory.getLogger(MedicationStatementDao.class);

    @Override
    public List<MedicationStatement> search(FhirContext ctx, ReferenceParam patient) throws Exception {


        SSPInterceptor sspInterceptor = new SSPInterceptor();
        //ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        IGenericClient client = ctx.newRestfulGenericClient(HapiProperties.getGpConnectServer());

        client.registerInterceptor(CreateAuthToken.createAuthInterceptor(false));
        client.registerInterceptor(sspInterceptor);

        log.info(patient.getIdPart() );

        Parameters parameters = StructuredRecord.getStructuredRecordParameters(patient.getValue(),false, false, new DateType(1980, 5, 5));
        Bundle result = client.operation().onType(Patient.class)
                .named("$gpc.getstructuredrecord")
                .withParameters(parameters)
                .returnResourceType(Bundle.class)
                .execute();

        return null;
    }


}

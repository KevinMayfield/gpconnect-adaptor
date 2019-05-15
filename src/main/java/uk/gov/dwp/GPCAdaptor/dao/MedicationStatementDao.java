package uk.gov.dwp.GPCAdaptor.dao;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MedicationStatementDao implements IMedicationStatement {

    private static final Logger log = LoggerFactory.getLogger(MedicationStatementDao.class);

    @Override
    public List<MedicationStatement> search(IGenericClient client, ReferenceParam patient) throws Exception {
        log.info(patient.getIdPart() );
        return null;
        /*
        return client
                .search()
                .forResource(CarePlan.class)
                .where(CarePlan.PATIENT.hasId(patient.getIdPart()))
                .and(CarePlan.CATEGORY.exactly().code(carePlanType.getValue()))
                .returnBundle(Bundle.class)
                .execute();

         */
    }


}

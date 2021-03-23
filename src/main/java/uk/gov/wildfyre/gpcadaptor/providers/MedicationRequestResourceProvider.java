package uk.gov.wildfyre.gpcadaptor.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.gpcadaptor.dao.IMedicationRequest;
import uk.gov.wildfyre.gpcadaptor.transforms.STU3MedicationtoR4MedicationRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class MedicationRequestResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;

    @Autowired
    IGenericClient client;

    @Autowired
    IMedicationRequest resourceDao;

    @Autowired
    STU3MedicationtoR4MedicationRequest stu3MedicationtoR4MedicationRequest;

    @Override
    public Class<MedicationRequest> getResourceType() {
        return MedicationRequest.class;
    }

    private static final Logger log = LoggerFactory.getLogger(MedicationRequestResourceProvider.class);

    @Search
    public List<Resource> search(HttpServletRequest httpRequest,
                                 @RequiredParam(name = MedicationRequest.SP_PATIENT) ReferenceParam patient,
                                 @OptionalParam(name= MedicationRequest.SP_STATUS)TokenParam status
                                 )  {

       List<org.hl7.fhir.dstu3.model.Resource>  stu3resources = resourceDao.search(client, patient, status);

       List<Resource> r4resources = new ArrayList<>();
       for(org.hl7.fhir.dstu3.model.Resource resource : stu3resources) {
            if (resource instanceof org.hl7.fhir.dstu3.model.MedicationRequest) {

                if (status == null) {
                    r4resources.add(stu3MedicationtoR4MedicationRequest.transform((org.hl7.fhir.dstu3.model.MedicationRequest) resource));
                }
                else {
                    MedicationRequest medicationRequest = stu3MedicationtoR4MedicationRequest.transform((org.hl7.fhir.dstu3.model.MedicationRequest) resource);
                    log.info(medicationRequest.getStatus().getDisplay());
                    log.info(medicationRequest.getStatus().getSystem());
                    log.info(medicationRequest.getStatus().toCode());
                    if (medicationRequest.getStatus().toCode().equals(status.getValue())) {
                        r4resources.add(medicationRequest);
                    }
                }
            }
       }
       return r4resources;

    }




}

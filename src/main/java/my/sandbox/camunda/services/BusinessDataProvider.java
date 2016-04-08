package my.sandbox.camunda.services;

import my.sandbox.camunda.data.model.BusinessData;
import my.sandbox.camunda.data.repository.BusinessDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("businessDataProvider")
public class BusinessDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessDataProvider.class);
    
    @Autowired
    protected BusinessDataRepository dataRepository;

    public String getLatestData() {
        BusinessData businessData = dataRepository.findTopByOrderByIdDesc();
        if (businessData == null) {
            LOG.warn("No business data was found!");
            return "EMPTY_DATA";
        }
        
        return businessData.getText();
    }
    
}

package my.sandbox.camunda.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Configuration
public class DataSourceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfiguration.class);
   
    
//    @Bean(name="camundaBpmDataSource")
//    DataSource customBpmDataSource() {
//        LOG.debug("Creating a custom DataSource for Camunda Engine");
//        return DataSourceBuilder.create()
//                .url("jdbc:h2:~/.h2/camunda/process-engine;DB_CLOSE_DELAY=-1;MVCC=TRUE;DB_CLOSE_ON_EXIT=FALSE")
//                .username("sa")
//                .password("sa")
//                .build();
//    }

}

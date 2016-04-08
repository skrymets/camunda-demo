package my.sandbox.camunda.data.repository;

import java.util.List;
import javax.transaction.Transactional;
import my.sandbox.camunda.data.model.BusinessData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
//@Transactional
public interface BusinessDataRepository extends CrudRepository<BusinessData, Long> {

    List<BusinessData> findByTextLike(String text);
    
    BusinessData findTopByOrderByIdDesc();
}

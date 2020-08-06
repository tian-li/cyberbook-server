package app.cyberbook.cyberbookserver.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.server.ExposesResourceFor;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, String> {
    @RestResource(exported = false)
    List<Transaction> findAllByUserId(String userId);
}

package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @RestResource(exported = false)
    List<Transaction> findAllByUserId(String userId);
}

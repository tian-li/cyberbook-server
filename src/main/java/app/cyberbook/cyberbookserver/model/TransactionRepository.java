package app.cyberbook.cyberbookserver.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, String> {
    List<Transaction> findAllByUserId(String userId);
}

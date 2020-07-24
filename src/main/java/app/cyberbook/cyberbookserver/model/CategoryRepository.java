package app.cyberbook.cyberbookserver.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, String> {
    List<Category> findAllByUserId(String userId);
}

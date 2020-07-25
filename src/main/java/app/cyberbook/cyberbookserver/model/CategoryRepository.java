package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAllByUserId(String userId);
}

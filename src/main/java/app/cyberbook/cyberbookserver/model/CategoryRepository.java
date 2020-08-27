package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @RestResource(exported = false)
    List<Category> findAllByUserId(String userId);
}

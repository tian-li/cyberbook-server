package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "asdasd", path = "categories")
public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAllByUserId(@Param("userId") String userId);
}

package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

public interface UserRepository extends JpaRepository<User, String> {
    @RestResource(exported = false)
    User findByEmail(String email);

    @RestResource(exported = false)
    boolean existsByEmail(String email);

    @RestResource(exported = false)
    boolean existsByUsername(String username);
}

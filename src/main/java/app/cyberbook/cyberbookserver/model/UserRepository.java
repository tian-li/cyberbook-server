package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}

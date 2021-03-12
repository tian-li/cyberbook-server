package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    @RestResource(exported = false)
    User findByEmail(String email);

    @RestResource(exported = false)
    User findByUsernameOrEmail(String username, String email);

    @RestResource(exported = false)
    boolean existsByEmail(String email);

    @RestResource(exported = false)
    boolean existsByUsername(String username);

    @RestResource(exported = false)
    User findByRoles(Role role);

    @RestResource(exported = false)
    List<User> findByDateRegisteredLessThanAndRegisteredIsFalse(String dateRegistered);

//    @RestResource(exported = false)
//    List<MessageThread> findMessageThreadsById(String userId);
}

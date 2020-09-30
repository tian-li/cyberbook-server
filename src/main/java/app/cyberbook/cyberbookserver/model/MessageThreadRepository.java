package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface MessageThreadRepository extends JpaRepository<MessageThread, String> {

//    @RestResource(exported = false)
//    List<MessageThread> findAllByUsers(User user);
}

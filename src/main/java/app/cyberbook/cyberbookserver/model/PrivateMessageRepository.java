package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, String> {

    @RestResource(exported = false)
    List<PrivateMessage> findAllByMessageThreadId(String threadId);
}

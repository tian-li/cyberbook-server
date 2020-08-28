package app.cyberbook.cyberbookserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    @RestResource(exported = false)
    List<Subscription> findAllByUserId(String userId);
}

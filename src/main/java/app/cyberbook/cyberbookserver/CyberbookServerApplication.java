package app.cyberbook.cyberbookserver;

import app.cyberbook.cyberbookserver.service.CategoryService;
import app.cyberbook.cyberbookserver.service.SubscriptionService;
import app.cyberbook.cyberbookserver.service.TransactionService;
import app.cyberbook.cyberbookserver.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.TimeZone;

@SpringBootApplication
public class CyberbookServerApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(CyberbookServerApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public CategoryService categoryService() {
        return new CategoryService();
    }

    @Bean
    public TransactionService transactionService() {
        return new TransactionService();
    }

    @Bean
    public SubscriptionService subscriptionService() {
        return new SubscriptionService();
    }

}

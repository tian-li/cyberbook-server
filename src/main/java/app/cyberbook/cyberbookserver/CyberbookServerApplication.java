package app.cyberbook.cyberbookserver;

import app.cyberbook.cyberbookserver.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.MultipartConfigElement;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
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
    public UserCreationService userCreationService() {
        return new UserCreationService();
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

    @Bean
    public PrivateMessageService privateMessageService() {
        return new PrivateMessageService();
    }

    @Bean
    public MessageThreadService messageThreadService() {
        return new MessageThreadService();
    }

    @Bean
    public FileService fileService() {
        return new FileService();
    }

//    @Bean
//    MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setLocation("/ftpfiles/");
//        return factory.createMultipartConfig();
//    }



}

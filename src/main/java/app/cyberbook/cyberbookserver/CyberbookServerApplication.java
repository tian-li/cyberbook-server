package app.cyberbook.cyberbookserver;

import app.cyberbook.cyberbookserver.model.CategoryDTO;
import app.cyberbook.cyberbookserver.service.CategoryService;
import app.cyberbook.cyberbookserver.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class CyberbookServerApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(CyberbookServerApplication.class, args);


//        ObjectMapper objectMapper = new ObjectMapper();
//
//        //read json file and convert to customer object
//        try {
//            List<CategoryDTO> categoryDTOs = objectMapper.readValue(new ClassPathResource("./default-categories.json").getFile(), ArrayList.class);
//            System.out.println("categoryDTOs " + categoryDTOs);
//        } catch (IOException e) {
//            System.out.println("error " + e);
//        }

        //print customer details
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

}

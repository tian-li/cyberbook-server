package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.exception.CyberbookException;
import app.cyberbook.cyberbookserver.model.CategoryDTO;
import app.cyberbook.cyberbookserver.model.Role;
import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.model.UserRepository;
import app.cyberbook.cyberbookserver.security.JwtTokenProvider;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CategoryService categoryService;

    public ResponseEntity login(User value) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(value.getEmail(), value.getPassword()));
            return ResponseEntity.ok(
                    jwtTokenProvider.createToken(
                            value.getEmail(),
                            userRepository.findByEmail(value.getEmail()).getRoles()
                    )
            );
        } catch (AuthenticationException e) {
            return new ResponseEntity("Invalid email/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public ResponseEntity registerTempUser() {
        User user = new User();

        user.setUsername("新用户" + UUID.randomUUID().toString().substring(0, 4));
        user.setBirthday(null);
        user.setEmail(null);
        user.setGender(null);
        user.setProfilePhotoUrl(null);
        user.setRegistered(false);
        user.setDateRegistered(DateTime.now().getMillis());

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_CLIENT);
        user.setRoles(roles);
        userRepository.save(user);

        try {
            CategoryDTO[] categoryDTOs = categoryService.generateDefaultCategoryDTOs();
            System.out.println("categoryDTOs" + categoryDTOs);
            categoryService.createCategories(categoryDTOs, user.getId());
        } catch (IOException e) {
            return new ResponseEntity("Error when creating default categories", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(jwtTokenProvider.createToken(user.getUsername(), user.getRoles()));
    }

    public ResponseEntity register(User value) {
        if (value == null || value.getEmail() == null || value.getPassword() == null) {
            return new ResponseEntity("Invalid register info", HttpStatus.BAD_REQUEST);
        }

        User user = new User();

        if (value.getId() == null) {
            user.setBirthday(null);
            user.setEmail(value.getEmail());
            user.setPassword(value.getPassword());
            user.setGender(null);
            user.setProfilePhotoUrl(null);
            user.setRegistered(true);
            user.setDateRegistered(DateTime.now().getMillis());
        } else {
            Optional<User> existingUser = userRepository.findById(value.getId());

            if (existingUser.isPresent()) {
                // If this is an existing temp user
                user = existingUser.get();
                user.setEmail(value.getEmail());
                user.setPassword(value.getPassword());
                user.setRegistered(true);
            }
        }

        if (userRepository.existsByUsername(value.getUsername())) {
            throw new CyberbookException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        } else if (userRepository.existsByEmail(value.getEmail())) {
            throw new CyberbookException("Email is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            List<Role> roles = new ArrayList<>();
            roles.add(Role.ROLE_CLIENT);
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(value.getPassword()));
        }

        userRepository.save(user);
        return ResponseEntity.ok(jwtTokenProvider.createToken(user.getUsername(), user.getRoles()));
    }

    public User getUserByHttpRequestToken(HttpServletRequest req) {
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req));
        User user = userRepository.findByEmail(email);
        return user;
    }

}

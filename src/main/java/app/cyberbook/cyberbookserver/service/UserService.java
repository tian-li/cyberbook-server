package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.exception.CyberbookException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;
//

    public ResponseEntity login(User value) {
        System.out.println("login in service: " + value);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(value.getEmail(), value.getPassword()));
            System.out.println("authenticate");
            return ResponseEntity.ok(
                    jwtTokenProvider.createToken(
                            value.getEmail(),
                            userRepository.findByEmail(value.getEmail()).getRoles()
                    )
            );
        } catch (AuthenticationException e) {
            return new ResponseEntity("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    //
    public ResponseEntity register(User value) {
        if (value == null || value.getEmail() == null || value.getPassword() == null) {
            return new ResponseEntity("Invalid register info", HttpStatus.BAD_REQUEST);
        }
        Optional<User> existingUser = userRepository.findById(value.getId());
        User user;

        if (existingUser.isPresent()) {
            // If this is an existing temp user
            user = existingUser.get();
            user.setEmail(value.getEmail());
            user.setPassword(value.getPassword());
            user.setRegistered(true);
        } else {
            user = new User();

            user.setBirthday(null);
            user.setEmail(value.getEmail());
            user.setPassword(value.getPassword());
            user.setGender(null);
            user.setProfilePhotoUrl(null);
            user.setRegistered(true);
            user.setDateRegistered(DateTime.now().getMillis());
        }

        if (userRepository.existsByUsername(value.getUsername())) {
            throw new CyberbookException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        } else if (userRepository.existsByEmail(value.getEmail())) {
            throw new CyberbookException("Email is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            ArrayList<Role> role = new ArrayList<>();
            role.add(Role.ROLE_CLIENT);
            user.setRoles(role);
            user.setPassword(passwordEncoder.encode(value.getPassword()));
        }

        System.out.println("user to save" + user);

        userRepository.save(user);
        return ResponseEntity.ok(jwtTokenProvider.createToken(user.getUsername(), user.getRoles()));
    }

}

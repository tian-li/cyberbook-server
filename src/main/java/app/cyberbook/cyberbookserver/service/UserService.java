package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
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

    public ResponseEntity<CyberbookServerResponse<UserDTO>> loginWithToken(HttpServletRequest req) {
        User user = getUserByHttpRequestToken(req);

        if (user != null) {
            return ResponseEntity.ok(
                    CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                            user,
                            jwtTokenProvider.createToken(user.getEmail(), user.getRoles())
                    ))
            );
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> login(User value) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(value.getEmail(), value.getPassword());

            authenticationManager.authenticate(token);

            User user = userRepository.findByEmail(value.getEmail());

            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getRoles());

            return ResponseEntity.ok(
                    CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                            user,
                            jwtToken
                    ))
            );
        } catch (AuthenticationException e) {
            return new ResponseEntity("Invalid email/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> registerTempUser() {
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
            List<CategoryDTO> categoryDTOs = categoryService.generateDefaultCategoryDTOs();
            categoryService.createCategories(categoryDTOs, user.getId());
        } catch (IOException e) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Error when creating default categories"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                        user,
                        jwtTokenProvider.createToken(user.getUsername(), user.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> register(User value, HttpServletRequest req) {
        if (value == null || value.getEmail() == null || value.getPassword() == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid register info"), HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        User tokenUser = getUserByHttpRequestToken(req);

        if (tokenUser == null) {
            if (value.getUsername() == null) {
                return new ResponseEntity(CyberbookServerResponse.noDataMessage("Username can't be empty"), HttpStatus.BAD_REQUEST);
            }

            if (userRepository.existsByUsername(value.getUsername())) {
                return new ResponseEntity(CyberbookServerResponse.noDataMessage("Username is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            user.setUsername(value.getUsername());
            user.setBirthday(null);
            user.setEmail(value.getEmail());
            user.setPassword(value.getPassword());
            user.setGender(null);
            user.setProfilePhotoUrl(null);
            user.setRegistered(true);
            user.setDateRegistered(DateTime.now().getMillis());

        } else {
            if (!user.getUsername().equals(value.getUsername()) && userRepository.existsByUsername(value.getUsername())) {
                return new ResponseEntity(CyberbookServerResponse.noDataMessage("Username is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // If this is an existing temp user
            user = tokenUser;
            user.setUsername(value.getUsername());
            user.setEmail(value.getEmail());
            user.setPassword(value.getPassword());
            user.setRegistered(true);
        }

        if (userRepository.existsByEmail(value.getEmail())) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Email is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_CLIENT);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(value.getPassword()));

        userRepository.save(user);

        if (tokenUser == null) {
            try {
                List<CategoryDTO> categoryDTOs = categoryService.generateDefaultCategoryDTOs();
                categoryService.createCategories(categoryDTOs, user.getId());
            } catch (IOException e) {
                return new ResponseEntity("Error when creating default categories", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                        user,
                        jwtTokenProvider.createToken(user.getEmail(), user.getRoles())
                ))
        );

//        return ResponseEntity.ok(jwtTokenProvider.createToken(user.getEmail(), user.getRoles()));
    }

    public User getUserByHttpRequestToken(HttpServletRequest req) {
        String token = jwtTokenProvider.resolveToken(req);
        if (token != null) {
            String usernameOrEmail = jwtTokenProvider.getUsername(token);
            return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        } else {
            return null;
        }

    }

    private UserDTO createUserDTOWithUserAndToken(User user, String jwtToken) {
        UserDTO userDTO = new UserDTO();

        userDTO.setBirthday(user.getBirthday());
        userDTO.setDateRegistered(user.getDateRegistered());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setId(user.getId());
        userDTO.setProfilePhotoUrl(user.getProfilePhotoUrl());
        userDTO.setRegistered(user.getRegistered());
        userDTO.setUsername(user.getUsername());
        userDTO.setJwtToken(jwtToken);

        return userDTO;
    }

}

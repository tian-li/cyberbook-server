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

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

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
        user.setDateRegistered(DateTime.now().toString(ISOFormat));

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_CLIENT);
        user.setRoles(roles);
        userRepository.save(user);

        try {
            List<CategoryDTO> categoryDTOs = categoryService.generateDefaultCategoryDTOs();
            categoryService.createCategories(categoryDTOs, user.getId());
        } catch (IOException e) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Error when creating default categories when creating temp user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                        user,
                        jwtTokenProvider.createToken(user.getUsername(), user.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> register(User value) {
        if (value == null || value.getUsername() == null || value.getEmail() == null || value.getPassword() == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid register info"), HttpStatus.BAD_REQUEST);
        }
        User user = new User();

        if (userRepository.existsByUsername(value.getUsername())) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Username is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (userRepository.existsByEmail(value.getEmail())) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Email is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        user.setUsername(value.getUsername());
        user.setBirthday(null);
        user.setEmail(value.getEmail());
        user.setGender(0);
        user.setProfilePhotoUrl(null);
        user.setRegistered(true);
        user.setDateRegistered(DateTime.now().toString(ISOFormat));

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_CLIENT);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(value.getPassword()));

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
                        jwtTokenProvider.createToken(user.getEmail(), user.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> saveTempUser(User value, HttpServletRequest req) {
        if (value == null || value.getUsername() == null || value.getEmail() == null || value.getPassword() == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid register info"), HttpStatus.BAD_REQUEST);
        }

        User tokenUser = getUserByHttpRequestToken(req);

        if (tokenUser == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid temp user token"), HttpStatus.BAD_REQUEST);

        } else {
            // If username is updated when saving temp user, need to check if it is used by other users
            if (!tokenUser.getUsername().equals(value.getUsername()) && userRepository.existsByUsername(value.getUsername())) {
                return new ResponseEntity(CyberbookServerResponse.noDataMessage("Username is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            if (userRepository.existsByEmail(value.getEmail())) {
                return new ResponseEntity(CyberbookServerResponse.noDataMessage("Email is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // If this is an existing temp user
            tokenUser.setUsername(value.getUsername());
            tokenUser.setEmail(value.getEmail());
            tokenUser.setPassword(value.getPassword());
            tokenUser.setRegistered(true);
        }

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_CLIENT);
        tokenUser.setRoles(roles);
        tokenUser.setPassword(passwordEncoder.encode(value.getPassword()));

        userRepository.save(tokenUser);

        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                        tokenUser,
                        jwtTokenProvider.createToken(tokenUser.getEmail(), tokenUser.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> updateProfile(User value, HttpServletRequest req) {
        if (value == null || value.getUsername() == null || value.getEmail() == null || value.getGender() == null || value.getBirthday() == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid profile info"), HttpStatus.BAD_REQUEST);
        }

        User tokenUser = getUserByHttpRequestToken(req);

        if (tokenUser == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid temp user token"), HttpStatus.BAD_REQUEST);
        } else {
            // If username is updated when updating profile, need to check if it is used by other users
            if (!tokenUser.getUsername().equals(value.getUsername()) && userRepository.existsByUsername(value.getUsername())) {
                return new ResponseEntity(CyberbookServerResponse.noDataMessage("Username is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // If username is updated when updating profile, need to check if it is used by other users
            if (!tokenUser.getEmail().equals(value.getEmail()) && userRepository.existsByEmail(value.getEmail())) {
                return new ResponseEntity(CyberbookServerResponse.noDataMessage("Email is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            tokenUser.setUsername(value.getUsername());
            tokenUser.setEmail(value.getEmail());
            tokenUser.setBirthday(value.getBirthday());
            tokenUser.setGender(value.getGender());
        }

        userRepository.save(tokenUser);

        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                        tokenUser,
                        jwtTokenProvider.createToken(tokenUser.getEmail(), tokenUser.getRoles())
                ))
        );
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

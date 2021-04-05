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
import java.util.stream.Collectors;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

public class UserService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    SubscriptionRepository subscriptionRepository;
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

    public User getUserById(String id) {
        return userRepository.findById(id).get();
    }

    public User getFeedbackManagerUser() {
        return userRepository.findByRoles(Role.ROLE_FEEDBACK_MANAGER);
    }

    public User saveTreadToUser(MessageThread messageThread, User user) {
        List<MessageThread> messageThreadList = user.getMessageThreads();
        if (messageThreadList == null) {
            messageThreadList = new ArrayList<>();
        }
        messageThreadList.add(messageThread);
        return userRepository.save(user);
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> loginWithToken(HttpServletRequest req) {
        User user = getUserByHttpRequestToken(req);

        if (user != null) {
            return ResponseEntity.ok(
                    CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                            user,
                            jwtTokenProvider.createToken(user.getUsernameOrEmail(), user.getRoles())
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


            String jwtToken = jwtTokenProvider.createToken(user.getUsernameOrEmail(), user.getRoles());

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
        user.setProfilePhoto(null);
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
                        jwtTokenProvider.createToken(user.getUsernameOrEmail(), user.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> register(User value, Role role) {
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
        user.setProfilePhoto(null);
        user.setRegistered(true);
        user.setDateRegistered(DateTime.now().toString(ISOFormat));

        List<Role> roles = new ArrayList<>();
        roles.add(role);
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
                        jwtTokenProvider.createToken(user.getUsernameOrEmail(), user.getRoles())
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
                        jwtTokenProvider.createToken(tokenUser.getUsernameOrEmail(), tokenUser.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> updateProfile(User value, HttpServletRequest req) {
        if (value == null) {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Invalid profile info"), HttpStatus.BAD_REQUEST);
        }

        User tokenUser = getUserByHttpRequestToken(req);

        if (tokenUser == null) {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Invalid temp user token"), HttpStatus.BAD_REQUEST);
        } else {
//            tokenUser.setUsername(value.getUsername());
//            tokenUser.setEmail(value.getEmail());
//            tokenUser.setBirthday(value.getBirthday());
//            tokenUser.setGender(value.getGender());

            if (value.getUsername() != null) {
                // If username is updated when updating profile, need to check if it is used by other users
                if (!tokenUser.getUsername().equals(value.getUsername()) && userRepository.existsByUsername(value.getUsername())) {
                    return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Username is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
                } else {
                    tokenUser.setUsername(value.getUsername());
                }
            }

            if (value.getEmail() != null) {
                // If email is updated when updating profile, need to check if it is used by other users
                if (!tokenUser.getEmail().equals(value.getEmail()) && userRepository.existsByEmail(value.getEmail())) {
                    return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Email is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
                } else {
                    tokenUser.setEmail(value.getEmail());
                }
            }

            if (value.getBirthday() != null) {
                tokenUser.setBirthday(value.getBirthday());
            }

            if (value.getGender() != null) {
                tokenUser.setGender(value.getGender());
            }

            if (value.getProfilePhoto() != null) {
                tokenUser.setProfilePhoto(value.getProfilePhoto());
            }
        }

        userRepository.save(tokenUser);

        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(createUserDTOWithUserAndToken(
                        tokenUser,
                        jwtTokenProvider.createToken(tokenUser.getUsernameOrEmail(), tokenUser.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse> setTheme(String theme, HttpServletRequest req) {
        User tokenUser = getUserByHttpRequestToken(req);

        if (tokenUser == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid temp user token"), HttpStatus.BAD_REQUEST);
        } else {
            tokenUser.setTheme(theme);
            userRepository.save(tokenUser);
            return ResponseEntity.ok(CyberbookServerResponse.successNoData());
        }
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

        List<MessageThreadDTO> messageThreadDTOList = user.getMessageThreads().stream().map(messageThread -> {
            MessageThreadDTO messageThreadDTO = new MessageThreadDTO();

            messageThreadDTO.setLastMessageDate(messageThread.getLastMessageDate());
            messageThreadDTO.setPreview(messageThread.getPreview());
            messageThreadDTO.setId(messageThread.getId());
            messageThreadDTO.setType(messageThread.getType());

            return messageThreadDTO;
        }).collect(Collectors.toList());

        userDTO.setBirthday(user.getBirthday());
        userDTO.setDateRegistered(user.getDateRegistered());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setId(user.getId());
        userDTO.setProfilePhoto(user.getProfilePhoto());
        userDTO.setRegistered(user.getRegistered());
        userDTO.setUsername(user.getUsername());
        userDTO.setTheme(user.getTheme());
        userDTO.setJwtToken(jwtToken);
        userDTO.setMessageThreads(messageThreadDTOList);

        return userDTO;
    }

}

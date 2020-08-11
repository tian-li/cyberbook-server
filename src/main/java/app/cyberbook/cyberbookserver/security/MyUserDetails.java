package app.cyberbook.cyberbookserver.security;

import app.cyberbook.cyberbookserver.model.Role;
import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//import murraco.model.User;
//import murraco.repository.UserRepository;

@Service
public class MyUserDetails implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername" + email);

        final User user = userRepository.findByEmail(email);

        System.out.println("user find by email" + user);

//        UserDetails u = null;

        if (user == null) {
            throw new UsernameNotFoundException(email + "' not found");
        }

//        UserDetails result = new User(email, "password", true, true, true, true, new GrantedAuthority[]{ new GrantedAuthorityImpl("ROLE_USER") });
//        return result;


//        System.out.println("builder"+builder);
//        System.out.println("build"+ u);
//
//        return u;

        UserDetails u= org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
//                .accountExpired(false)
//                .accountLocked(false)
//                .credentialsExpired(false)
//                .disabled(false)
                .roles(user.getRoles().toString())
                .build();
//
//        System.out.println("user details u: "+ u);
        return u;
    }

}
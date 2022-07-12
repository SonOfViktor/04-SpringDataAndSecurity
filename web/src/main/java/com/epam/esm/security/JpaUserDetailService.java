package com.epam.esm.security;

import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Component
@RequiredArgsConstructor
public class JpaUserDetailService implements UserDetailsService {
    public static final String ROLE = "ROLE_";
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userService.findByEmail(email)
                .map(this::createUserDetailsFromUser)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
    }

    public Optional<UserDetails> loadUserByJwtToken(String jwtToken) {
       return (jwtTokenProvider.validateToken(jwtToken)) ?
               Optional.of(createUserDetailsFromJwtToken(jwtToken)) :
               Optional.empty();
    }

    private UserDetails createUserDetailsFromUser(User user) {
        return withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(ROLE + user.getRole().name())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private UserDetails createUserDetailsFromJwtToken(String jwtToken) {
        return withUsername(jwtTokenProvider.getUsername(jwtToken))
                .authorities(jwtTokenProvider.getRole(jwtToken))
                .password("")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}

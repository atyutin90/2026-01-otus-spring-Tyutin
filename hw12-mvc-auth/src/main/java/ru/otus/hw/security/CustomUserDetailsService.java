package ru.otus.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

import java.util.ArrayList;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: %s".formatted(username)));
        var roles = user.getRoles() != null ?
            user.getRoles().stream().map(Role::getName).toList() :
            new ArrayList<String>();

        return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .roles(roles.toArray(new String[0]))
            .password(user.getPassword())
            .authorities(emptyList())
            .build();
    }
}

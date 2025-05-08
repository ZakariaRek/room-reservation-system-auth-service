package com.reservation_system.authService.Services;

import com.reservation_system.authService.models.ERole;
import com.reservation_system.authService.models.Role;
import com.reservation_system.authService.models.User;
import com.reservation_system.authService.repository.RoleRepository;
import com.reservation_system.authService.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
     UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getUsers() {

        return userRepository.findAll();
    }
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));

    }
    @Transactional
    public User updateUser(Long id, String username, String email, String password, Set<String> strRoles) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));

        if (username != null && !username.isEmpty()) {
            user.setUsername(username);
        }

        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword( passwordEncoder.encode(password));
        }

        if (strRoles != null && !strRoles.isEmpty()) {
            Set<Role> roles = new HashSet<>();

            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });

            user.setRoles(roles);
        }

        return userRepository.save(user);
    }
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));
        userRepository.delete(user);
    }
}

package com.boots.service;

import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.repository.RoleRepository;
import com.boots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class SaveUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public boolean saveUser(User userFromForm) {
        if (emailExists(userFromForm.getEmail())) {
            System.out.println("There is an account with that email address:");
            return false;
        }
        User user = new User();
        Set<Role> roles = new HashSet<>();
        Role currentRole = roleRepository.findByName("ROLE_USER");
        roles.add(currentRole);
            user.setFirstName(userFromForm.getFirstName());
            user.setLastName(userFromForm.getLastName());
            user.setPassword(userFromForm.getPassword());
            user.setEmail(userFromForm.getEmail());
            user.setRoles(roles);
        userRepository.save(user);
        return true;
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

}



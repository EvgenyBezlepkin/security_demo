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
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username);
        if (user == null) {
            return null;
        }
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        System.out.println(user.getLastName());
        return new org.springframework.security.core.userdetails.User(
                        user.getLastName(),
                        bCryptPasswordEncoder.encode(user.getPassword()),
                        enabled,
                        accountNonExpired,
                        credentialsNonExpired,
                        accountNonLocked,
                        user.getRoles());
    }

    @Transactional
    public boolean saveUser(User userFromForm)
           // throws UserAlreadyExistException
    {
        if (emailExists(userFromForm.getEmail())) {
           // throw new UserAlreadyExistException
           //         (
            System.out.println("There is an account with that email address:");
               //             + userFromForm.getEmail());
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



package com.boots.service;

import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.entity.VerificationToken;
import com.boots.repository.RoleRepository;
import com.boots.repository.UserRepository;
import com.boots.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private VerificationTokenRepository tokenRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    //@Transactional
    public User saveUser(User userFromForm) {
        if (emailExists(userFromForm.getEmail())) {
            System.out.println("There is an account with that email address:");
            return null;
        }
        User user = new User();
        Set<Role> roles = new HashSet<>();
        Role currentRole = roleRepository.findByName("ROLE_USER");
        roles.add(currentRole);
            user.setFirstName(userFromForm.getFirstName());
            user.setLastName(userFromForm.getLastName());
            user.setPassword(bCryptPasswordEncoder.encode(userFromForm.getPassword()));
            user.setEmail(userFromForm.getEmail());
            user.setRoles(roles);
            userRepository.save(user);
        return user;
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }


    public User getUserByVerificationToken(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }


    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user, LocalDateTime.now());
        tokenRepository.save(myToken);
    }


}



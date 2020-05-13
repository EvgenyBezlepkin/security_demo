package com.boots.service;

import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    public MyAuthenticationProvider(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();
        User myUser = userRepository.findByEmail(userName);
        if (myUser == null) {
            throw new BadCredentialsException("Unknown user");
        }

        if (!bCryptPasswordEncoder.matches(password, myUser.getPassword())) {
            throw new BadCredentialsException("Bad password");
        }

        List<String> roles = myUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        UserDetails principal =  new org.springframework.security.core.userdetails.User
                (myUser.getLastName(),
                        bCryptPasswordEncoder.encode(password),
                        true, true,
                        true, true,
                        myUser.getRoles());

        return new UsernamePasswordAuthenticationToken(
                principal, password, principal.getAuthorities());
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
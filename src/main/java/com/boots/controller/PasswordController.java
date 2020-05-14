package com.boots.controller;

import com.boots.entity.Token;
import com.boots.entity.User;
import com.boots.event.EventType;
import com.boots.event.PasswordActionsEvent;
import com.boots.repository.TokenRepository;
import com.boots.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class PasswordController {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public PasswordController(UserRepository userRepository, ApplicationEventPublisher eventPublisher, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @GetMapping("forgot")
    public String getForgot(Model model) {
        model.addAttribute("oldUser", new User());
        return "forgot";
    }


    @PostMapping("forgot")
    public String postForgot(@ModelAttribute User userFromForm, HttpServletRequest request) {
        User user = userRepository.findByEmail(userFromForm.getEmail());
        if (user == null) {
            return "redirect:/";
        }
        eventPublisher.publishEvent(new PasswordActionsEvent(user, request.getLocale(), request.getContextPath(), EventType.FORGOT_PASSWORD));
        return "redirect:/";
    }


    @GetMapping("/forgotPassword")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        Token verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            model.addAttribute("message", "auth.message.invalidToken");
            return "redirect:/bad";
        }
        if (!(LocalDateTime.now().getMinute() - verificationToken.getExpiryDate().getMinute() <= (24 * 60))) {
            model.addAttribute("message", "auth.message.expired");
            return "redirect:/bad";
        }
        User user = verificationToken.getUser();
        tokenRepository.delete(verificationToken);
        model.addAttribute("user", user);
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String postChangePassword(@ModelAttribute User userFromForm){
        User user = userRepository.findByEmail(userFromForm.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userFromForm.getPassword()));
        userRepository.save(user);
        return "redirect:/";
    }

}

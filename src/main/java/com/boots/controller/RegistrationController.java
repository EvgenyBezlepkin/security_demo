package com.boots.controller;

import com.boots.entity.User;
import com.boots.entity.Token;
import com.boots.event.EventType;
import com.boots.event.PasswordActionsEvent;
import com.boots.repository.TokenRepository;
import com.boots.repository.UserRepository;
import com.boots.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private UserService service;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;

    public RegistrationController(UserService userService, ApplicationEventPublisher eventPublisher, UserService service, UserRepository userRepository, TokenRepository tokenRepository) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.service = service;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

//TODO:
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("newUser", new User());
        return "registration";
    }


    @PostMapping("/registration")
    public String addUser(@ModelAttribute("newUser") @Valid User userForm,
                          Model model,
                          HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

//        if (!userForm.getPassword().equals(userForm.getMatchingPassword())){
//            errors.add("Пароли не совпадают");
//        }
        User user = userService.saveUser(userForm);
        if (user == null) {
            errors.add("Пользователь с таким именем уже существует");
        }
        // на этом месте вызывается слушатель с добавочным кодом
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new PasswordActionsEvent(user, request.getLocale(), appUrl, EventType.REGISTRATION));
        model.addAttribute("errors", errors);
        if (errors.isEmpty()) {
            return "redirect:/";
        }
        return "bad";
    }

    // сюда приходит запрос с верификационного email
    @GetMapping("/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        Token verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            model.addAttribute("message", "auth.message.invalidToken");
            return "redirect:/badUser";
        }
        if (!(LocalDateTime.now().getMinute() - verificationToken.getExpiryDate().getMinute() <= (24 * 60))) {
            model.addAttribute("message", "auth.message.expired");
            return "redirect:/badUser";
        }
        tokenRepository.delete(verificationToken);
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/login";
    }
}

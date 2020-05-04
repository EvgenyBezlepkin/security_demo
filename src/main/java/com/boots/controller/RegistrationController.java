package com.boots.controller;

import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("newUser", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("newUser") @Valid User userForm, Model model) {

        List<String> errors = new ArrayList<>();

        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            errors.add("Пароли не совпадают");
            //model.addAttribute("error", "Пароли не совпадают");
            //return "registration";
        }
        if (!userService.saveUser(userForm)){
            errors.add("Пользователь с таким именем уже существует");
            //model.addAttribute("eError", "Пользователь с таким именем уже существует");
            //return "registration";
        }

        if (errors.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("errors", errors);
        return "registration";
    }
}

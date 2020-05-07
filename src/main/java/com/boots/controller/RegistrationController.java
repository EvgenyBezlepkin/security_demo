package com.boots.controller;

import com.boots.entity.User;
import com.boots.service.SaveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    private SaveUserService userService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("newUser", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("newUser") @Valid User userForm, Model model) {

        List<String> errors = new ArrayList<>();

        if (!userForm.getPassword().equals(userForm.getMatchingPassword())){
            errors.add("Пароли не совпадают");
        }
        if (!userService.saveUser(userForm)){
            errors.add("Пользователь с таким именем уже существует");
        }

        if (errors.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("errors", errors);
        return "registration";
    }
}

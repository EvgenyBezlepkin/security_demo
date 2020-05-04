package com.boots.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String getIndex(Model model) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("userName", SecurityContextHolder.getContext().getAuthentication().getName());
        return "index";
    }

}

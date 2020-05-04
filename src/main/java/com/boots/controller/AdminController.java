package com.boots.controller;

import com.boots.entity.Role;
import com.boots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collection;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/admin")
    public String userList(Model model) {
        model.addAttribute("allUsers", userRepository.findAll());
        return "admin";
    }

    @PostMapping("/admin")
    public String  deleteUser(@RequestParam Long userId,
                              @RequestParam String action) {
        if (action.equals("delete")){
            userRepository.deleteById(userId);
            //userService.deleteUser(userId);
        }
        return "redirect:/admin";
    }

//    @GetMapping("/admin/gt/{userId}")
//    public String  gtUser(@PathVariable("userId") Long userId, Model model) {
//        model.addAttribute("allUsers", userService.usergtList(userId));
//        return "admin";
//    }
}

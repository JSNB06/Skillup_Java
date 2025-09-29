package com.skillup.skillup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String index(Model model) {
        model.addAttribute("title", "SkillUp"); // equivalente al ['title'=>'SkillUp']
        return "home"; // busca en templates/home.html
    }
}

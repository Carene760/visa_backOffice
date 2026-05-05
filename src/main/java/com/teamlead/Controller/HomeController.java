package com.teamlead.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String afficherAccueil(Model model) {
        model.addAttribute("pageTitle", "Accueil - Application Visa");
        model.addAttribute("contentPage", "demande/accueil.jsp");
        return "layout";
    }
}

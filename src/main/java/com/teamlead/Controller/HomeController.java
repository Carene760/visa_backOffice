package com.teamlead.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToDemandeForm() {
        return "redirect:/demande/nouveau";
    }
}

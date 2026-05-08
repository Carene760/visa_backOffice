package com.teamlead.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

    @GetMapping("/")
    public String afficherAccueil(Model model) {
        model.addAttribute("pageTitle", "Accueil - Application Visa");
        model.addAttribute("contentPage", "demande/accueil.jsp");
        return "layout";
    }

    /**
     * GET /track/{numeroDemande}
     * Page publique de suivi d'une demande (via QR Code)
     * 
     * Ajoute le header 'ngrok-skip-browser-warning' pour bypass l'avertissement ngrok
     * quand le téléphone scanne le QR code via ngrok
     */
    @GetMapping("/track/{numeroDemande}")
    public String trackDemande(
        @PathVariable String numeroDemande, 
        Model model,
        HttpServletResponse response
    ) {
        // Ajouter le header pour bypasser l'avertissement ngrok
        response.addHeader("ngrok-skip-browser-warning", "69");
        
        model.addAttribute("numeroDemande", numeroDemande);
        return "track";
    }
}

package com.teamlead.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamlead.Model.*;
import com.teamlead.Service.*;
import java.util.List;

@Controller
@RequestMapping("/demande")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private NationaliteService nationaliteService;

    @Autowired
    private SituationMatrimoniaService situationMatrimoniaService;

    @Autowired
    private TypeMotifService typeMotifService;

    @Autowired
    private TypeDocumentService typeDocumentService;

    @Autowired
    private StatutDemandeService statutDemandeService;

    @Autowired
    private TypeDemandeService typeDemandeService;

    @GetMapping("/nouveau")
    public String afficherFormulaire(Model model) {
        // Charger les données de référence
        List<Nationalite> nationalites = nationaliteService.findAll();
        List<SituationMatrimoniale> situations = situationMatrimoniaService.findAll();
        List<TypeMotif> typeMotifs = typeMotifService.findAll();
        List<TypeDocument> documentsCommuns = typeDocumentService.findDocumentsCommuns();
        List<TypeDemande> typesDemande = typeDemandeService.findAll();

        model.addAttribute("nationalites", nationalites);
        model.addAttribute("situations", situations);
        model.addAttribute("typeMotifs", typeMotifs);
        model.addAttribute("documentsCommuns", documentsCommuns);
        model.addAttribute("typesDemande", typesDemande);
        model.addAttribute("demande", new Demande());

        return "demande/formulaire";
    }

    @PostMapping("/creer")
    public String creerDemande(Demande demande, Model model) {
        try {
            Demande nouvelleDemande = demandeService.creerDemande(demande);
            model.addAttribute("message", "Demande créée avec succès");
            model.addAttribute("demandeId", nouvelleDemande.getId());
            return "demande/confirmation";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            return "demande/formulaire";
        }
    }
}

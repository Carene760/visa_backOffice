package com.teamlead.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamlead.Model.*;
import com.teamlead.Service.*;
import java.util.List;

@Controller
@RequestMapping("/demande")
public class DemandeController {

    @Autowired
    private NationaliteService nationaliteService;

    @Autowired
    private SituationMatrimonialeService situationMatrimonialeService;

    @Autowired
    private TypeMotifService typeMotifService;

    @Autowired
    private TypeDocumentService typeDocumentService;

    @Autowired
    private TypeDemandeService typeDemandeService;

    @GetMapping("/nouveau")
    public String afficherFormulaire(Model model) {
        // Charger les données de référence
        List<Nationalite> nationalites = nationaliteService.findAll();
        List<SituationMatrimoniale> situations = situationMatrimonialeService.findAll();
        List<TypeMotif> typeMotifs = typeMotifService.findAll();
        List<TypeDocument> documentsCommuns = typeDocumentService.findDocumentsCommuns();
        List<TypeDocument> documentsSpecifiques = typeDocumentService.findDocumentsSpecifiques();
        List<TypeDemande> typesDemande = typeDemandeService.findAll();

        model.addAttribute("nationalites", nationalites);
        model.addAttribute("situations", situations);
        model.addAttribute("typeMotifs", typeMotifs);
        model.addAttribute("documentsCommuns", documentsCommuns);
        model.addAttribute("documentsSpecifiques", documentsSpecifiques);
        model.addAttribute("typesDemande", typesDemande);
        model.addAttribute("demande", new Demande());

        return "demande/formulaire";
    }
}

package com.teamlead.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamlead.Model.*;
import com.teamlead.Service.*;
import java.util.List;
import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;

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
  
    @Autowired
    private DemandeService demandeService;

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
  
    /**
     * Crée une nouvelle demande de visa transformable en long séjour
     * POST /demande/creer
     * 
     * Valide tous les champs obligatoires et documents obligatoires
     * Si OK → Enregistre en base avec statut "ENREGISTREE"
     * Si erreur → Retourne erreur détaillée
     */
    @PostMapping("/creer")
    public ResponseEntity<ValidationErrorDTO> creerDemande(@RequestBody DemandeCreationDTO demandeDTO) {
        try {
            ValidationErrorDTO resultat = demandeService.creerNouvelleDemande(demandeDTO);

            if (resultat.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultat);
            }

        } catch (Exception e) {
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, 
                "Erreur serveur lors de l'enregistrement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }
}

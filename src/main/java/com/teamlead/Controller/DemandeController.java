package com.teamlead.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamlead.Model.*;
import com.teamlead.Service.*;
import java.util.List;
import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import java.time.LocalDate;

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
        chargerDonneesReference(model);
        model.addAttribute("demande", new Demande());

        return "demande/formulaire";
    }
  
    /**
     * Crée une nouvelle demande de visa transformable en long séjour
     * POST /demande/creer
     * 
     * Valide tous les champs obligatoires et documents obligatoires
     * Si OK → Enregistre en base avec statut "DOSSIER_CREE"
     * Si erreur → Retourne erreur détaillée
     */
    @PostMapping("/creer")
    public String creerDemande(
            @RequestParam String demandeur_nom,
            @RequestParam(required = false) String demandeur_prenom,
            @RequestParam(required = false) String demandeur_nom_naissance,
            @RequestParam(required = false) String demandeur_date_naissance,
            @RequestParam(required = false) String demandeur_lieu_naissance,
            @RequestParam(required = false) Integer demandeur_nationalite,
            @RequestParam(required = false) Integer demandeur_situation,
            @RequestParam String demandeur_adresse,
            @RequestParam(required = false) String demandeur_email,
            @RequestParam String demandeur_telephone,
            @RequestParam String passeport_numero,
            @RequestParam(required = false) String passeport_date_delivrance,
            @RequestParam(required = false) String passeport_date_expiration,
            @RequestParam String visa_reference,
            @RequestParam(required = false) String visa_date_entree,
            @RequestParam(required = false) String visa_lieu_entree,
            @RequestParam(required = false) String visa_date_expiration,
            @RequestParam(required = false) Integer type_demande,
            @RequestParam(required = false) Integer type_motif,
            @RequestParam(required = false) String[] documents,
            Model model) {
        DemandeCreationDTO demandeDTO = new DemandeCreationDTO();

        try {
            // Construire le DTO à partir des paramètres du formulaire
            demandeDTO.setNom(demandeur_nom);
            demandeDTO.setPrenom(demandeur_prenom);
            demandeDTO.setNomNaissance(demandeur_nom_naissance);
            demandeDTO.setEmail(demandeur_email);
            demandeDTO.setTelephone(demandeur_telephone);
            demandeDTO.setIdNationalite(demandeur_nationalite);
            demandeDTO.setDateNaissance(parseDateOrNull(demandeur_date_naissance));
            demandeDTO.setLieuNaissance(demandeur_lieu_naissance);
            demandeDTO.setIdSituationMatrimoniale(demandeur_situation);
            demandeDTO.setAdresseMadagascar(demandeur_adresse);
            
            demandeDTO.setNumeroPasseport(passeport_numero);
            demandeDTO.setDateDelivrancePasseport(parseDateOrNull(passeport_date_delivrance));
            demandeDTO.setDateExpirationPasseport(parseDateOrNull(passeport_date_expiration));
            
            demandeDTO.setReferenceVisa(visa_reference);
            demandeDTO.setDateEntreeVisa(parseDateOrNull(visa_date_entree));
            demandeDTO.setLieuEntreeVisa(visa_lieu_entree);
            demandeDTO.setDateExpirationVisa(parseDateOrNull(visa_date_expiration));
            
            demandeDTO.setIdTypeDemande(type_demande);
            demandeDTO.setIdTypeMotif(type_motif);
            
            // Convertir les IDs de documents en liste
            if (documents != null && documents.length > 0) {
                java.util.List<Integer> pieceIds = new java.util.ArrayList<>();
                for (String docId : documents) {
                    pieceIds.add(Integer.parseInt(docId));
                }
                demandeDTO.setPiecesPresentes(pieceIds);
            }
            
            // Créer la demande
            ValidationErrorDTO resultat = demandeService.creerNouvelleDemande(demandeDTO);
            
            if (resultat.isSuccess()) {
                // Passer les infos de la demande créée à la page de confirmation
                model.addAttribute("demandeId", resultat.getDemandeId());
                model.addAttribute("createdDate", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return "demande/confirmation";
            } else {
                // Afficher les erreurs détaillées
                model.addAttribute("erreur", resultat.getMessage());
                model.addAttribute("erreurs", resultat.getErrors());
                
                // Passer le DTO avec les valeurs saisies pour les restaurer
                model.addAttribute("demandeDTO", demandeDTO);
                chargerDonneesReference(model);
                
                return "demande/formulaire";
            }
            
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur est survenue. Veuillez verifier les champs saisis puis reessayer.");
            model.addAttribute("demandeDTO", demandeDTO);
            chargerDonneesReference(model);
            return "demande/formulaire";
        }
    }

    private void chargerDonneesReference(Model model) {
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
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            return null;
        }
    }

    @GetMapping("/confirmation")
    public String afficherConfirmation() {
        return "demande/confirmation";
    }
}

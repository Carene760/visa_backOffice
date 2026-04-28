package com.teamlead.Controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.teamlead.DTO.*;
import com.teamlead.Exception.ValidationException;
import com.teamlead.Model.*;
import com.teamlead.Service.*;

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

    @PostMapping("/creer")
    public String creerDemande(
            @RequestParam String demandeur_nom,
            @RequestParam(required = false) String demandeur_prenom,
            @RequestParam(required = false) String demandeur_nom_naissance,
            @RequestParam String demandeur_date_naissance,
            @RequestParam(required = false) String demandeur_lieu_naissance,
            @RequestParam Integer demandeur_nationalite,
            @RequestParam(required = false) Integer demandeur_situation,
            @RequestParam String demandeur_adresse,
            @RequestParam(required = false) String demandeur_email,
            @RequestParam String demandeur_telephone,
            @RequestParam String passeport_numero,
            @RequestParam String passeport_date_delivrance,
            @RequestParam String passeport_date_expiration,
            @RequestParam String visa_reference,
            @RequestParam String visa_date_entree,
            @RequestParam(required = false) String visa_lieu_entree,
            @RequestParam String visa_date_expiration,
            @RequestParam Integer type_demande,
            @RequestParam Integer type_motif,
            @RequestParam(required = false) String[] documents,
            Model model) {
        DemandeCreationDTO dto = new DemandeCreationDTO();
        try {
            dto.setNom(demandeur_nom);
            dto.setPrenom(demandeur_prenom);
            dto.setNomNaissance(demandeur_nom_naissance);
            dto.setEmail(demandeur_email);
            dto.setTelephone(demandeur_telephone);
            dto.setIdNationalite(demandeur_nationalite);
            dto.setDateNaissance(parseDateOrNull(demandeur_date_naissance));
            dto.setLieuNaissance(demandeur_lieu_naissance);
            dto.setIdSituationMatrimoniale(demandeur_situation);
            dto.setAdresseMadagascar(demandeur_adresse);
            dto.setNumeroPasseport(passeport_numero);
            dto.setDateDelivrancePasseport(parseDateOrNull(passeport_date_delivrance));
            dto.setDateExpirationPasseport(parseDateOrNull(passeport_date_expiration));
            dto.setReferenceVisa(visa_reference);
            dto.setDateEntreeVisa(parseDateOrNull(visa_date_entree));
            dto.setLieuEntreeVisa(visa_lieu_entree);
            dto.setDateExpirationVisa(parseDateOrNull(visa_date_expiration));
            dto.setIdTypeDemande(type_demande);
            dto.setIdTypeMotif(type_motif);

            if (documents != null && documents.length > 0) {
                java.util.List<Integer> ids = new java.util.ArrayList<>();
                for (String d : documents)
                    ids.add(Integer.parseInt(d));
                dto.setPiecesPresentes(ids);
            }

            ValidationErrorDTO res = demandeService.creerNouvelleDemande(dto);

            if (res.isSuccess()) {
                model.addAttribute("demandeId", res.getDemandeId());
                model.addAttribute("createdDate", java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return "demande/confirmation";
            } else {
                model.addAttribute("erreur", res.getMessage());
                model.addAttribute("erreurs", res.getErrors());
                model.addAttribute("demandeDTO", dto);
                chargerDonneesReference(model);
                return "demande/formulaire";
            }

        } catch (ValidationException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("erreurs", e.getErrors());
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            return "demande/formulaire";
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur inattendue est survenue.");
            model.addAttribute("erreurs", java.util.List.of("Type: " + e.getClass().getSimpleName(),
                    "Détail: " + (e.getMessage() != null ? e.getMessage() : "(sans message)")));
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            return "demande/formulaire";
        }
    }

    private void chargerDonneesReference(Model model) {
        model.addAttribute("nationalites", nationaliteService.findAll());
        model.addAttribute("situations", situationMatrimonialeService.findAll());
        model.addAttribute("typeMotifs", typeMotifService.findAll());
        model.addAttribute("documentsCommuns", typeDocumentService.findDocumentsCommuns());
        model.addAttribute("documentsSpecifiques", typeDocumentService.findDocumentsSpecifiques());
        model.addAttribute("typesDemande", typeDemandeService.findAll());
    }

    private LocalDate parseDateOrNull(String v) {
        if (v == null || v.isBlank())
            return null;
        try {
            return LocalDate.parse(v);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/confirmation")
    public String afficherConfirmation() {
        return "demande/confirmation";
    }
}

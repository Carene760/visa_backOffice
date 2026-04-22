package com.teamlead.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Service.DemandeService;

@RestController
@RequestMapping("/api/demandes")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    /**
     * Crée une nouvelle demande de visa transformable en long séjour
     * POST /api/demandes/creer
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

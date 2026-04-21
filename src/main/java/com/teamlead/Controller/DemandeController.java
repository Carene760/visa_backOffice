package com.teamlead.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demande;
import com.teamlead.Service.DemandeService;
import com.teamlead.Service.DemandeStatusService;
import com.teamlead.Service.DemandeValidationService;

@RestController
@RequestMapping("/api/demandes")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private DemandeStatusService demandeStatusService;

    @Autowired
    private DemandeValidationService demandeValidationService;

    /**
     * Crée une nouvelle demande de visa transformable en long séjour
     * POST /api/demandes/creer
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
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }

    /**
     * Modifie une demande existante pour ajouter/modifier des documents
     * PUT /api/demandes/{demandeId}/modifier
     */
    @PutMapping("/{demandeId}/modifier")
    public ResponseEntity<ValidationErrorDTO> modifierDemande(
            @PathVariable Integer demandeId,
            @RequestBody DemandeCreationDTO demandeDTO) {
        try {
            ValidationErrorDTO resultat = demandeService.modifierDemande(demandeId, demandeDTO);

            if (resultat.isSuccess()) {
                return ResponseEntity.ok(resultat);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultat);
            }

        } catch (Exception e) {
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }

    /**
     * Récupère les détails d'une demande
     * GET /api/demandes/{demandeId}
     */
    @GetMapping("/{demandeId}")
    public ResponseEntity<?> getDetailDemande(@PathVariable Integer demandeId) {
        try {
            Demande demande = demandeService.getDemandeById(demandeId);

            if (demande == null) {
                ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Demande introuvable");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
            }

            return ResponseEntity.ok(demande);

        } catch (Exception e) {
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }

    /**
     * Valide une demande (vérifie les documents obligatoires)
     * POST /api/demandes/{demandeId}/valider
     */
    @PostMapping("/{demandeId}/valider")
    public ResponseEntity<ValidationErrorDTO> validerDemande(@PathVariable Integer demandeId) {
        try {
            ValidationErrorDTO resultat = demandeValidationService.validerDocumentsObligatoires(demandeId);

            if (resultat.isSuccess()) {
                return ResponseEntity.ok(resultat);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultat);
            }

        } catch (Exception e) {
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }

    /**
     * Change le statut de la demande
     * PUT /api/demandes/{demandeId}/changer-statut
     */
    @PutMapping("/{demandeId}/changer-statut")
    public ResponseEntity<ValidationErrorDTO> changerStatut(
            @PathVariable Integer demandeId,
            @RequestBody Map<String, String> body) {
        try {
            String nouveauStatut = body.get("statut");

            if (!demandeService.peutPasserAuStatutSuivant(demandeId)) {
                ValidationErrorDTO erreur = new ValidationErrorDTO(false,
                        "Impossible de changer le statut: documents obligatoires manquants");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
            }

            boolean success = demandeStatusService.changerStatut(demandeId, nouveauStatut);

            ValidationErrorDTO resultat = new ValidationErrorDTO(success,
                    success ? "Statut changé avec succès" : "Erreur lors du changement de statut");

            if (success) {
                return ResponseEntity.ok(resultat);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultat);
            }

        } catch (Exception e) {
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }

    /**
     * Récupère les documents manquants obligatoires
     * GET /api/demandes/{demandeId}/documents-manquants
     */
    @GetMapping("/{demandeId}/documents-manquants")
    public ResponseEntity<?> getDocumentsManquants(@PathVariable Integer demandeId) {
        try {
            List<String> documentsManquants = demandeStatusService.getDocumentsManquants(demandeId);
            return ResponseEntity.ok(documentsManquants);

        } catch (Exception e) {
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }

    /**
     * Récupère le statut courant de la demande
     * GET /api/demandes/{demandeId}/statut
     */
    @GetMapping("/{demandeId}/statut")
    public ResponseEntity<?> getStatutDemande(@PathVariable Integer demandeId) {
        try {
            return ResponseEntity.ok(demandeStatusService.getStatutCourant(demandeId));

        } catch (Exception e) {
            ValidationErrorDTO erreur = new ValidationErrorDTO(false, "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
        }
    }
}

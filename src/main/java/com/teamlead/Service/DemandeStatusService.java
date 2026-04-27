package com.teamlead.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demande;
import com.teamlead.Model.HistoriqueStatutDemande;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Repository.HistoriqueStatutDemandeRepository;
import com.teamlead.Repository.StatutDemandeRepository;

@Service
public class DemandeStatusService {

    @Autowired
    private StatutDemandeRepository statutDemandeRepository;

    @Autowired
    private HistoriqueStatutDemandeRepository historiqueStatutDemandeRepository;

    /**
     * Initialise une demande avec le statut "ENREGISTREE"
     * 
     * Le statut doit être créé dans init.sql par DEV1
     * Si le statut n'existe pas en base → Retourne une erreur
     */
    public ValidationErrorDTO initializeDemandeStatus(Demande demande) {
        ValidationErrorDTO result = new ValidationErrorDTO(true, "Statut initialisé avec succès");
        
        // Chercher le statut "DOSSIER_CREE" en base
        StatutDemande statutEnregistree = statutDemandeRepository.findByLibelle("DOSSIER_CREE");

        if (statutEnregistree == null) {
            result.setSuccess(false);
            result.setMessage("Erreur de configuration du système");
            result.addError("Le statut 'DOSSIER_CREE' n'existe pas en base de données. Contactez l'administrateur du système.");
            return result;
        }

        try {
            // Enregistrer dans l'historique
            HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
            historique.setDemande(demande);
            historique.setStatut(statutEnregistree);
            historique.setDateChangement(LocalDateTime.now());
            historiqueStatutDemandeRepository.save(historique);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Erreur lors de l'enregistrement du statut");
            result.addError("Impossible d'enregistrer le changement de statut: " + e.getMessage());
        }
        
        return result;
    }
}


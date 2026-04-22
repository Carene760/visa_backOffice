package com.teamlead.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Si le statut n'existe pas en base → Lance une exception (configuration incorrecte)
     */
    public void initializeDemandeStatus(Demande demande) {
        // Chercher le statut "ENREGISTREE" en base
        StatutDemande statutEnregistree = statutDemandeRepository.findByLibelle("ENREGISTREE");

        if (statutEnregistree == null) {
            throw new RuntimeException(
                "Erreur de configuration: le statut 'ENREGISTREE' n'existe pas en base. "
                + "Assurez-vous que init.sql a été exécuté correctement par DEV1.");
        }

        // Enregistrer dans l'historique
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(statutEnregistree);
        historique.setDateChangement(LocalDateTime.now());
        historiqueStatutDemandeRepository.save(historique);
    }
}


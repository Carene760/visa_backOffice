package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.HistoriqueStatutDemande;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Model.TypeDocument;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.HistoriqueStatutDemandeRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.StatutDemandeRepository;

@Service
public class DemandeStatusService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private StatutDemandeRepository statutDemandeRepository;

    @Autowired
    private HistoriqueStatutDemandeRepository historiqueStatutDemandeRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    /**
     * Initialise une demande avec le statut 'dossier cree'
     */
    public StatutDemande initializeDemandeStatus(Demande demande) {
        StatutDemande statutDossierCree = statutDemandeRepository.findByLibelle("dossier cree");

        if (statutDossierCree == null) {
            // Créer le statut s'il n'existe pas
            statutDossierCree = new StatutDemande();
            statutDossierCree.setLibelle("dossier cree");
            statutDossierCree = statutDemandeRepository.save(statutDossierCree);
        }

        // Enregistrer dans l'historique
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(statutDossierCree);
        historique.setDateChangement(LocalDateTime.now());
        historiqueStatutDemandeRepository.save(historique);

        return statutDossierCree;
    }

    /**
     * Autorise le changement de statut uniquement si tous les documents obligatoires sont présents
     */
    public boolean peutChangerStatut(Integer demandeId) {
        List<PieceAFournir> piecesObligatoires = pieceAFournirRepository
                .findByDemandeIdAndTypeDocumentObligatoireTrue(demandeId);

        for (PieceAFournir piece : piecesObligatoires) {
            if (!piece.getPresent()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Change le statut de la demande si tous les documents obligatoires sont présents
     */
    public boolean changerStatut(Integer demandeId, String nouveauStatutLibelle) {
        if (!peutChangerStatut(demandeId)) {
            return false;
        }

        Demande demande = demandeRepository.findById(demandeId).orElse(null);
        if (demande == null) {
            return false;
        }

        StatutDemande nouveauStatut = statutDemandeRepository.findByLibelle(nouveauStatutLibelle);
        if (nouveauStatut == null) {
            return false;
        }

        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(nouveauStatut);
        historique.setDateChangement(LocalDateTime.now());
        historiqueStatutDemandeRepository.save(historique);

        return true;
    }

    /**
     * Récupère le statut courant de la demande
     */
    public StatutDemande getStatutCourant(Integer demandeId) {
        HistoriqueStatutDemande dernier = historiqueStatutDemandeRepository.findAll().stream()
                .filter(h -> h.getDemande().getId().equals(demandeId))
                .sorted((h1, h2) -> h2.getDateChangement().compareTo(h1.getDateChangement()))
                .findFirst()
                .orElse(null);

        return dernier != null ? dernier.getStatut() : null;
    }

    /**
     * Vérifie si le dossier est complet (tous les documents obligatoires sont présents)
     */
    public boolean isDossierComplet(Integer demandeId) {
        List<PieceAFournir> piecesObligatoires = pieceAFournirRepository
                .findByDemandeIdAndTypeDocumentObligatoireTrue(demandeId);

        if (piecesObligatoires.isEmpty()) {
            return true;
        }

        for (PieceAFournir piece : piecesObligatoires) {
            if (!piece.getPresent()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Obtient les documents manquants obligatoires
     */
    public List<String> getDocumentsManquants(Integer demandeId) {
        List<String> documentsManquants = new ArrayList<>();
        List<PieceAFournir> piecesObligatoires = pieceAFournirRepository
                .findByDemandeIdAndTypeDocumentObligatoireTrue(demandeId);

        for (PieceAFournir piece : piecesObligatoires) {
            if (!piece.getPresent()) {
                documentsManquants.add(piece.getTypeDocument().getLibelle());
            }
        }

        return documentsManquants;
    }
}

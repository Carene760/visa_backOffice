package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Exception.ValidationException;
import com.teamlead.Model.*;
import com.teamlead.Repository.*;

@Service
public class DemandeStatusService {
    
    @Autowired
    private StatutDemandeRepository statutDemandeRepository;
    
    @Autowired
    private HistoriqueStatutDemandeRepository historiqueStatutDemandeRepository;
    
    @Autowired
    private JournalActiviteRepository journalActiviteRepository;
    
    @Autowired
    private DemandeRepository demandeRepository;
    
    @Autowired
    private DocumentScanValidationService documentScanValidationService;
    
    @Autowired
    private TypeEvenementRepository typeEvenementRepository;

    public void initializeDemandeStatus(Demande demande) {
        StatutDemande statut = statutDemandeRepository.findByLibelle("DOSSIER_CREE");
        if (statut == null)
            throw new ValidationException("Erreur de configuration du système",
                    List.of("Le statut 'DOSSIER_CREE' n'existe pas en base de données."));
        HistoriqueStatutDemande h = new HistoriqueStatutDemande();
        h.setDemande(demande);
        h.setStatut(statut);
        h.setDateChangement(LocalDateTime.now());
        historiqueStatutDemandeRepository.save(h);
    }

    /**
     * Transition d'une demande de DOSSIER_CREE à SCAN_TERMINE
     * Validation: Tous les documents obligatoires doivent avoir au moins 1 scan
     */
    @Transactional
    public ValidationErrorDTO transitionnerVersScanTermine(Integer idDemande) {
        try {
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande non trouvée",
                            List.of("La demande " + idDemande + " n'existe pas")));

            // Vérifier le statut actuel
            StatutDemande statutActuel = demande.getStatutDemande();
            if (statutActuel == null || !statutActuel.getLibelle().equals("DOSSIER_CREE")) {
                return new ValidationErrorDTO(false,
                        "Erreur: Le statut actuel n'est pas DOSSIER_CREE. Transition impossible.");
            }

            // Valider que tous les obligatoires sont scannés
            String validationError = documentScanValidationService.validerAvantTransitionScanTermine(idDemande);
            if (!validationError.isEmpty()) {
                return new ValidationErrorDTO(false, validationError);
            }

            // Charger le nouveau statut
            StatutDemande statutScanTermine = statutDemandeRepository.findByLibelle("SCAN_TERMINE");
            if (statutScanTermine == null) {
                statutScanTermine = new StatutDemande();
                statutScanTermine.setLibelle("SCAN_TERMINE");
                statutDemandeRepository.save(statutScanTermine);
            }

            // Mettre à jour le statut
            demande.setStatutDemande(statutScanTermine);
            demande.setDateModification(LocalDateTime.now());
            demandeRepository.save(demande);

            // Créer l'historique
            HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
            historique.setDemande(demande);
            historique.setStatut(statutScanTermine);
            historique.setDateChangement(LocalDateTime.now());
            historiqueStatutDemandeRepository.save(historique);

            // Créer un événement d'audit
            TypeEvenement typeEvenement = typeEvenementRepository.findByCode("SCAN_TERMINE");
            if (typeEvenement != null) {
                JournalActivite journal = new JournalActivite();
                journal.setDemandeur(demande.getDemandeur());
                journal.setTypeEvenement(typeEvenement);
                journal.setDateAction(LocalDateTime.now());
                journalActiviteRepository.save(journal);
            }

            ValidationErrorDTO result = new ValidationErrorDTO(true, "Transition vers SCAN_TERMINE réussie");
            result.setDemandeId(idDemande);
            return result;

        } catch (ValidationException e) {
            return new ValidationErrorDTO(false, e.getMessage());
        } catch (Exception e) {
            return new ValidationErrorDTO(false, "Erreur inattendue: " + e.getMessage());
        }
    }
}

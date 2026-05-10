package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Exception.ValidationException;
import com.teamlead.Model.Demande;
import com.teamlead.Model.HistoriqueStatutDemande;
import com.teamlead.Model.JournalActivite;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Model.TypeEvenement;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.HistoriqueStatutDemandeRepository;
import com.teamlead.Repository.JournalActiviteRepository;
import com.teamlead.Repository.StatutDemandeRepository;
import com.teamlead.Repository.TypeEvenementRepository;

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
        initializeDemandeStatus(demande, false);
    }

    public void initializeDemandeStatus(Demande demande, boolean sansDonneesAnterieures) {
        StatutDemande statut = statutDemandeRepository.findByLibelle("DOSSIER_CREE");
        if (statut == null)
            throw new ValidationException("Erreur de configuration du système",
                    List.of("Le statut 'DOSSIER_CREE' n'existe pas en base de données."));
        HistoriqueStatutDemande h = new HistoriqueStatutDemande();
        h.setDemande(demande);
        h.setStatut(statut);
        h.setDateChangement(LocalDateTime.now());
        historiqueStatutDemandeRepository.save(h);

        demande.setStatutDemande(statut);
        demande.setDateModification(LocalDateTime.now());
        demandeRepository.save(demande);
    }

    public void enregistrerJournalActivite(Demande demande, String codeEvenement) {
        if (demande == null || demande.getDemandeur() == null || codeEvenement == null || codeEvenement.isBlank()) {
            return;
        }

        TypeEvenement typeEvenement = typeEvenementRepository.findByCode(codeEvenement);
        if (typeEvenement == null) {
            typeEvenement = new TypeEvenement();
            typeEvenement.setCode(codeEvenement);
            typeEvenement = typeEvenementRepository.save(typeEvenement);
        }

        JournalActivite journal = new JournalActivite();
        journal.setDemandeur(demande.getDemandeur());
        journal.setTypeEvenement(typeEvenement);
        journal.setDateAction(LocalDateTime.now());
        journalActiviteRepository.save(journal);
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
            if (statutActuel != null && "SCAN_TERMINE".equalsIgnoreCase(statutActuel.getLibelle())) {
                return new ValidationErrorDTO(true, "La demande est déjà au statut SCAN_TERMINE");
            }
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

    /**
     * Transition d'une demande vers PHOTO_SIGNATURE_TERMINE
     * Validation: photo + signature doivent être terminées
     */
    @Transactional
    public ValidationErrorDTO transitionnerVersPhotoSignatureTermine(Integer idDemande) {
        try {
            if (idDemande == null) {
                return new ValidationErrorDTO(false, "Identifiant de demande manquant");
            }
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande non trouvée",
                            List.of("La demande " + idDemande + " n'existe pas")));

            if (demande.getDemandeur() == null) {
                return new ValidationErrorDTO(false, "Demandeur introuvable pour cette demande");
            }

            boolean photoTerminee = Boolean.TRUE.equals(demande.getDemandeur().getPhotoTerminee());
            boolean signatureTerminee = Boolean.TRUE.equals(demande.getDemandeur().getSignatureTerminee());
            if (!photoTerminee || !signatureTerminee) {
                return new ValidationErrorDTO(false,
                        "Transition impossible: photo et signature doivent être terminées.");
            }

            StatutDemande statutActuel = demande.getStatutDemande();
            if (statutActuel != null && "PHOTO_SIGNATURE_TERMINE".equalsIgnoreCase(statutActuel.getLibelle())) {
                ValidationErrorDTO alreadyDone = new ValidationErrorDTO(true,
                        "La demande est déjà au statut PHOTO_SIGNATURE_TERMINE");
                alreadyDone.setDemandeId(idDemande);
                return alreadyDone;
            }

            StatutDemande statutPhotoSignature = statutDemandeRepository.findByLibelle("PHOTO_SIGNATURE_TERMINE");
            if (statutPhotoSignature == null) {
                statutPhotoSignature = new StatutDemande();
                statutPhotoSignature.setLibelle("PHOTO_SIGNATURE_TERMINE");
                statutPhotoSignature = statutDemandeRepository.save(statutPhotoSignature);
            }

            demande.setStatutDemande(statutPhotoSignature);
            demande.setDateModification(LocalDateTime.now());
            demandeRepository.save(demande);

            HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
            historique.setDemande(demande);
            historique.setStatut(statutPhotoSignature);
            historique.setDateChangement(LocalDateTime.now());
            historiqueStatutDemandeRepository.save(historique);

            enregistrerJournalActivite(demande, "PHOTO_SIGNATURE_TERMINE");

            ValidationErrorDTO result = new ValidationErrorDTO(true,
                    "Transition vers PHOTO_SIGNATURE_TERMINE réussie");
            result.setDemandeId(idDemande);
            return result;
        } catch (ValidationException e) {
            return new ValidationErrorDTO(false, e.getMessage());
        } catch (Exception e) {
            return new ValidationErrorDTO(false, "Erreur inattendue: " + e.getMessage());
        }
    }
}

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
import com.teamlead.Repository.DocumentSignatureRepository;
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
    private DocumentSignatureRepository documentSignatureRepository;
    
    @Autowired
    private TypeEvenementRepository typeEvenementRepository;

    public void initializeDemandeStatus(Demande demande) {
        initializeDemandeStatus(demande, false);
    }

    public void initializeDemandeStatus(Demande demande, boolean sansDonneesAnterieures) {
        StatutDemande statut = obtenirOuCreerStatut("DOSSIER_CREE");
        HistoriqueStatutDemande h = new HistoriqueStatutDemande();
        h.setDemande(demande);
        h.setStatut(statut);
        h.setDateChangement(LocalDateTime.now());
        historiqueStatutDemandeRepository.save(h);

        demande.setStatutDemande(statut);
        demande.setDateModification(LocalDateTime.now());
        demandeRepository.save(demande);
    }

    @Transactional
    public ValidationErrorDTO transitionnerVersPhotoSignatureTerminee(Integer idDemande) {
        try {
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande non trouvée",
                            List.of("La demande " + idDemande + " n'existe pas")));

            StatutDemande statutActuel = demande.getStatutDemande();
            if (statutActuel != null && "PHOTO_SIGNATURE_TERMINE".equalsIgnoreCase(statutActuel.getLibelle())) {
                return new ValidationErrorDTO(true, "La demande est déjà au statut PHOTO_SIGNATURE_TERMINE");
            }
            if (statutActuel == null || !"DOSSIER_CREE".equalsIgnoreCase(statutActuel.getLibelle())) {
                return new ValidationErrorDTO(false,
                        "Erreur: Le statut actuel n'est pas DOSSIER_CREE. Transition impossible.");
            }

            StatutDemande statutCible = obtenirOuCreerStatut("PHOTO_SIGNATURE_TERMINE");
            demande.setStatutDemande(statutCible);
            demande.setDateModification(LocalDateTime.now());
            demandeRepository.save(demande);

            HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
            historique.setDemande(demande);
            historique.setStatut(statutCible);
            historique.setDateChangement(LocalDateTime.now());
            historiqueStatutDemandeRepository.save(historique);

            enregistrerJournalActivite(demande, "PHOTO_SIGNATURE_TERMINE");

            ValidationErrorDTO result = new ValidationErrorDTO(true, "Transition vers PHOTO_SIGNATURE_TERMINE réussie");
            result.setDemandeId(idDemande);
            return result;
        } catch (ValidationException e) {
            return new ValidationErrorDTO(false, e.getMessage());
        } catch (Exception e) {
            return new ValidationErrorDTO(false, "Erreur inattendue: " + e.getMessage());
        }
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
     * Transition d'une demande vers SCAN_TERMINE
     * Accepte l'appel de n'importe quel statut (DOSSIER_CREE ou PHOTO_SIGNATURE_TERMINE)
     * Validation: Photos + signatures DOIVENT exister + Tous les documents obligatoires scannés
     * Permet l'ordre flexible: upload d'abord OU photo/signature d'abord, peu importe l'ordre
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
            if (statutActuel == null || (!("DOSSIER_CREE".equalsIgnoreCase(statutActuel.getLibelle()) 
                    || "PHOTO_SIGNATURE_TERMINE".equalsIgnoreCase(statutActuel.getLibelle())))) {
                return new ValidationErrorDTO(false,
                        "Erreur: Le statut actuel ne permet pas la transition vers SCAN_TERMINE.");
            }

            // Vérifier photos et signatures - messages spécifiques
            StringBuilder erreurs = new StringBuilder();
            
            if (!documentSignatureRepository.existsPhotoWebcam(demande.getId())) {
                erreurs.append("Photo manquante. ");
            }
            if (!documentSignatureRepository.existsSignatureSouris(demande.getId())) {
                erreurs.append("Signature manquante. ");
            }
            
            if (erreurs.length() > 0) {
                return new ValidationErrorDTO(false, "Erreur: " + erreurs.toString().trim());
            }

            // Valider les uploads obligatoires SEULEMENT si c'est "AVEC données antérieures"
            // Logique: 
            // - Si sansDonneesAnterieures = TRUE -> pas de vérif uploads (optionnels)
            // - Si sansDonneesAnterieures = FALSE/NULL -> vérif uploads (obligatoires)
            boolean isSansDonneesAnterieures = Boolean.TRUE.equals(demande.getSansDonneesAnterieures());
            
            if (!isSansDonneesAnterieures) {
                // AVEC données antérieures -> uploads obligatoires
                String validationError = documentScanValidationService.validerAvantTransitionScanTermine(idDemande);
                if (!validationError.isEmpty()) {
                    return new ValidationErrorDTO(false, validationError);
                }
            }

            // Charger le nouveau statut
            StatutDemande statutScanTermine = obtenirOuCreerStatut("SCAN_TERMINE");

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

    @Transactional
    public ValidationErrorDTO reinitialiserPourModification(Integer idDemande, String etape) {
        try {
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande non trouvée",
                            List.of("La demande " + idDemande + " n'existe pas")));

            String etapeNormalisee = etape != null ? etape.trim().toLowerCase() : "";
            if (etapeNormalisee.isBlank()) {
                return new ValidationErrorDTO(false, "L'étape de reprise est obligatoire.");
            }

            StatutDemande statutCible;
            if ("photo-signature".equals(etapeNormalisee)) {
                statutCible = obtenirOuCreerStatut("DOSSIER_CREE");
                // Supprimer les documents photo/signature de la table document_signature
                documentSignatureRepository.deleteByDemandeIdAndTypeDocument(demande.getId(), "PHOTO_WEBCAM");
                documentSignatureRepository.deleteByDemandeIdAndTypeDocument(demande.getId(), "SIGNATURE_SOURIS");
            } else if ("upload".equals(etapeNormalisee)) {
                statutCible = obtenirOuCreerStatut("PHOTO_SIGNATURE_TERMINE");
            } else {
                return new ValidationErrorDTO(false, "Étape de reprise inconnue: " + etape);
            }

            demande.setStatutDemande(statutCible);
            demande.setDateModification(LocalDateTime.now());
            demandeRepository.save(demande);

            HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
            historique.setDemande(demande);
            historique.setStatut(statutCible);
            historique.setDateChangement(LocalDateTime.now());
            historiqueStatutDemandeRepository.save(historique);

            enregistrerJournalActivite(demande, "REPRISE_" + etapeNormalisee.toUpperCase());

            ValidationErrorDTO result = new ValidationErrorDTO(true, "Dossier réinitialisé pour l'étape: " + etapeNormalisee);
            result.setDemandeId(idDemande);
            return result;
        } catch (ValidationException e) {
            return new ValidationErrorDTO(false, e.getMessage());
        } catch (Exception e) {
            return new ValidationErrorDTO(false, "Erreur inattendue: " + e.getMessage());
        }
    }

    private StatutDemande obtenirOuCreerStatut(String libelle) {
        StatutDemande statut = statutDemandeRepository.findByLibelle(libelle);
        if (statut != null) {
            return statut;
        }

        StatutDemande nouveauStatut = new StatutDemande();
        nouveauStatut.setLibelle(libelle);
        return statutDemandeRepository.save(nouveauStatut);
    }
}

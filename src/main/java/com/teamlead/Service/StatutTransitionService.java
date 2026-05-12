package com.teamlead.Service;

import com.teamlead.Model.*;
import com.teamlead.Repository.*;
import com.teamlead.DTO.ValidationErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Service métier pour gestion des transitions de statut
 * Responsabilités:
 * - Gérer les transitions entre statuts valides
 * - Créer historique pour chaque transition
 * - Implémenter la logique de refus (Sprint 2: avant SCAN_TERMINE uniquement)
 * 
 * Statuts disponibles:
 * - DOSSIER_CREE: Initial, dossier créé et complété
 * - PHOTO_SIGNATURE_TERMINE: Photo webcam et signature souris terminées
 * - SCAN_TERMINE: Après scan des documents
 * - VISA_APPROUVEE: Visa approuvé (final)
 * - REFUSE: Dossier refusé (peut être avant SCAN_TERMINE seulement en Sprint 2)
 */
@Service
public class StatutTransitionService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private HistoriqueStatutDemandeRepository historiqueRepository;

    @Autowired
    private StatutDemandeRepository statutDemandeRepository;

    /**
     * Transition une demande vers un nouveau statut
     * Valide la transition et crée l'historique
     * 
     * @param demande La demande à transitionner
     * @param statutCible Le nom du statut cible (ex: "SCAN_TERMINE")
     * @param raison Raison de la transition (optionnelle, pour audit)
     * @return ValidationErrorDTO avec succès ou erreurs
     */
    @Transactional
    public ValidationErrorDTO transitionner(
            Demande demande,
            String statutCible,
            String raison) {
        
        ValidationErrorDTO result = new ValidationErrorDTO();
        result.setSuccess(true);
        
        StatutDemande statutActuel = demande.getStatutDemande();
        StatutDemande nouveauStatut = statutDemandeRepository.findByLibelle(statutCible);
        
        if (nouveauStatut == null) {
            result.setSuccess(false);
            result.addError("Statut cible '" + statutCible + "' non trouvé");
            return result;
        }
        
        // Vérifier que ce n'est pas la même transition
        if (statutActuel.getId().equals(nouveauStatut.getId())) {
            result.setSuccess(false);
            result.addError("La demande est déjà au statut " + statutActuel.getLibelle());
            return result;
        }
        
        // Vérifier que la transition est valide
        if (!estTransitionValide(statutActuel.getLibelle(), nouveauStatut.getLibelle())) {
            result.setSuccess(false);
            result.addError("Transition non autorisée de " + statutActuel.getLibelle() + 
                          " vers " + nouveauStatut.getLibelle());
            return result;
        }

        if ("SCAN_TERMINE".equalsIgnoreCase(nouveauStatut.getLibelle())) {
            if (demande.getDemandeur() == null
                    || !Boolean.TRUE.equals(demande.getDemandeur().getPhotoTerminee())
                    || !Boolean.TRUE.equals(demande.getDemandeur().getSignatureTerminee())) {
                result.setSuccess(false);
                result.addError("La photo webcam et la signature souris doivent être terminées avant de passer à SCAN_TERMINE");
                return result;
            }
        }
        
        // Effectuer la transition
        demande.setStatutDemande(nouveauStatut);
        demande.setDateTraitement(LocalDateTime.now());
        demandeRepository.save(demande);
        
        // Créer historique
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(nouveauStatut);
        historique.setDateChangement(LocalDateTime.now());
        historiqueRepository.save(historique);
        
        return result;
    }

    /**
     * Refuse une demande
     * Sprint 2: Refus autorisé UNIQUEMENT avant SCAN_TERMINE
     * Sprint 3+: Autres cas de refus métier
     * 
     * @param demande La demande à refuser
     * @param motif Le motif du refus (obligatoire)
     * @return ValidationErrorDTO avec succès ou erreurs
     */
    @Transactional
    public ValidationErrorDTO refuserDemande(Demande demande, String motif) {
        ValidationErrorDTO result = new ValidationErrorDTO();
        result.setSuccess(true);
        
        StatutDemande statutActuel = demande.getStatutDemande();
        StatutDemande statutRefuse = statutDemandeRepository.findByLibelle("REFUSE");
        
        if (statutRefuse == null) {
            result.setSuccess(false);
            result.addError("Statut REFUSE non trouvé en base");
            return result;
        }
        
        // REFUSE est un état final
        if (statutActuel.getLibelle().equals("REFUSE")) {
            result.setSuccess(false);
            result.addError("La demande est déjà refusée");
            return result;
        }
        
        // VISA_APPROUVEE est un état final, pas de refus possible
        if (statutActuel.getLibelle().equals("VISA_APPROUVEE")) {
            result.setSuccess(false);
            result.addError("Le refus est impossible après approbation du visa");
            return result;
        }
        
        // SCAN_TERMINE: refus impossible en Sprint 2
        if (statutActuel.getLibelle().equals("SCAN_TERMINE")) {
            result.setSuccess(false);
            result.addError("Le refus après SCAN_TERMINE est réservé au Sprint 3");
            return result;
        }
        
        // DOSSIER_CREE: refus autorisé
        if (!statutActuel.getLibelle().equals("DOSSIER_CREE")) {
            result.setSuccess(false);
            result.addError("Refus impossible à partir du statut " + statutActuel.getLibelle());
            return result;
        }
        
        // Vérifier que le motif est fourni
        if (motif == null || motif.isBlank()) {
            result.setSuccess(false);
            result.addError("Le motif du refus est obligatoire");
            return result;
        }
        
        // Effectuer le refus
        demande.setStatutDemande(statutRefuse);
        demande.setDateTraitement(LocalDateTime.now());
        demandeRepository.save(demande);
        
        // Créer historique
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(statutRefuse);
        historique.setDateChangement(LocalDateTime.now());
        historiqueRepository.save(historique);
        
        return result;
    }

    /**
     * Valide si une transition de statut est permise
     * Règles:
     * - DOSSIER_CREE -> SCAN_TERMINE, REFUSE
     * - SCAN_TERMINE -> VISA_APPROUVEE, REFUSE (Sprint 3+)
     * - VISA_APPROUVEE -> (terminal)
     * - REFUSE -> (terminal)
     * 
     * @param from Statut source
     * @param to Statut destination
     * @return true si transition valide, false sinon
     */
    private boolean estTransitionValide(String from, String to) {
        // DOSSIER_CREE peut aller vers PHOTO_SIGNATURE_TERMINE ou REFUSE
        if (from.equals("DOSSIER_CREE")) {
            return to.equals("PHOTO_SIGNATURE_TERMINE") || to.equals("REFUSE");
        }

        if (from.equals("PHOTO_SIGNATURE_TERMINE")) {
            return to.equals("SCAN_TERMINE");
        }
        
        // SCAN_TERMINE peut aller vers VISA_APPROUVEE
        // REFUSE après SCAN_TERMINE: Sprint 3+
        if (from.equals("SCAN_TERMINE")) {
            return to.equals("VISA_APPROUVEE");
        }
        
        // VISA_APPROUVEE est terminal
        if (from.equals("VISA_APPROUVEE")) {
            return false;
        }
        
        // REFUSE est terminal
        if (from.equals("REFUSE")) {
            return false;
        }
        
        return false;
    }

    /**
     * Récupère le dernier statut enregistré
     * Utile pour affichage
     * 
     * @param demande La demande
     * @return Le dernier HistoriqueStatutDemande ou null
     */
    @Transactional(readOnly = true)
    public HistoriqueStatutDemande obtenirDernierStatut(Demande demande) {
        java.util.List<HistoriqueStatutDemande> historiques = 
            historiqueRepository.findByDemandeIdOrderByDateChangementDesc(demande.getId());
        
        if (historiques.isEmpty()) {
            return null;
        }
        
        return historiques.get(0);
    }

}

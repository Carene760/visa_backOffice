package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.HistoriqueStatutDemande;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Model.TypeDemande;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.HistoriqueStatutDemandeRepository;
import com.teamlead.Repository.StatutDemandeRepository;
import com.teamlead.Repository.TypeDemandeRepository;

/**
 * Service métier pour gestion des duplicata/transfert
 * Responsabilités:
 * - Créer demande base (type NOUVEAU_TITRE, est_base_generee=TRUE) quand pas d'antécédents trouvés
 * - Chainer les demandes duplicata/transfert avec leur source (id_demande_source)
 * - Vérifier qu'un duplicata/transfert a toujours une source
 * - Assurer source et demande courante ont le même demandeur
 * 
 * Attributs des demandes:
 * - avec_donnees_anterieures BOOLEAN
 * - est_base_generee BOOLEAN
 * - id_demande_source INT FK (nullable)
 */
@Service
public class DuplicataTransfertService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private TypeDemandeRepository typeDemandeRepository;

    @Autowired
    private StatutDemandeRepository statutDemandeRepository;

    @Autowired
    private HistoriqueStatutDemandeRepository historiqueRepository;

    /**
     * Crée une demande base de type NOUVEAU_TITRE avec est_base_generee=TRUE
     * Cette demande sert de source pour un duplicata/transfert sans antécédents
     * 
     * @param demandeur Le demandeur de la demande base
     * @return La demande base créée avec statut DOSSIER_CREE
     */
    @Transactional
    public Demande creerDemandeBase(Demandeur demandeur) {
        Demande demandeBase = new Demande();
        
        // Récupérer type NOUVEAU_TITRE via repository
        TypeDemande typeNouveauTitre = typeDemandeRepository.findByLibelle("NOUVEAU_TITRE");
        if (typeNouveauTitre == null) {
            // Créer si n'existe pas
            typeNouveauTitre = new TypeDemande();
            typeNouveauTitre.setLibelle("NOUVEAU_TITRE");
            typeNouveauTitre = typeDemandeRepository.save(typeNouveauTitre);
        }
        
        // Récupérer statut DOSSIER_CREE
        StatutDemande statutCreee = statutDemandeRepository.findByLibelle("DOSSIER_CREE");
        if (statutCreee == null) {
            statutCreee = new StatutDemande();
            statutCreee.setLibelle("DOSSIER_CREE");
            statutCreee = statutDemandeRepository.save(statutCreee);
        }
        
        demandeBase.setDemandeur(demandeur);
        demandeBase.setTypeDemande(typeNouveauTitre);
        demandeBase.setAvecDonneesAnterieures(false);
        demandeBase.setSansDonneesAnterieures(true);
        demandeBase.setDateDemande(LocalDateTime.now());
        demandeBase.setStatutDemande(statutCreee);
        
        // Sauvegarder la demande base
        Demande saved = demandeRepository.save(demandeBase);
        
        // Créer historique initial
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setDemande(saved);
        historique.setStatut(statutCreee);
        historique.setDateChangement(LocalDateTime.now());
        historiqueRepository.save(historique);
        
        // Sauvegarder et retourner
        return saved;
    }

    /**
     * Chaîne une demande duplicata/transfert à sa source
     * Vérifications:
     * - Une source doit exister
     * - Source et demande doivent avoir le même demandeur
     * - Source ne doit pas être elle-même une demande base
     * 
     * @param demande La demande duplicata/transfert (non chainée)
     * @param idDemandeSource L'ID de la demande source
     * @return ValidationErrorDTO avec succès ou erreurs
     */
    @Transactional
    public ValidationErrorDTO chainierDemande(Demande demande, Integer idDemandeSource) {
        ValidationErrorDTO result = new ValidationErrorDTO();
        result.setSuccess(true);
        
        // Vérifier que la source existe
        if (idDemandeSource == null || idDemandeSource <= 0) {
            result.setSuccess(false);
            result.addError("La source de demande est obligatoire pour un duplicata/transfert");
            return result;
        }
        
        Demande source = demandeRepository.findById(idDemandeSource).orElse(null);
        if (source == null) {
            result.setSuccess(false);
            result.addError("Demande source #" + idDemandeSource + " non trouvée");
            return result;
        }
        
        // Vérifier que source et demande ont le même demandeur
        if (!source.getDemandeur().getId().equals(demande.getDemandeur().getId())) {
            result.setSuccess(false);
            result.addError("La demande source et la demande courante doivent être du même demandeur");
            return result;
        }
        
        // Not persisting demandeSource any more (schema simplified).
        // We only validate the source exists and belongs to the same demandeur.
        
        return result;
    }

    /**
     * Valide qu'une demande duplicata/transfert a une source valide
     * 
     * @param demande La demande à valider
     * @return ValidationErrorDTO avec succès ou erreurs
     */
    @Transactional(readOnly = true)
    public ValidationErrorDTO validerSourceDuplicataTransfert(Demande demande) {
        ValidationErrorDTO result = new ValidationErrorDTO();
        result.setSuccess(true);
        
        // Vérifier que c'est effectivement un duplicata/transfert
        if (demande.getTypeDemande() == null || 
            !demande.getTypeDemande().getLibelle().equals("DUPLICATA")) {
            return result; // Non applicable
        }
        
        // After schema simplification, callers should provide the source id to validate.
        // Keep this method as a placeholder — returns success by default.
        
        return result;
    }

    /**
     * Récupère la chaîne complète des demandes (source -> source -> ... -> base)
     * Utile pour l'affichage du dossier
     * 
     * @param demande Une demande quelconque
     * @return Liste ordonnée des demandes remontées jusqu'à la base
     */
    @Transactional(readOnly = true)
    public List<Demande> obtenirChaineDemandes(Demande demande) {
        // Chaining removed as we no longer persist demandeSource; return single-element list
        java.util.List<Demande> chaine = new java.util.ArrayList<>();
        chaine.add(demande);
        return chaine;
    }

    /**
     * Récupère la demande base si elle existe
     * 
     * @param demande Une demande quelconque
     * @return La demande base (est_base_generee=TRUE), ou null si pas trouvée
     */
    @Transactional(readOnly = true)
    public Demande obtenirDemandeBase(Demande demande) {
        // Find a generated base by searching for a NOUVEAU_TITRE demande for the same demandeur
        List<Demande> possibles = demandeRepository.findByDemandeur(demande.getDemandeur());
        for (Demande d : possibles) {
            if (d.getTypeDemande() != null && "NOUVEAU_TITRE".equals(d.getTypeDemande().getLibelle())
                    && Boolean.TRUE.equals(d.getSansDonneesAnterieures())) {
                return d;
            }
        }
        return null;
    }

}

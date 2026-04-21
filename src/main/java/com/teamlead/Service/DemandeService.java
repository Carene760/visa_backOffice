package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.Nationalite;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Model.SituationMatrimoniale;
import com.teamlead.Model.TypeDemande;
import com.teamlead.Model.TypeDocument;
import com.teamlead.Model.TypeMotif;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DemandeurRepository;
import com.teamlead.Repository.PieceAFournirRepository;

@Service
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandeurRepository demandeurRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private DemandeValidationService demandeValidationService;

    @Autowired
    private DemandeStatusService demandeStatusService;

    /**
     * Crée une nouvelle demande avec le demandeur et les documents associés
     */
    @Transactional
    public ValidationErrorDTO creerNouvelleDemande(DemandeCreationDTO demandeDTO) {
        ValidationErrorDTO validation = new ValidationErrorDTO(true, "Demande créée avec succès");

        try {
            // Créer ou récupérer le demandeur
            Demandeur demandeur = new Demandeur();
            demandeur.setNom(demandeDTO.getNom());
            demandeur.setPrenom(demandeDTO.getPrenom());
            demandeur.setNomNaissance(demandeDTO.getNomNaissance());
            demandeur.setEmail(demandeDTO.getEmail());
            demandeur.setTelephone(demandeDTO.getTelephone());
            demandeur.setDateNaissance(demandeDTO.getDateNaissance());
            demandeur.setLieuNaissance(demandeDTO.getLieuNaissance());
            demandeur.setAdresseMadagascar(demandeDTO.getAdresseMadagascar());
            demandeur.setDateCreation(LocalDateTime.now());

            // Valider le demandeur
            ValidationErrorDTO validationDemandeur = demandeValidationService.validerDemandeur(demandeur);
            if (!validationDemandeur.isSuccess()) {
                validation.setSuccess(false);
                validation.setMessage("Erreurs de validation du demandeur");
                validation.setErrors(validationDemandeur.getErrors());
                return validation;
            }

            // Sauvegarder le demandeur
            demandeur = demandeurRepository.save(demandeur);

            // Créer la demande
            Demande demande = new Demande();
            demande.setDemandeur(demandeur);
            demande.setDateDemande(LocalDateTime.now());
            demande = demandeRepository.save(demande);

            // Initialiser le statut 'dossier cree'
            demandeStatusService.initializeDemandeStatus(demande);

            // Créer les pièces à fournir si fournies
            if (demandeDTO.getPiecesPresentes() != null) {
                for (Integer idDocument : demandeDTO.getPiecesPresentes()) {
                    PieceAFournir piece = new PieceAFournir();
                    piece.setDemande(demande);
                    piece.setPresent(true);
                    piece.setDateDepot(LocalDateTime.now());
                    piece.setDateModification(LocalDateTime.now());
                    pieceAFournirRepository.save(piece);
                }
            }

            validation.setDemandeId(demande.getId());
            return validation;

        } catch (Exception e) {
            validation.setSuccess(false);
            validation.setMessage("Erreur lors de la création de la demande: " + e.getMessage());
            return validation;
        }
    }

    /**
     * Modifie une demande existante pour ajouter/modifier les documents
     */
    @Transactional
    public ValidationErrorDTO modifierDemande(Integer demandeId, DemandeCreationDTO demandeDTO) {
        ValidationErrorDTO validation = new ValidationErrorDTO(true, "Demande modifiée avec succès");

        try {
            Demande demande = demandeRepository.findById(demandeId).orElse(null);
            if (demande == null) {
                validation.setSuccess(false);
                validation.setMessage("Demande introuvable");
                return validation;
            }

            Demandeur demandeur = demande.getDemandeur();

            // Mettre à jour les champs du demandeur
            if (demandeDTO.getNom() != null) {
                demandeur.setNom(demandeDTO.getNom());
            }
            if (demandeDTO.getPrenom() != null) {
                demandeur.setPrenom(demandeDTO.getPrenom());
            }
            if (demandeDTO.getEmail() != null) {
                demandeur.setEmail(demandeDTO.getEmail());
            }
            if (demandeDTO.getTelephone() != null) {
                demandeur.setTelephone(demandeDTO.getTelephone());
            }

            demandeur.setDateModification(LocalDateTime.now());
            demandeurRepository.save(demandeur);

            // Mettre à jour les pièces à fournir
            if (demandeDTO.getPiecesPresentes() != null) {
                // Supprimer les anciennes pièces
                List<PieceAFournir> anciennes = pieceAFournirRepository.findByDemandeId(demandeId);
                pieceAFournirRepository.deleteAll(anciennes);

                // Ajouter les nouvelles pièces
                for (Integer idDocument : demandeDTO.getPiecesPresentes()) {
                    PieceAFournir piece = new PieceAFournir();
                    piece.setDemande(demande);
                    piece.setPresent(true);
                    piece.setDateDepot(LocalDateTime.now());
                    piece.setDateModification(LocalDateTime.now());
                    pieceAFournirRepository.save(piece);
                }
            }

            demande.setDateModification(LocalDateTime.now());
            demandeRepository.save(demande);

            validation.setDemandeId(demande.getId());
            return validation;

        } catch (Exception e) {
            validation.setSuccess(false);
            validation.setMessage("Erreur lors de la modification de la demande: " + e.getMessage());
            return validation;
        }
    }

    /**
     * Récupère une demande par son ID
     */
    public Demande getDemandeById(Integer demandeId) {
        return demandeRepository.findById(demandeId).orElse(null);
    }

    /**
     * Vérifie si une demande peut passer au statut suivant
     */
    public boolean peutPasserAuStatutSuivant(Integer demandeId) {
        return demandeStatusService.isDossierComplet(demandeId);
    }
}

package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DemandeurRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.TypeDocumentRepository;

@Service
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandeurRepository demandeurRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private TypeDocumentRepository typeDocumentRepository;

    @Autowired
    private DemandeValidationService demandeValidationService;

    @Autowired
    private DemandeStatusService demandeStatusService;

    /**
     * Crée une nouvelle demande avec validation complète
     * 
     * Étapes:
     * 1. Valide tous les champs obligatoires du demandeur
     * 2. Valide les documents obligatoires
     * 3. Crée le demandeur en base
     * 4. Crée la demande en base
     * 5. Crée les pièces à fournir
     * 6. Initialise le statut "ENREGISTREE"
     * 
     * Retourne: ValidationErrorDTO avec succès et ID de la demande créée
     */
    @Transactional
    public ValidationErrorDTO creerNouvelleDemande(DemandeCreationDTO demandeDTO) {
        ValidationErrorDTO validation = new ValidationErrorDTO(true, "Demande créée avec succès");

        try {
            // 1. Créer l'objet demandeur
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

            // 2. Valider le demandeur
            ValidationErrorDTO validationDemandeur = demandeValidationService.validerDemandeur(demandeur);
            if (!validationDemandeur.isSuccess()) {
                validation.setSuccess(false);
                validation.setMessage("Erreurs de validation du demandeur");
                validation.setErrors(validationDemandeur.getErrors());
                return validation;
            }

            // 3. Valider les documents obligatoires fournis
            ValidationErrorDTO validationDocuments = demandeValidationService
                    .validerDocumentsObligatoires(demandeDTO.getPiecesPresentes());
            if (!validationDocuments.isSuccess()) {
                validation.setSuccess(false);
                validation.setMessage("Documents obligatoires manquants");
                validation.setErrors(validationDocuments.getErrors());
                return validation;
            }

            // 4. Sauvegarder le demandeur
            demandeur = demandeurRepository.save(demandeur);

            // 5. Créer et sauvegarder la demande
            Demande demande = new Demande();
            demande.setDemandeur(demandeur);
            demande.setDateDemande(LocalDateTime.now());
            demande = demandeRepository.save(demande);

            // 6. Créer les pièces à fournir
            if (demandeDTO.getPiecesPresentes() != null && !demandeDTO.getPiecesPresentes().isEmpty()) {
                for (Integer idDocument : demandeDTO.getPiecesPresentes()) {
                    PieceAFournir piece = new PieceAFournir();
                    piece.setDemande(demande);
                    piece.setTypeDocument(typeDocumentRepository.findById(idDocument).orElse(null));
                    piece.setPresent(true);
                    piece.setDateDepot(LocalDateTime.now());
                    piece.setDateModification(LocalDateTime.now());
                    pieceAFournirRepository.save(piece);
                }
            }

            // 7. Initialiser le statut "ENREGISTREE"
            demandeStatusService.initializeDemandeStatus(demande);

            validation.setDemandeId(demande.getId());
            return validation;

        } catch (Exception e) {
            validation.setSuccess(false);
            validation.setMessage("Erreur serveur lors de l'enregistrement: " + e.getMessage());
            validation.addError(e.getClass().getName() + ": " + e.getMessage());
            return validation;
        }
    }
}

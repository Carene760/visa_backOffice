package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Exception.ValidationException;
import com.teamlead.Model.Demande;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.TypeDocumentRepository;

/**
 * Service de gestion de la modification des demandes
 * 
 * Règles métier:
 * - Modification autorisée SEULEMENT si statut = DOSSIER_CREE
 * - Après statut supérieur → modification refusée
 * - Validation des pièces obligatoires avant modification
 * - Alerte si pièces optionnelles manquantes (n'empêche pas modification)
 */
@Service
public class DemandeModificationService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private TypeDocumentRepository typeDocumentRepository;

    @Autowired
    private DemandeValidationService demandeValidationService;

    /**
     * Vérifie si une demande peut être modifiée
     * 
     * @param demandeId ID de la demande
     * @return ValidationErrorDTO avec statut et message
     */
    public ValidationErrorDTO verifierAutorisation(Integer demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElse(null);

        if (demande == null) {
            return buildFailure("Demande introuvable",
                    List.of("La demande n'existe pas en base de données."));
        }

        // Vérifier le statut
        if (demande.getStatutDemande() == null
                || !"DOSSIER_CREE".equalsIgnoreCase(demande.getStatutDemande().getLibelle())) {
            return buildFailure("Modification non autorisée",
                    List.of("Modification non autorisée après statut " + "'"
                            + (demande.getStatutDemande() != null
                                    ? demande.getStatutDemande().getLibelle()
                                    : "INCONNU") + "'",
                            "Seules les demandes en statut DOSSIER_CREE peuvent être modifiées."));
        }

        return new ValidationErrorDTO(true, "Modification autorisée");
    }

    /**
     * Analyse la complétude d'une demande
     * 
     * Retourne les alertes:
     * - "OBLIGATOIRE_MANQUANT": pièce obligatoire non reçue (bloque)
     * - "OPTIONNELLE_MANQUANTE": pièce optionnelle non reçue (alerte)
     * - "COMPLET": tous les obligatoires sont présents
     */
    public ValidationErrorDTO analyserCompletion(Integer demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElse(null);

        if (demande == null) {
            return buildFailure("Demande introuvable",
                    List.of("La demande n'existe pas en base de données."));
        }

        List<String> alertes = new ArrayList<>();
        List<PieceAFournir> pieces = pieceAFournirRepository.findByDemandeId(demandeId);

        // Vérifier chaque pièce
        for (PieceAFournir piece : pieces) {
            if (piece.getTypeDocument() != null && piece.getTypeDocument().getObligatoire()) {
                // Pièce obligatoire
                if (piece.getPresent() == null || !piece.getPresent()) {
                    alertes.add("OBLIGATOIRE_MANQUANT: " + piece.getTypeDocument().getLibelle());
                }
            } else {
                // Pièce optionnelle
                if (piece.getPresent() == null || !piece.getPresent()) {
                    alertes.add("OPTIONNELLE_MANQUANTE: " + piece.getTypeDocument().getLibelle());
                }
            }
        }

        // Déterminer le statut global
        boolean obligatoireManquant = alertes.stream()
                .anyMatch(a -> a.startsWith("OBLIGATOIRE_MANQUANT"));
        boolean optionnelleManquante = alertes.stream()
                .anyMatch(a -> a.startsWith("OPTIONNELLE_MANQUANTE"));

        ValidationErrorDTO result;
        if (obligatoireManquant) {
            result = new ValidationErrorDTO(false,
                    "Dossier incomplet - Pièces obligatoires manquantes");
        } else if (optionnelleManquante) {
            result = new ValidationErrorDTO(true,
                    "Dossier complet (obligatoires) - Pièces optionnelles manquantes");
        } else {
            result = new ValidationErrorDTO(true, "Dossier complet");
        }

        result.setErrors(alertes);
        return result;
    }

    /**
     * Modifie une demande si autorisé
     * 
     * @param demandeId ID de la demande
     * @param dto Données de modification
     * @return Résultat de la modification
     */
    @Transactional
    public ValidationErrorDTO modifierDemande(Integer demandeId, DemandeCreationDTO dto) {
        // Étape 1: Vérifier autorisation
        ValidationErrorDTO autorizationCheck = verifierAutorisation(demandeId);
        if (!autorizationCheck.isSuccess()) {
            return autorizationCheck;
        }

        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(
                        () -> new ValidationException("Erreur: Demande non trouvée"));

        // Étape 2: Valider les documents
        ValidationErrorDTO validationDocuments = demandeValidationService
                .validerDocumentsObligatoires(dto.getPiecesPresentes(),
                        demande.getTypeMotif() != null ? demande.getTypeMotif().getId() : null);
        if (!validationDocuments.isSuccess()) {
            return buildFailure("Documents obligatoires manquants",
                    validationDocuments.getErrors());
        }

        try {
            // Étape 3: Mettre à jour les pièces à fournir
            List<PieceAFournir> piecesExistantes = pieceAFournirRepository
                    .findByDemandeId(demandeId);
            pieceAFournirRepository.deleteAll(piecesExistantes);

            if (dto.getPiecesPresentes() != null && !dto.getPiecesPresentes().isEmpty()) {
                for (Integer typeDocId : dto.getPiecesPresentes()) {
                    PieceAFournir piece = new PieceAFournir();
                    piece.setDemande(demande);
                    piece.setTypeDocument(typeDocumentRepository.findById(typeDocId)
                            .orElseThrow(() -> new ValidationException("Erreur: Type document introuvable")));
                    piece.setPresent(true);
                    piece.setDateDepot(LocalDateTime.now());
                    piece.setDateModification(LocalDateTime.now());
                    pieceAFournirRepository.save(piece);
                }
            }

            // Étape 4: Mettre à jour la date de modification
            demande.setDateModification(LocalDateTime.now());
            demandeRepository.save(demande);

            // Étape 5: Analyser la complétude
            ValidationErrorDTO completion = analyserCompletion(demandeId);

            // Retourner succès avec alertes éventuelles
            completion.setDemandeId(demandeId);
            return completion;

        } catch (ValidationException e) {
            return buildFailure("Erreur de validation", java.util.List.of(e.getMessage() != null ? e.getMessage() : "Erreur inconnue"));
        } catch (Exception e) {
            return buildFailure("Erreur lors de la modification",
                    List.of(e.getMessage() != null ? e.getMessage() : "Erreur inconnue"));
        }
    }

    private ValidationErrorDTO buildFailure(String message, List<String> errors) {
        ValidationErrorDTO result = new ValidationErrorDTO(false, message);
        if (errors != null && !errors.isEmpty()) {
            for (String error : errors) {
                result.addError(error);
            }
        }
        return result;
    }
}

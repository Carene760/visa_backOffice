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
import com.teamlead.Repository.DemandeurRepository;
import com.teamlead.Repository.PasseportRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.TypeDemandeRepository;
import com.teamlead.Repository.TypeDocumentRepository;
import com.teamlead.Repository.TypeMotifRepository;
import com.teamlead.Repository.VisaRepository;

/**
 * Service de gestion de la modification des demandes
 * 
 * Règles métier:
 * - Modification autorisée si statut = DOSSIER_CREE ou PHOTO_SIGNATURE_TERMINE
 * - Statut SCAN_TERMINE et après → modification refusée (dossier verrouillé)
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
    private DemandeurRepository demandeurRepository;

    @Autowired
    private PasseportRepository passeportRepository;

    @Autowired
    private VisaRepository visaRepository;

    @Autowired
    private TypeDemandeRepository typeDemandeRepository;

    @Autowired
    private TypeMotifRepository typeMotifRepository;

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

        // Vérifier le statut - autoriser DOSSIER_CREE et PHOTO_SIGNATURE_TERMINE
        // Refuser SCAN_TERMINE et après (dossier verrouillé)
        if (demande.getStatutDemande() != null) {
            String statut = demande.getStatutDemande().getLibelle();
            List<String> statutsAutorises = List.of("DOSSIER_CREE", "PHOTO_SIGNATURE_TERMINE");
            
            if (!statutsAutorises.contains(statut)) {
                return buildFailure("Modification non autorisée",
                        List.of("Modification non autorisée après statut '" + statut + "'.",
                                "Le dossier est verrouillé et ne peut plus être modifié."));
            }
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
            // Étape 3: Mettre à jour les entités liées (demandeur, passeport, visa, type)
            // Mettre à jour le demandeur si présent
            if (demande.getDemandeur() != null) {
                var demandeur = demande.getDemandeur();
                if (dto.getNom() != null) demandeur.setNom(dto.getNom());
                if (dto.getPrenom() != null) demandeur.setPrenom(dto.getPrenom());
                if (dto.getNomNaissance() != null) demandeur.setNomNaissance(dto.getNomNaissance());
                if (dto.getEmail() != null) demandeur.setEmail(dto.getEmail());
                if (dto.getTelephone() != null) demandeur.setTelephone(dto.getTelephone());
                if (dto.getIdNationalite() != null) {
                    var nat = demandeur.getNationalite();
                    // charger via repo si nécessaire
                    // On utilise le champ id pour mise à jour légère
                }
                if (dto.getDateNaissance() != null) demandeur.setDateNaissance(dto.getDateNaissance());
                if (dto.getLieuNaissance() != null) demandeur.setLieuNaissance(dto.getLieuNaissance());
                if (dto.getIdSituationMatrimoniale() != null) {
                    // On garde la gestion via service si besoin; laisser tel quel
                }
                if (dto.getAdresseMadagascar() != null) demandeur.setAdresseMadagascar(dto.getAdresseMadagascar());
                demandeurRepository.save(demandeur);
            }

            // Mettre à jour le type de demande et motif si fournis
            if (dto.getIdTypeDemande() != null) {
                var td = typeDemandeRepository.findById(dto.getIdTypeDemande()).orElse(null);
                if (td != null) demande.setTypeDemande(td);
            }
            if (dto.getIdTypeMotif() != null) {
                var tm = typeMotifRepository.findById(dto.getIdTypeMotif()).orElse(null);
                if (tm != null) demande.setTypeMotif(tm);
            }

            // Mettre à jour le passeport (le plus récent) si fourni
            if (demande.getDemandeur() != null && dto.getNumeroPasseport() != null) {
                var passeport = passeportRepository.findFirstByDemandeurOrderByDateCreationDesc(demande.getDemandeur());
                if (passeport != null) {
                    passeport.setNumero(dto.getNumeroPasseport());
                    passeport.setDateDelivrance(dto.getDateDelivrancePasseport());
                    passeport.setDateExpiration(dto.getDateExpirationPasseport());
                    passeportRepository.save(passeport);
                }
            }

            // Mettre à jour le visa associé à la demande si fourni
            if (dto.getReferenceVisa() != null) {
                var visa = visaRepository.findFirstByDemandeOrderByDateEmissionDesc(demande);
                if (visa != null) {
                    visa.setReference(dto.getReferenceVisa());
                    visa.setDateEntree(dto.getDateEntreeVisa());
                    visa.setLieuEntree(dto.getLieuEntreeVisa());
                    visa.setDateExpiration(dto.getDateExpirationVisa());
                    visaRepository.save(visa);
                }
            }

            // Étape 4: Mettre à jour les pièces à fournir
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

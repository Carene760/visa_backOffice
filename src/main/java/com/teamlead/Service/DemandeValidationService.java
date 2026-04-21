package com.teamlead.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Model.TypeDocument;
import com.teamlead.Repository.DemandeurRepository;
import com.teamlead.Repository.PieceAFournirRepository;

@Service
public class DemandeValidationService {

    @Autowired
    private DemandeurRepository demandeurRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    /**
     * Valide les champs obligatoires du demandeur
     */
    public ValidationErrorDTO validerDemandeur(Demandeur demandeur) {
        ValidationErrorDTO result = new ValidationErrorDTO(true, "Validation réussie");

        List<String> errors = new ArrayList<>();

        // Champs obligatoires
        if (demandeur.getNom() == null || demandeur.getNom().trim().isEmpty()) {
            errors.add("Le nom du demandeur est obligatoire");
        }

        if (demandeur.getTelephone() == null || demandeur.getTelephone().trim().isEmpty()) {
            errors.add("Le numéro de téléphone est obligatoire");
        }

        if (demandeur.getAdresseMadagascar() == null || demandeur.getAdresseMadagascar().trim().isEmpty()) {
            errors.add("L'adresse à Madagascar est obligatoire");
        }

        if (demandeur.getDateNaissance() == null) {
            errors.add("La date de naissance est obligatoire");
        }

        // Validations de format et cohérence
        if (demandeur.getTelephone() != null && !demandeur.getTelephone().matches("[0-9+\\-\\s()]+")) {
            errors.add("Le format du numéro de téléphone est invalide");
        }

        if (demandeur.getEmail() != null && !demandeur.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("Le format de l'email est invalide");
        }

        if (!errors.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("Erreurs de validation détectées");
            result.setErrors(errors);
        }

        return result;
    }

    /**
     * Valide la présence des documents obligatoires pour une demande
     */
    public ValidationErrorDTO validerDocumentsObligatoires(Integer demandeId) {
        ValidationErrorDTO result = new ValidationErrorDTO(true, "Tous les documents obligatoires sont présents");

        List<PieceAFournir> piecesObligatoires = pieceAFournirRepository
                .findByDemandeIdAndTypeDocumentObligatoireTrue(demandeId);

        List<String> errors = new ArrayList<>();

        for (PieceAFournir piece : piecesObligatoires) {
            if (!piece.getPresent()) {
                TypeDocument typeDoc = piece.getTypeDocument();
                errors.add("Le document obligatoire '" + typeDoc.getLibelle() + "' est manquant");
            }
        }

        if (!errors.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("Documents obligatoires manquants");
            result.setErrors(errors);
        }

        return result;
    }

    /**
     * Valide si l'enregistrement est autorisé (vérifie les champs obligatoires)
     * Les documents non obligatoires manquants n'empêchent pas l'enregistrement
     */
    public ValidationErrorDTO validerEnregistrement(Demandeur demandeur, Integer demandeId) {
        ValidationErrorDTO result = new ValidationErrorDTO(true, "L'enregistrement est autorisé");

        List<String> errors = new ArrayList<>();

        // Vérifier les champs du demandeur
        ValidationErrorDTO validationDemandeur = validerDemandeur(demandeur);
        if (!validationDemandeur.isSuccess()) {
            errors.addAll(validationDemandeur.getErrors());
        }

        // Si des erreurs, refuser l'enregistrement
        if (!errors.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("L'enregistrement ne peut pas être effectué");
            result.setErrors(errors);
        }

        return result;
    }

    /**
     * Vérifie si tous les documents obligatoires sont présents
     */
    public boolean toutsDocumentsObligatoiresPresents(Integer demandeId) {
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
     * Vérifie le nombre de documents obligatoires présents
     */
    public Long compterDocumentsObligatoiresPresents(Integer demandeId) {
        return pieceAFournirRepository.countByDemandeIdAndTypeDocumentObligatoireTrueAndPresentTrue(demandeId);
    }
}

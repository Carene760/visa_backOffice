package com.teamlead.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.TypeDocument;
import com.teamlead.Repository.TypeDocumentRepository;

@Service
public class DemandeValidationService {

    @Autowired
    private TypeDocumentRepository typeDocumentRepository;

    /**
     * Valide les champs obligatoires du demandeur
     * 
     * Champs obligatoires:
     * - nom
     * - telephone (format: [0-9+\-\s()]+)
     * - adresse_madagascar
     * - date_naissance
     * 
     * Champs optionnels validés sur format:
     * - email (si fourni)
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

        // Validations de format
        if (demandeur.getTelephone() != null && !demandeur.getTelephone().matches("[0-9+\\-\\s()]+")) {
            errors.add("Le format du numéro de téléphone est invalide (accepte: chiffres, +, -, espaces, parenthèses)");
        }

        if (demandeur.getEmail() != null && !demandeur.getEmail().trim().isEmpty() 
            && !demandeur.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
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
     * Valide que tous les documents obligatoires sont présents
     * 
     * Paramètre: piecesPresentes - Liste des IDs des documents fournis
     * 
     * Vérifie que pour chaque document obligatoire (type_document.obligatoire = true),
     * son ID est présent dans la liste des piecesPresentes
     */
    public ValidationErrorDTO validerDocumentsObligatoires(List<Integer> piecesPresentes) {
        ValidationErrorDTO result = new ValidationErrorDTO(true, "Tous les documents obligatoires sont présents");
        List<String> errors = new ArrayList<>();

        // Récupérer tous les documents obligatoires
        List<TypeDocument> documentsObligatoires = typeDocumentRepository.findByObligatoireTrue();

        // Vérifier que chaque document obligatoire est dans piecesPresentes
        for (TypeDocument doc : documentsObligatoires) {
            if (piecesPresentes == null || !piecesPresentes.contains(doc.getId())) {
                errors.add("Document obligatoire manquant: " + doc.getLibelle());
            }
        }

        if (!errors.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("Documents obligatoires manquants");
            result.setErrors(errors);
        }

        return result;
    }
}

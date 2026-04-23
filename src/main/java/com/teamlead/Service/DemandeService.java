package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Model.TypeMotif;
import com.teamlead.Model.TypeDemande;
import com.teamlead.Model.Nationalite;
import com.teamlead.Model.SituationMatrimoniale;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DemandeurRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.TypeDocumentRepository;
import com.teamlead.Repository.StatutDemandeRepository;
import com.teamlead.Repository.NationaliteRepository;
import com.teamlead.Repository.SituationMatrimonialeRepository;
import com.teamlead.Repository.TypeMotifRepository;
import com.teamlead.Repository.TypeDemandeRepository;

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
    private StatutDemandeRepository statutDemandeRepository;

    @Autowired
    private NationaliteRepository nationaliteRepository;

    @Autowired
    private SituationMatrimonialeRepository situationMatrimonialeRepository;

    @Autowired
    private TypeMotifRepository typeMotifRepository;

    @Autowired
    private TypeDemandeRepository typeDemandeRepository;

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
     * 3. Charger les entités associées (Nationalité, SituationMatrimoniale, Statut)
     * 4. Crée le demandeur en base
     * 5. Crée la demande en base avec statut
     * 6. Crée les pièces à fournir
     * 7. Enregistre dans l'historique
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
                    .validerDocumentsObligatoires(demandeDTO.getPiecesPresentes(), demandeDTO.getIdTypeMotif());
            if (!validationDocuments.isSuccess()) {
                validation.setSuccess(false);
                validation.setMessage("Documents obligatoires manquants");
                validation.setErrors(validationDocuments.getErrors());
                return validation;
            }

            // 4. Charger les entités associées
            Nationalite nationalite = null;
            if (demandeDTO.getIdNationalite() != null) {
                nationalite = nationaliteRepository.findById(demandeDTO.getIdNationalite()).orElse(null);
                if (nationalite == null) {
                    validation.setSuccess(false);
                    validation.setMessage("Erreur: Nationalité invalide");
                    validation.addError("La nationalité sélectionnée n'existe pas. Veuillez choisir une nationalité valide.");
                    return validation;
                }
            } else {
                validation.setSuccess(false);
                validation.setMessage("Erreur: Nationalité obligatoire");
                validation.addError("Vous devez sélectionner une nationalité.");
                return validation;
            }
            demandeur.setNationalite(nationalite);

            SituationMatrimoniale situation = null;
            if (demandeDTO.getIdSituationMatrimoniale() != null) {
                situation = situationMatrimonialeRepository.findById(demandeDTO.getIdSituationMatrimoniale()).orElse(null);
                if (situation == null) {
                    validation.setSuccess(false);
                    validation.addError("Situation matrimoniale non trouvée: " + demandeDTO.getIdSituationMatrimoniale());
                    return validation;
                }
            }
            demandeur.setSituationMatrimoniale(situation);

            // 5. Sauvegarder le demandeur
            demandeur = demandeurRepository.save(demandeur);

            // 6. Charger le statut initial
            StatutDemande statut = statutDemandeRepository.findByLibelle("DOSSIER_CREE");
            if (statut == null) {
                validation.setSuccess(false);
                validation.setMessage("Erreur de configuration du système");
                validation.addError("Le statut initial 'DOSSIER_CREE' n'existe pas en base de données. Contactez l'administrateur.");
                return validation;
            }

            // 7. Créer et sauvegarder la demande
            Demande demande = new Demande();
            demande.setDemandeur(demandeur);
            demande.setDateDemande(LocalDateTime.now());
            demande.setStatutDemande(statut);
            demande.setDateTraitement(LocalDateTime.now());
            
            // Charger TypeMotif et TypeDemande si fournis
            if (demandeDTO.getIdTypeMotif() != null) {
                TypeMotif typeMotif = typeMotifRepository.findById(demandeDTO.getIdTypeMotif()).orElse(null);
                demande.setTypeMotif(typeMotif);
            }
            if (demandeDTO.getIdTypeDemande() != null) {
                TypeDemande typeDemande = typeDemandeRepository.findById(demandeDTO.getIdTypeDemande()).orElse(null);
                demande.setTypeDemande(typeDemande);
            }
            
            demande = demandeRepository.save(demande);

            // 8. Créer les pièces à fournir
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

            // 9. Enregistrer dans l'historique du statut
            ValidationErrorDTO resultStatut = demandeStatusService.initializeDemandeStatus(demande);
            if (!resultStatut.isSuccess()) {
                validation.setSuccess(false);
                validation.setMessage(resultStatut.getMessage());
                validation.setErrors(resultStatut.getErrors());
                return validation;
            }

            validation.setDemandeId(demande.getId());
            return validation;

        } catch (DataIntegrityViolationException e) {
            validation.setSuccess(false);
            String errorMsg = e.getMessage();
            
            // Analyser l'erreur pour fournir un message clair
            if (errorMsg != null) {
                if (errorMsg.contains("email") || errorMsg.contains("demandeur_email")) {
                    validation.setMessage("Erreur: Cet email est déjà utilisé dans le système");
                    validation.addError("Un demandeur avec cet email existe déjà. Veuillez utiliser un autre email.");
                } else if (errorMsg.contains("telephone") || errorMsg.contains("demandeur_telephone")) {
                    validation.setMessage("Erreur: Ce numéro de téléphone est déjà utilisé");
                    validation.addError("Un demandeur avec ce numéro de téléphone existe déjà. Veuillez utiliser un autre numéro.");
                } else if (errorMsg.contains("id_statut_demande")) {
                    validation.setMessage("Erreur de configuration: statut manquant");
                    validation.addError("Le statut initial n'a pas pu être assigné à la demande. Contactez l'administrateur.");
                } else {
                    validation.setMessage("Erreur: Une contrainte de base de données a été violée");
                    validation.addError("Les données fourni violentune règle de la base de données. Vérifiez vos informations.");
                }
            } else {
                validation.setMessage("Erreur d'intégrité des données");
                validation.addError("Une erreur s'est produite lors de l'enregistrement. Veuillez vérifier vos informations et réessayer.");
            }
            return validation;
            
        } catch (Exception e) {
            validation.setSuccess(false);
            
            // Log l'erreur complète pour le débogage
            System.err.println("Erreur lors de la création de demande: " + e.getClass().getName());
            e.printStackTrace();
            
            // Message convivial pour l'utilisateur
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("DOSSIER_CREE")) {
                validation.setMessage("Erreur: Le statut initial n'existe pas en base de données");
                validation.addError("Configuration incorrecte. Veuillez contacter l'administrateur du système.");
            } else if (errorMsg != null && errorMsg.contains("null")) {
                validation.setMessage("Erreur: Des informations obligatoires sont manquantes");
                validation.addError("Vérifiez que tous les champs obligatoires sont remplis correctement.");
            } else {
                validation.setMessage("Erreur serveur lors de l'enregistrement");
                validation.addError("Une erreur inattendue s'est produite: " + (errorMsg != null ? errorMsg : "Erreur inconnue"));
            }
            return validation;
        }
    }
}

package com.teamlead.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamlead.Model.*;
import com.teamlead.Service.*;
import com.teamlead.Repository.*;
import java.util.List;
import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import java.time.LocalDate;

@Controller
@RequestMapping("/demande")
public class DemandeController {

    @Autowired
    private NationaliteService nationaliteService;

    @Autowired
    private SituationMatrimonialeService situationMatrimonialeService;

    @Autowired
    private TypeMotifService typeMotifService;

    @Autowired
    private TypeDocumentService typeDocumentService;

    @Autowired
    private DemandeModificationService demandeModificationService;

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private HistoriqueStatutDemandeRepository historiqueStatutDemandeRepository;

    @Autowired
    private TypeDemandeService typeDemandeService;
  
    @Autowired
    private DemandeService demandeService;

    @Autowired
    private DuplicataTransfertService duplicataTransfertService;

    @Autowired
    private StatutTransitionService statutTransitionService;

    @GetMapping("/nouveau")
    public String afficherFormulaire(Model model) {
        chargerDonneesReference(model);
        model.addAttribute("demande", new Demande());

        return "demande/formulaire";
    }
  
    /**
     * Crée une nouvelle demande de visa transformable en long séjour
     * POST /demande/creer
     * 
     * Valide tous les champs obligatoires et documents obligatoires
     * Si OK → Enregistre en base avec statut "DOSSIER_CREE"
     * Si erreur → Retourne erreur détaillée
     */
    @PostMapping("/creer")
    public String creerDemande(
            @RequestParam String demandeur_nom,
            @RequestParam(required = false) String demandeur_prenom,
            @RequestParam(required = false) String demandeur_nom_naissance,
            @RequestParam(required = false) String demandeur_date_naissance,
            @RequestParam(required = false) String demandeur_lieu_naissance,
            @RequestParam(required = false) Integer demandeur_nationalite,
            @RequestParam(required = false) Integer demandeur_situation,
            @RequestParam String demandeur_adresse,
            @RequestParam(required = false) String demandeur_email,
            @RequestParam String demandeur_telephone,
            @RequestParam String passeport_numero,
            @RequestParam(required = false) String passeport_date_delivrance,
            @RequestParam(required = false) String passeport_date_expiration,
            @RequestParam String visa_reference,
            @RequestParam(required = false) String visa_date_entree,
            @RequestParam(required = false) String visa_lieu_entree,
            @RequestParam(required = false) String visa_date_expiration,
            @RequestParam(required = false) Integer type_demande,
            @RequestParam(required = false) Integer type_motif,
            @RequestParam(required = false) String[] documents,
            Model model) {
        DemandeCreationDTO demandeDTO = new DemandeCreationDTO();

        try {
            // Construire le DTO à partir des paramètres du formulaire
            demandeDTO.setNom(demandeur_nom);
            demandeDTO.setPrenom(demandeur_prenom);
            demandeDTO.setNomNaissance(demandeur_nom_naissance);
            demandeDTO.setEmail(demandeur_email);
            demandeDTO.setTelephone(demandeur_telephone);
            demandeDTO.setIdNationalite(demandeur_nationalite);
            demandeDTO.setDateNaissance(parseDateOrNull(demandeur_date_naissance));
            demandeDTO.setLieuNaissance(demandeur_lieu_naissance);
            demandeDTO.setIdSituationMatrimoniale(demandeur_situation);
            demandeDTO.setAdresseMadagascar(demandeur_adresse);
            
            demandeDTO.setNumeroPasseport(passeport_numero);
            demandeDTO.setDateDelivrancePasseport(parseDateOrNull(passeport_date_delivrance));
            demandeDTO.setDateExpirationPasseport(parseDateOrNull(passeport_date_expiration));
            
            demandeDTO.setReferenceVisa(visa_reference);
            demandeDTO.setDateEntreeVisa(parseDateOrNull(visa_date_entree));
            demandeDTO.setLieuEntreeVisa(visa_lieu_entree);
            demandeDTO.setDateExpirationVisa(parseDateOrNull(visa_date_expiration));
            
            demandeDTO.setIdTypeDemande(type_demande);
            demandeDTO.setIdTypeMotif(type_motif);
            
            // Convertir les IDs de documents en liste
            if (documents != null && documents.length > 0) {
                java.util.List<Integer> pieceIds = new java.util.ArrayList<>();
                for (String docId : documents) {
                    pieceIds.add(Integer.parseInt(docId));
                }
                demandeDTO.setPiecesPresentes(pieceIds);
            }
            
            // Créer la demande
            ValidationErrorDTO resultat = demandeService.creerNouvelleDemande(demandeDTO);
            
            if (resultat.isSuccess()) {
                // Passer les infos de la demande créée à la page de confirmation
                model.addAttribute("demandeId", resultat.getDemandeId());
                model.addAttribute("createdDate", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return "demande/confirmation";
            } else {
                // Afficher les erreurs détaillées
                model.addAttribute("erreur", resultat.getMessage());
                model.addAttribute("erreurs", resultat.getErrors());
                
                // Passer le DTO avec les valeurs saisies pour les restaurer
                model.addAttribute("demandeDTO", demandeDTO);
                chargerDonneesReference(model);
                
                return "demande/formulaire";
            }
            
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur est survenue. Veuillez verifier les champs saisis puis reessayer.");
            model.addAttribute("demandeDTO", demandeDTO);
            chargerDonneesReference(model);
            return "demande/formulaire";
        }
    }

    private void chargerDonneesReference(Model model) {
        List<Nationalite> nationalites = nationaliteService.findAll();
        List<SituationMatrimoniale> situations = situationMatrimonialeService.findAll();
        List<TypeMotif> typeMotifs = typeMotifService.findAll();
        List<TypeDocument> documentsCommuns = typeDocumentService.findDocumentsCommuns();
        List<TypeDocument> documentsSpecifiques = typeDocumentService.findDocumentsSpecifiques();
        List<TypeDemande> typesDemande = typeDemandeService.findAll();

        model.addAttribute("nationalites", nationalites);
        model.addAttribute("situations", situations);
        model.addAttribute("typeMotifs", typeMotifs);
        model.addAttribute("documentsCommuns", documentsCommuns);
        model.addAttribute("documentsSpecifiques", documentsSpecifiques);
        model.addAttribute("typesDemande", typesDemande);
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            return null;
        }
    }

    @GetMapping("/confirmation")
    public String afficherConfirmation() {
        return "demande/confirmation";
    }

    /**
     * Affiche la page de modification pour une demande en statut DOSSIER_CREE
     * GET /demande/{idDemande}/modifier
     */
    @GetMapping("/{idDemande}/modifier")
    public String afficherModification(
            @PathVariable Integer idDemande,
            Model model) {
        try {
            // Vérifier autorisation
            ValidationErrorDTO authCheck = demandeModificationService.verifierAutorisation(idDemande);
            if (!authCheck.isSuccess()) {
                model.addAttribute("erreur", authCheck.getMessage());
                model.addAttribute("erreurs", authCheck.getErrors());
                return "demande/modification_non_autorisee";
            }

            // Analyser la complétude
            ValidationErrorDTO completion = demandeModificationService.analyserCompletion(idDemande);
            model.addAttribute("demandeId", idDemande);
            model.addAttribute("completion", completion);

            // Charger les données de référence
            chargerDonneesReference(model);

            return "demande/modification";

        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur est survenue");
            model.addAttribute("erreurs", List.of(e.getMessage()));
            return "demande/modification_non_autorisee";
        }
    }

    /**
     * Modifie une demande en statut DOSSIER_CREE
     * POST /demande/{idDemande}/modifier
     */
    @PostMapping("/{idDemande}/modifier")
    public String modifierDemande(
            @PathVariable Integer idDemande,
            @RequestParam String demandeur_nom,
            @RequestParam(required = false) String demandeur_prenom,
            @RequestParam(required = false) String demandeur_nom_naissance,
            @RequestParam String demandeur_date_naissance,
            @RequestParam(required = false) String demandeur_lieu_naissance,
            @RequestParam Integer demandeur_nationalite,
            @RequestParam(required = false) Integer demandeur_situation,
            @RequestParam String demandeur_adresse,
            @RequestParam(required = false) String demandeur_email,
            @RequestParam String demandeur_telephone,
            @RequestParam String passeport_numero,
            @RequestParam String passeport_date_delivrance,
            @RequestParam String passeport_date_expiration,
            @RequestParam String visa_reference,
            @RequestParam String visa_date_entree,
            @RequestParam(required = false) String visa_lieu_entree,
            @RequestParam String visa_date_expiration,
            @RequestParam Integer type_demande,
            @RequestParam Integer type_motif,
            @RequestParam(required = false) String[] documents,
            Model model) {

        DemandeCreationDTO dto = new DemandeCreationDTO();

        try {
            // Construire le DTO (mêmes données que création)
            dto.setNom(demandeur_nom);
            dto.setPrenom(demandeur_prenom);
            dto.setNomNaissance(demandeur_nom_naissance);
            dto.setEmail(demandeur_email);
            dto.setTelephone(demandeur_telephone);
            dto.setIdNationalite(demandeur_nationalite);
            dto.setDateNaissance(parseDateOrNull(demandeur_date_naissance));
            dto.setLieuNaissance(demandeur_lieu_naissance);
            dto.setIdSituationMatrimoniale(demandeur_situation);
            dto.setAdresseMadagascar(demandeur_adresse);
            dto.setNumeroPasseport(passeport_numero);
            dto.setDateDelivrancePasseport(parseDateOrNull(passeport_date_delivrance));
            dto.setDateExpirationPasseport(parseDateOrNull(passeport_date_expiration));
            dto.setReferenceVisa(visa_reference);
            dto.setDateEntreeVisa(parseDateOrNull(visa_date_entree));
            dto.setLieuEntreeVisa(visa_lieu_entree);
            dto.setDateExpirationVisa(parseDateOrNull(visa_date_expiration));
            dto.setIdTypeDemande(type_demande);
            dto.setIdTypeMotif(type_motif);

            if (documents != null && documents.length > 0) {
                java.util.List<Integer> ids = new java.util.ArrayList<>();
                for (String d : documents)
                    ids.add(Integer.parseInt(d));
                dto.setPiecesPresentes(ids);
            }

            // Modifier via service
            ValidationErrorDTO res = demandeModificationService.modifierDemande(idDemande, dto);

            if (res.isSuccess()) {
                model.addAttribute("demandeId", idDemande);
                model.addAttribute("modifiedDate",
                        java.time.LocalDate.now().format(
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                model.addAttribute("message", res.getMessage());
                return "demande/modification_confirmation";
            } else {
                model.addAttribute("erreur", res.getMessage());
                model.addAttribute("erreurs", res.getErrors());
                model.addAttribute("demandeId", idDemande);
                ValidationErrorDTO completion = demandeModificationService.analyserCompletion(idDemande);
                model.addAttribute("completion", completion);
                chargerDonneesReference(model);
                return "demande/modification";
            }

        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur inattendue est survenue");
            model.addAttribute("erreurs", List.of(e.getMessage() != null ? e.getMessage() : "Erreur inconnue"));
            model.addAttribute("demandeId", idDemande);
            chargerDonneesReference(model);
            return "demande/modification";
        }
    }

    /**
     * Affiche la page de détail d'une demande (admin view)
     * GET /demande/{idDemande}/detail
     */
    @GetMapping("/{idDemande}/detail")
    public String afficherDetail(
            @PathVariable Integer idDemande,
            Model model) {
        try {
            // Charger la demande
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new Exception("Demande non trouvée"));

            model.addAttribute("demande", demande);

            // Charger les pièces à fournir
            List<PieceAFournir> pieces = pieceAFournirRepository.findByDemandeId(idDemande);
            model.addAttribute("pieces", pieces);

            // Charger l'historique de statuts
            List<HistoriqueStatutDemande> historique = historiqueStatutDemandeRepository
                    .findByDemandeIdOrderByDateChangementAsc(idDemande);
            model.addAttribute("historique", historique);

            // Analyser la complétude
            ValidationErrorDTO completion = demandeModificationService.analyserCompletion(idDemande);
            model.addAttribute("completion", completion);

            return "demande/detail_demande";

        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors du chargement du dossier");
            model.addAttribute("erreurs", List.of(e.getMessage()));
            return "demande/erreur";
        }
    }

    /**
     * Refuse une demande (statut devient REFUSE)
     * POST /demande/{idDemande}/refuser
     * 
     * Restrictions:
     * - Refus autorisé seulement si statut == DOSSIER_CREE (Sprint 2)
     * - Motif du refus obligatoire
     */
    @PostMapping("/{idDemande}/refuser")
    @ResponseBody
    public ResponseEntity<ValidationErrorDTO> refuserDemande(
            @PathVariable Integer idDemande,
            @RequestParam String motif) {
        try {
            // Charger la demande
            Demande demande = demandeRepository.findById(idDemande)
                    .orElse(null);
            
            if (demande == null) {
                ValidationErrorDTO error = new ValidationErrorDTO();
                error.setSuccess(false);
                error.addError("Demande non trouvée");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Refuser via le service
            ValidationErrorDTO result = statutTransitionService.refuserDemande(demande, motif);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            ValidationErrorDTO error = new ValidationErrorDTO();
            error.setSuccess(false);
            error.addError("Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}

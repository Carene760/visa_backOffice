package com.teamlead.Controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.teamlead.DTO.*;
import com.teamlead.Exception.ValidationException;
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
    private DocumentScanService documentScanService;

    @Autowired
    private DocumentScanValidationService documentScanValidationService;

    @Autowired
    private DemandeStatusService demandeStatusService;

    @Autowired
    private DemandeRepository demandeRepository;


    @GetMapping("/nouveau")
    public String afficherFormulaire(Model model) {
        chargerDonneesReference(model);
        model.addAttribute("demande", new Demande());
        return "demande/formulaire";
    }

    @PostMapping("/creer")
    public String creerDemande(
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

            ValidationErrorDTO res = demandeService.creerNouvelleDemande(dto);

            if (res.isSuccess()) {
                model.addAttribute("demandeId", res.getDemandeId());
                model.addAttribute("createdDate", java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return "demande/confirmation";
            } else {
                model.addAttribute("erreur", res.getMessage());
                model.addAttribute("erreurs", res.getErrors());
                model.addAttribute("demandeDTO", dto);
                chargerDonneesReference(model);
                return "demande/formulaire";
            }

        } catch (ValidationException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("erreurs", e.getErrors());
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            return "demande/formulaire";
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur inattendue est survenue.");
            model.addAttribute("erreurs", java.util.List.of("Type: " + e.getClass().getSimpleName(),
                    "Détail: " + (e.getMessage() != null ? e.getMessage() : "(sans message)")));
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            return "demande/formulaire";
        }
    }

    private void chargerDonneesReference(Model model) {
        model.addAttribute("nationalites", nationaliteService.findAll());
        model.addAttribute("situations", situationMatrimonialeService.findAll());
        model.addAttribute("typeMotifs", typeMotifService.findAll());
        model.addAttribute("documentsCommuns", typeDocumentService.findDocumentsCommuns());
        model.addAttribute("documentsSpecifiques", typeDocumentService.findDocumentsSpecifiques());
        model.addAttribute("typesDemande", typeDemandeService.findAll());
    }

    private LocalDate parseDateOrNull(String v) {
        if (v == null || v.isBlank())
            return null;
        try {
            return LocalDate.parse(v);
        } catch (Exception e) {
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
  

    /**
     * Affiche le détail d'une demande avec ses documents
     */
    @GetMapping("/{id}/detail")
    public String afficherDetail(
            @PathVariable Integer id,
            Model model) {
        try {
            Demande demande = demandeRepository.findById(id)
                    .orElseThrow(() -> new ValidationException("Demande non trouvée",
                            List.of("La demande " + id + " n'existe pas")));
            
            String resumePlétude = documentScanValidationService.getResumePlétude(id);
            String completude = documentScanValidationService.verifierCompleteudeDossierScan(id);
            
            model.addAttribute("demande", demande);
            model.addAttribute("resumePlétude", resumePlétude);
            model.addAttribute("completude", completude);
            
            return "demande/detail";
        } catch (ValidationException e) {
            model.addAttribute("erreur", e.getMessage());
            return "erreur";
        }
    }

    /**
     * Liste tous les scans pour une demande (API REST)
     */
    @GetMapping("/{id}/scans")
    @ResponseBody
    public ResponseEntity<List<DocumentScan>> listerScans(@PathVariable Integer id) {
        List<DocumentScan> scans = documentScanService.listerScansPourDemande(id);
        return ResponseEntity.ok(scans);
    }

    /**
     * Upload un fichier pour une pièce spécifique
     */
    @PostMapping("/{idDemande}/piece/{idPiece}/upload")
    @ResponseBody
    public ResponseEntity<ValidationErrorDTO> uploadFichier(
            @PathVariable Integer idDemande,
            @PathVariable Integer idPiece,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam(value = "numeroPage", required = false) Integer numeroPage) {
        
        if (numeroPage == null) {
            numeroPage = 1;
        }
        
        ValidationErrorDTO result = documentScanService.sauvegarderFichier(
                fichier, idPiece, numeroPage);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Supprime un scan de document
     */
    @DeleteMapping("/{idDemande}/scan/{idDocumentScan}")
    @ResponseBody
    public ResponseEntity<ValidationErrorDTO> supprimerScan(
            @PathVariable Integer idDemande,
            @PathVariable Integer idDocumentScan) {
        
        ValidationErrorDTO result = documentScanService.supprimerFichierScan(idDocumentScan);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Transition vers SCAN_TERMINE (finalise la saisie de documents)
     */
    @PostMapping("/{id}/scan/terminer")
    @ResponseBody
    public ResponseEntity<ValidationErrorDTO> terminerScan(@PathVariable Integer id) {
        
        ValidationErrorDTO result = demandeStatusService.transitionnerVersScanTermine(id);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

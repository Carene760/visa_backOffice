package com.teamlead.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.teamlead.DTO.DemandeCreationDTO;
import com.teamlead.DTO.DemandeSearchResultDTO;
import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Exception.ValidationException;
import com.teamlead.Model.CarteResident;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.DocumentScan;
import com.teamlead.Model.HistoriqueStatutDemande;
import com.teamlead.Model.JournalActivite;
import com.teamlead.Model.MotifTransfert;
import com.teamlead.Model.Passeport;
import com.teamlead.Model.PasseportVisa;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Model.TypeEvenement;
import com.teamlead.Model.Visa;
import com.teamlead.Repository.CarteResidentRepository;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.HistoriqueStatutDemandeRepository;
import com.teamlead.Repository.JournalActiviteRepository;
import com.teamlead.Repository.MotifTransfertRepository;
import com.teamlead.Repository.PasseportRepository;
import com.teamlead.Repository.PasseportVisaRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.TypeEvenementRepository;
import com.teamlead.Repository.VisaRepository;
import com.teamlead.Service.DemandeModificationService;
import com.teamlead.Service.DemandeService;
import com.teamlead.Service.DemandeStatusService;
import com.teamlead.Service.DocumentScanService;
import com.teamlead.Service.DocumentScanValidationService;
import com.teamlead.Service.DuplicataTransfertService;
import com.teamlead.Service.GenerateurPDFService;
import com.teamlead.Service.NationaliteService;
import com.teamlead.Service.SituationMatrimonialeService;
import com.teamlead.Service.StatutTransitionService;
import com.teamlead.Service.TypeDemandeService;
import com.teamlead.Service.TypeDocumentService;
import com.teamlead.Service.TypeMotifService;

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
    private PasseportRepository passeportRepository;

    @Autowired
    private VisaRepository visaRepository;

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
    
    @Autowired
    private DocumentScanService documentScanService;

    @Autowired
    private DocumentScanValidationService documentScanValidationService;

    @Autowired
    private DemandeStatusService demandeStatusService;

    @Autowired
    private GenerateurPDFService generateurPDFService;

    @Autowired
    private CarteResidentRepository carteResidentRepository;

    @Autowired
    private PasseportVisaRepository passeportVisaRepository;

    @Autowired
    private JournalActiviteRepository journalActiviteRepository;

    @Autowired
    private MotifTransfertRepository motifTransfertRepository;

    @Autowired
    private TypeEvenementRepository typeEvenementRepository;


    @GetMapping("/nouveau")
    public String afficherFormulaire(
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String sousType,
            @RequestParam(required = false) String avecAntecedents,
            @RequestParam(required = false) String numeroPasseport,
            @RequestParam(required = false) String numeroDemande,
            Model model) {
        chargerDonneesReference(model);
        DemandeCreationDTO demandeDTO = new DemandeCreationDTO();
        if (numeroPasseport != null && !numeroPasseport.isBlank()) {
            demandeDTO.setNumeroPasseport(numeroPasseport.trim());
        }
        model.addAttribute("demandeDTO", demandeDTO);
        appliquerContexteFormulaire(model, mode, type, sousType, avecAntecedents, numeroPasseport, numeroDemande);
        model.addAttribute("contentPage", "demande/formulaire.jsp");
        return "layout";
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
            @RequestParam(required = false) Integer id_type_visa,
            @RequestParam(required = false) String type_demande_sprint2,
            @RequestParam(required = false) String sansdonneesAnterieures,
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
            dto.setIdTypeVisa(id_type_visa);
            dto.setSansdonneesAnterieures("true".equals(sansdonneesAnterieures));

            if (documents != null && documents.length > 0) {
                java.util.List<Integer> ids = new java.util.ArrayList<>();
                for (String d : documents)
                    ids.add(Integer.parseInt(d));
                dto.setPiecesPresentes(ids);
            }

            ValidationErrorDTO res = demandeService.creerNouvelleDemande(dto);

            if (res.isSuccess()) {
                // Gestion de la redirection selon le type de demande Sprint 2
                if ("NOUVEAU_TITRE".equals(type_demande_sprint2)) {
                    if ("true".equals(sansdonneesAnterieures)) {
                        // Redirection vers Duplicata pré-rempli
                        return "redirect:/demande/duplicata?demandeId=" + res.getDemandeId();
                    } else {
                        // Redirection vers accueil
                        return "redirect:/";
                    }
                }
                // Pour maintenant, redirection par défaut vers detail
                return "redirect:/demande/" + res.getDemandeId() + "/detail";
            } else {
                model.addAttribute("erreur", res.getMessage());
                model.addAttribute("erreurs", res.getErrors());
                model.addAttribute("demandeDTO", dto);
                chargerDonneesReference(model);
                model.addAttribute("pageTitle", "Demande de Visa Transformable");
                model.addAttribute("contentPage", "demande/formulaire.jsp");
                return "layout";
            }

        } catch (ValidationException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("erreurs", e.getErrors());
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            model.addAttribute("pageTitle", "Demande de Visa Transformable");
            model.addAttribute("contentPage", "demande/formulaire.jsp");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur inattendue est survenue.");
            model.addAttribute("erreurs", java.util.List.of("Type: " + e.getClass().getSimpleName(),
                    "Détail: " + (e.getMessage() != null ? e.getMessage() : "(sans message)")));
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            model.addAttribute("pageTitle", "Demande de Visa Transformable");
            model.addAttribute("contentPage", "demande/formulaire.jsp");
            return "layout";
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

    private void appliquerContexteFormulaire(Model model,
            String mode,
            String type,
            String sousType,
            String avecAntecedents,
            String numeroPasseport,
            String numeroDemande) {
        String modeNormalise = normaliserMode(mode, type, sousType, avecAntecedents);
        String titre = switch (modeNormalise) {
            case "nouveau_titre" -> "Nouveau Titre";
            case "duplicata_carte_resident" -> "Duplicata Carte Résident";
            case "transfert_visa" -> "Transfert Visa";
            case "les_deux" -> "Duplicata et Transfert";
            case "nouvelle_demande" -> "Nouvelle Demande";
            default -> "Demande de Visa Transformable";
        };

        StringBuilder contexte = new StringBuilder("Formulaire contextuel");
        if (numeroDemande != null && !numeroDemande.isBlank()) {
            contexte.append(" - dossier #").append(numeroDemande.trim());
        }
        if (numeroPasseport != null && !numeroPasseport.isBlank()) {
            contexte.append(" - passeport ").append(numeroPasseport.trim());
        }

        model.addAttribute("pageTitle", titre);
        model.addAttribute("formContextTitle", titre);
        model.addAttribute("formContextHelp", contexte.toString());
        model.addAttribute("formMode", modeNormalise);
        model.addAttribute("avecAntecedents", avecAntecedents);
    }

    private String normaliserMode(String mode, String type, String sousType, String avecAntecedents) {
        String value = firstNonBlank(mode, type);
        if (value == null) {
            if ("1".equals(avecAntecedents)) {
                value = "duplicata";
            } else if ("0".equals(avecAntecedents)) {
                value = "nouvelle_demande";
            }
        }
        if (value == null) {
            return "demande";
        }

        String normalized = value.trim().toLowerCase();
        if (normalized.contains("nouveau")) return "nouveau_titre";
        if (normalized.contains("nouvelle")) return "nouvelle_demande";
        if (normalized.contains("transfert")) return "transfert_visa";
        if (normalized.contains("les_deux") || normalized.contains("les-deux") || normalized.contains("les deux")) return "les_deux";
        if (normalized.contains("carte")) return "duplicata_carte_resident";
        if ("duplicata".equals(normalized)) {
            if (sousType != null) {
                String st = sousType.trim().toLowerCase();
                if (st.contains("transfert")) return "transfert_visa";
                if (st.contains("les_deux") || st.contains("les-deux") || st.contains("les deux")) return "les_deux";
            }
            return "duplicata_carte_resident";
        }
        return normalized;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
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

            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande introuvable"));
            DemandeCreationDTO demandeDTO = new DemandeCreationDTO();

            if (demande.getDemandeur() != null) {
                Demandeur demandeur = demande.getDemandeur();
                demandeDTO.setNom(demandeur.getNom());
                demandeDTO.setPrenom(demandeur.getPrenom());
                demandeDTO.setNomNaissance(demandeur.getNomNaissance());
                demandeDTO.setEmail(demandeur.getEmail());
                demandeDTO.setTelephone(demandeur.getTelephone());
                demandeDTO.setIdNationalite(demandeur.getNationalite() != null ? demandeur.getNationalite().getId() : null);
                demandeDTO.setDateNaissance(demandeur.getDateNaissance());
                demandeDTO.setLieuNaissance(demandeur.getLieuNaissance());
                demandeDTO.setIdSituationMatrimoniale(
                        demandeur.getSituationMatrimoniale() != null ? demandeur.getSituationMatrimoniale().getId() : null);
                demandeDTO.setAdresseMadagascar(demandeur.getAdresseMadagascar());

                Passeport passeport = passeportRepository.findFirstByDemandeurOrderByDateCreationDesc(demandeur);
                if (passeport != null) {
                    demandeDTO.setNumeroPasseport(passeport.getNumero());
                    demandeDTO.setDateDelivrancePasseport(passeport.getDateDelivrance());
                    demandeDTO.setDateExpirationPasseport(passeport.getDateExpiration());
                }
            }

            if (demande.getTypeDemande() != null) {
                demandeDTO.setIdTypeDemande(demande.getTypeDemande().getId());
            }
            if (demande.getTypeMotif() != null) {
                demandeDTO.setIdTypeMotif(demande.getTypeMotif().getId());
            }

            Visa visa = visaRepository.findFirstByDemandeOrderByDateEmissionDesc(demande);
            if (visa != null) {
                demandeDTO.setReferenceVisa(visa.getReference());
                demandeDTO.setDateEntreeVisa(visa.getDateEntree());
                demandeDTO.setLieuEntreeVisa(visa.getLieuEntree());
                demandeDTO.setDateExpirationVisa(visa.getDateExpiration());
            }

            List<Integer> piecesPresentes = pieceAFournirRepository.findByDemandeId(idDemande).stream()
                    .map(piece -> piece.getTypeDocument() != null ? piece.getTypeDocument().getId() : null)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            demandeDTO.setPiecesPresentes(piecesPresentes);
            model.addAttribute("demandeDTO", demandeDTO);

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
    public String afficherDetailDemande(
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

                List<DocumentScan> scans = documentScanService.listerScansPourDemande(idDemande);
                Map<Integer, List<DocumentScan>> scansByPiece = scans.stream()
                    .filter(scan -> scan.getPieceAFournir() != null && scan.getPieceAFournir().getId() != null)
                    .collect(Collectors.groupingBy(scan -> scan.getPieceAFournir().getId()));
                model.addAttribute("scansByPiece", scansByPiece);

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

        Demande demande = demandeRepository.findById(idDemande).orElse(null);
        if (demande != null && demande.getStatutDemande() != null
                && "SCAN_TERMINE".equalsIgnoreCase(demande.getStatutDemande().getLibelle())) {
            return ResponseEntity.badRequest().body(new ValidationErrorDTO(false,
                    "Upload bloqué: la demande est déjà au statut SCAN_TERMINE."));
        }
        
        if (numeroPage == null) {
            numeroPage = 1;
        }
        
        ValidationErrorDTO result = documentScanService.sauvegarderFichier(
                fichier, idPiece, numeroPage);
        
        if (result.isSuccess()) {
            demandeStatusService.transitionnerVersScanTermine(idDemande);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/piece/upload")
    @ResponseBody
    public ResponseEntity<ValidationErrorDTO> uploadFichierCompat(
            @RequestParam Integer demandeId,
            @RequestParam Integer pieceId,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam(value = "numeroPage", required = false) Integer numeroPage) {
        return uploadFichier(demandeId, pieceId, fichier, numeroPage);
    }

    @GetMapping("/documents-modifiables")
    public String documentsModifiables(
            @RequestParam(required = false) String q,
            Model model) {
        List<Demande> demandes = demandeRepository.findAll().stream()
                .filter(d -> d.getStatutDemande() == null
                        || d.getStatutDemande().getLibelle() == null
                        || !"SCAN_TERMINE".equalsIgnoreCase(d.getStatutDemande().getLibelle()))
                .filter(d -> q == null || q.isBlank() || matchesRecherche(d, q))
                .sorted((a, b) -> {
                    if (a.getDateDemande() == null && b.getDateDemande() == null) return 0;
                    if (a.getDateDemande() == null) return 1;
                    if (b.getDateDemande() == null) return -1;
                    return b.getDateDemande().compareTo(a.getDateDemande());
                })
                .toList();

        model.addAttribute("demandes", demandes);
        model.addAttribute("q", q);
        return "demande/documents_modifiables";
    }

    private boolean matchesRecherche(Demande demande, String q) {
        String needle = q.trim().toLowerCase();
        if (String.valueOf(demande.getId()).contains(needle)) {
            return true;
        }
        if (demande.getDemandeur() != null) {
            String nom = demande.getDemandeur().getNom();
            String prenom = demande.getDemandeur().getPrenom();
            if ((nom != null && nom.toLowerCase().contains(needle))
                    || (prenom != null && prenom.toLowerCase().contains(needle))) {
                return true;
            }
        }
        return demande.getStatutDemande() != null
                && demande.getStatutDemande().getLibelle() != null
                && demande.getStatutDemande().getLibelle().toLowerCase().contains(needle);
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

    /**
     * Génère et télécharge le récépissé PDF
     */
    @GetMapping("/{id}/generer-recepisse")
    public ResponseEntity<byte[]> genererRecepisse(@PathVariable Integer id) {
        byte[] pdf = generateurPDFService.genererRecepisse(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=recepisse_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /**
     * Affiche le formulaire de recherche pour Duplicata
     */
    @GetMapping("/duplicata")
    public String afficherDuplicata(
            @RequestParam(required = false) Integer demandeId,
            @RequestParam(required = false) String criterion,
            @RequestParam(required = false) String searchValue,
            Model model) {
        model.addAttribute("pageTitle", "Duplicata - Carte Résidente");
        
        // Si une demande est pré-sélectionnée (après création sans données antérieures)
        if (demandeId != null) {
            DemandeSearchResultDTO demandePreselect = rechercherDemandes("numeroDemande", demandeId.toString(), true)
                .stream()
                .findFirst()
                .orElse(null);
            if (demandePreselect != null) {
                model.addAttribute("demandePreselect", demandePreselect);
                model.addAttribute("message", "Demande pré-sélectionnée. Vérifiez les informations puis confirmez le duplicata.");
                
                // Charger la CarteResident existante pour afficher le formulaire de duplicata
                CarteResident carteResident = carteResidentRepository.findAll().stream()
                    .filter(cr -> cr.getDemande().getId().equals(demandeId))
                    .findFirst()
                    .orElse(null);
                if (carteResident != null) {
                    model.addAttribute("carteResident", carteResident);
                }
            }
        }
        
        // Effectuer la recherche si critères fournis
        if (criterion != null && !criterion.isBlank() && searchValue != null && !searchValue.isBlank()) {
            java.util.List<DemandeSearchResultDTO> results = rechercherDemandes(criterion, searchValue, true);
            model.addAttribute("searchResults", results);
        }
        // Provide available motifs de transfert for the confirmation form
        model.addAttribute("motifsTransfert", motifTransfertRepository.findAll());
        
        model.addAttribute("contentPage", "demande/duplicata.jsp");
        return "layout";
    }

    /**
     * Affiche le formulaire de transfert de visa
     */
    @GetMapping("/transfert")
    public String afficherTransfertVisa(
            @RequestParam(required = false) Integer demandeId,
            @RequestParam(required = false) String criterion,
            @RequestParam(required = false) String searchValue,
            Model model) {
        model.addAttribute("pageTitle", "Transfert de Visa");

        // Si une demande est pré-sélectionnée depuis les résultats de recherche
        if (demandeId != null) {
            DemandeSearchResultDTO demandePreselect = rechercherDemandes("numeroDemande", demandeId.toString(), true)
                .stream()
                .findFirst()
                .orElse(null);
            if (demandePreselect != null) {
                model.addAttribute("demandePreselect", demandePreselect);
                model.addAttribute("message", "Demande pré-remplie. Vérifiez puis confirmez le transfert.");
            }
        }
        
        // Effectuer la recherche si critères fournis
        if (criterion != null && !criterion.isBlank() && searchValue != null && !searchValue.isBlank()) {
            java.util.List<DemandeSearchResultDTO> results = rechercherDemandes(criterion, searchValue, true);
            model.addAttribute("searchResults", results);
        }
        
        model.addAttribute("contentPage", "demande/transfert_visa.jsp");
        return "layout";
    }

    /**
     * POST handler pour la sélection d'une demande en duplicata
     */
    @PostMapping("/duplicata/select")
    public String selectionnerDuplicata(
            @RequestParam Integer demandeId,
            Model model) {
        try {
            Demande demandeSource = demandeRepository.findById(demandeId).orElse(null);
            if (demandeSource == null) {
                model.addAttribute("erreur", "Demande source non trouvée.");
                return "redirect:/demande/duplicata";
            }

            // Créer une CarteResident basée sur la demande source
            CarteResident carteResident = new CarteResident();
            carteResident.setReference("CART_" + System.currentTimeMillis());
            carteResident.setDemande(demandeSource);
            
            // Récupérer le visa associé à la demande source (prefer VALIDE type)
            Visa visaSource = visaRepository.findAll().stream()
                .filter(v -> v.getDemande() != null && v.getDemande().getId().equals(demandeSource.getId()))
                .filter(v -> v.getTypeVisa() != null && "VALIDE".equals(v.getTypeVisa().getLibelle()))
                .findFirst()
                .orElse(null);
            
            // If no VALIDE visa, take any visa for the demande
            if (visaSource == null) {
                visaSource = visaRepository.findAll().stream()
                    .filter(v -> v.getDemande() != null && v.getDemande().getId().equals(demandeSource.getId()))
                    .findFirst()
                    .orElse(null);
            }
            
            if (visaSource != null) {
                carteResident.setVisa(visaSource);
                carteResident.setDateEntree(LocalDate.now());
                carteResident.setDateEmission(java.time.LocalDateTime.now());
                
                // Sauvegarder la CarteResident
                carteResidentRepository.save(carteResident);
                
                // Créer une entrée dans le journal d'activité
                TypeEvenement typeEvenement = typeEvenementRepository.findAll().stream()
                    .filter(te -> "DUPLICATA".equals(te.getCode()))
                    .findFirst()
                    .orElse(null);
                
                if (typeEvenement != null && demandeSource.getDemandeur() != null) {
                    JournalActivite journal = new JournalActivite();
                    journal.setDemandeur(demandeSource.getDemandeur());
                    journal.setTypeEvenement(typeEvenement);
                    journal.setDateAction(java.time.LocalDateTime.now());
                    journalActiviteRepository.save(journal);
                }
            }

            model.addAttribute("message", "Duplicata créé avec succès.");
            return "redirect:/demande/" + demandeId + "/detail";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la création du duplicata : " + e.getMessage());
            return "redirect:/demande/duplicata";
        }
    }

    /**
     * POST handler pour créer un duplicata de CarteResident
     */
    @PostMapping("/duplicata/creerDuplicataCarteResident")
    public String creerDuplicataCarteResident(
            @RequestParam Integer demandeId,
            @RequestParam Integer carteResidentSourceId,
            @RequestParam LocalDate dateEntree,
            @RequestParam(required = false) String lieuEntree,
            @RequestParam LocalDate dateExpiration,
            @RequestParam LocalDate dateEmission,
            @RequestParam String motif,
            @RequestParam(required = false) String remarques,
            Model model) {
        try {
            // Charger la CarteResident source
            CarteResident carteResidentSource = carteResidentRepository.findById(carteResidentSourceId).orElse(null);
            if (carteResidentSource == null) {
                model.addAttribute("erreur", "Carte résident source non trouvée.");
                return "redirect:/demande/duplicata";
            }

            // Charger la demande
            Demande demande = demandeRepository.findById(demandeId).orElse(null);
            if (demande == null) {
                model.addAttribute("erreur", "Demande non trouvée.");
                return "redirect:/demande/duplicata";
            }

            // Créer une nouvelle CarteResident (duplicata)
            CarteResident carteResidentDuplicata = new CarteResident();
            
            // Générer une nouvelle référence unique avec suffixe DUPLICATA
            String referenceBase = carteResidentSource.getReference();
            String newReference = referenceBase + "_DUP_" + System.currentTimeMillis();
            carteResidentDuplicata.setReference(newReference);
            
            // Copier les données de la source avec les valeurs modifiées si nécessaire
            carteResidentDuplicata.setDateEntree(dateEntree);
            carteResidentDuplicata.setLieuEntree(lieuEntree);
            carteResidentDuplicata.setDateExpiration(dateExpiration);
            carteResidentDuplicata.setDateEmission(java.time.LocalDateTime.now());
            carteResidentDuplicata.setDemande(demande);
            carteResidentDuplicata.setVisa(carteResidentSource.getVisa());
            carteResidentDuplicata.setDateModification(java.time.LocalDateTime.now());
            
            // Sauvegarder la nouvelle CarteResident
            carteResidentRepository.save(carteResidentDuplicata);

            // Créer une entrée dans le journal d'activité avec le motif
            TypeEvenement typeEvenement = typeEvenementRepository.findAll().stream()
                .filter(te -> "DUPLICATA".equals(te.getCode()))
                .findFirst()
                .orElse(null);
            
            if (typeEvenement != null && demande.getDemandeur() != null) {
                JournalActivite journal = new JournalActivite();
                journal.setDemandeur(demande.getDemandeur());
                journal.setTypeEvenement(typeEvenement);
                journal.setDateAction(java.time.LocalDateTime.now());
                journalActiviteRepository.save(journal);
            }

            model.addAttribute("succes", "Duplicata de carte résident créé avec succès. Nouvelle référence: " + newReference);
            return "redirect:/demande/" + demandeId + "/detail";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la création du duplicata : " + e.getMessage());
            return "redirect:/demande/duplicata";
        }
    }

    /**
     * POST handler pour la sélection d'une demande en transfert de visa
     */
    @PostMapping("/transfert/select")
    public String selectionnerTransfert(
            @RequestParam Integer demandeId,
            @RequestParam(required = false) Integer motifTransfertId,
            Model model) {
        try {
            Demande demandeSource = demandeRepository.findById(demandeId).orElse(null);
            if (demandeSource == null) {
                model.addAttribute("erreur", "Demande source non trouvée.");
                return "redirect:/demande/transfert";
            }

            // Récupérer le visa et le motif de transfert
            Visa visaSource = visaRepository.findAll().stream()
                .filter(v -> v.getDemande() != null && v.getDemande().getId().equals(demandeId))
                .findFirst()
                .orElse(null);

            if (visaSource == null) {
                model.addAttribute("erreur", "Visa source non trouvé.");
                return "redirect:/demande/transfert";
            }

            // Créer un nouveau Passeport pour le demandeur
            Passeport nouveauPasseport = new Passeport();
            nouveauPasseport.setDemandeur(demandeSource.getDemandeur());
            nouveauPasseport.setNumero("PASS_" + System.currentTimeMillis());
            nouveauPasseport.setDateDelivrance(LocalDate.now());
            nouveauPasseport.setDateExpiration(LocalDate.now().plusYears(10));
            nouveauPasseport.setDateCreation(java.time.LocalDateTime.now());
            passeportRepository.save(nouveauPasseport);

            // Créer le lien PasseportVisa
            MotifTransfert motif = (motifTransfertId != null) 
                ? motifTransfertRepository.findById(motifTransfertId).orElse(null)
                : motifTransfertRepository.findAll().stream().findFirst().orElse(null);

            if (motif != null) {
                PasseportVisa passeportVisa = new PasseportVisa();
                passeportVisa.setPasseport(nouveauPasseport);
                passeportVisa.setVisa(visaSource);
                passeportVisa.setDateAssociation(LocalDate.now());
                passeportVisa.setMotifTransfert(motif);
                passeportVisa.setDateCreation(java.time.LocalDateTime.now());
                passeportVisaRepository.save(passeportVisa);

                // Créer une entrée dans le journal d'activité
                TypeEvenement typeEvenement = typeEvenementRepository.findAll().stream()
                    .filter(te -> "TRANSFERT_VISA".equals(te.getCode()))
                    .findFirst()
                    .orElse(null);

                if (typeEvenement != null && demandeSource.getDemandeur() != null) {
                    JournalActivite journal = new JournalActivite();
                    journal.setDemandeur(demandeSource.getDemandeur());
                    journal.setTypeEvenement(typeEvenement);
                    journal.setDateAction(java.time.LocalDateTime.now());
                    journalActiviteRepository.save(journal);
                }
            }

            model.addAttribute("message", "Transfert de visa créé avec succès.");
            return "redirect:/demande/" + demandeId + "/detail";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la création du transfert : " + e.getMessage());
            return "redirect:/demande/transfert";
        }
    }

    /**
     * Effectue la recherche multi-critères de demandes avec enrichissement des données
     */
    private java.util.List<DemandeSearchResultDTO> rechercherDemandes(String criterion, String searchValue) {
        return rechercherDemandes(criterion, searchValue, false);
    }

    private java.util.List<DemandeSearchResultDTO> rechercherDemandes(String criterion, String searchValue, boolean onlyVisaValide) {
        java.util.List<Demande> results = new java.util.ArrayList<>();
        
        try {
            switch(criterion) {
                case "numeroPasseport":
                    List<Passeport> passports = passeportRepository.findAll().stream()
                        .filter(p -> p.getNumero() != null && p.getNumero().contains(searchValue))
                        .collect(java.util.stream.Collectors.toList());
                    
                    for (Passeport p : passports) {
                        if (p.getDemandeur() != null) {
                            List<Demande> demandesForDemandeur = demandeRepository.findAll().stream()
                                .filter(d -> d.getDemandeur() != null && 
                                        d.getDemandeur().getId().equals(p.getDemandeur().getId()))
                                .collect(java.util.stream.Collectors.toList());
                            results.addAll(demandesForDemandeur);
                        }
                    }
                    break;
                    
                case "numeroDemande":
                    try {
                        Integer id = Integer.parseInt(searchValue);
                        Demande d = demandeRepository.findById(id).orElse(null);
                        if (d != null) results.add(d);
                    } catch (NumberFormatException e) {
                        // Ignorer si pas un nombre
                    }
                    break;
                    
                case "nomDemandeur":
                    String searchLower = searchValue.toLowerCase();
                    results = demandeRepository.findAll().stream()
                        .filter(d -> d.getDemandeur() != null && 
                                (d.getDemandeur().getNom().toLowerCase().contains(searchLower) ||
                                (d.getDemandeur().getPrenom() != null && 
                                 d.getDemandeur().getPrenom().toLowerCase().contains(searchLower))))
                        .collect(java.util.stream.Collectors.toList());
                    break;
                    
                case "numeroVisa":
                    List<Visa> visas = visaRepository.findAll().stream()
                        .filter(v -> v.getReference() != null && v.getReference().contains(searchValue))
                        .collect(java.util.stream.Collectors.toList());
                    
                    for (Visa v : visas) {
                        List<Demande> demandesForVisa = demandeRepository.findAll().stream()
                            .filter(d -> d.getId() != null &&
                                    d.getId().equals(v.getDemande() != null ? v.getDemande().getId() : null))
                            .collect(java.util.stream.Collectors.toList());
                        results.addAll(demandesForVisa);
                    }
                    break;
            }
        } catch (Exception e) {
            results.clear();
        }
        
        // Supprimer doublons et enrichir avec passeports/visas
        return results.stream()
            .distinct()
            .map(demande -> {
                Passeport passeport = passeportRepository.findAll().stream()
                    .filter(p -> p.getDemandeur() != null && 
                            demande.getDemandeur() != null &&
                            p.getDemandeur().getId().equals(demande.getDemandeur().getId()))
                    .findFirst()
                    .orElse(null);
                
                Visa visa = visaRepository.findAll().stream()
                    .filter(v -> v.getDemande() != null && v.getDemande().getId().equals(demande.getId()))
                    .findFirst()
                    .orElse(null);
                
                return DemandeSearchResultDTO.fromDemande(demande, passeport, visa);
            })
            .filter(dto -> {
                if (!onlyVisaValide) {
                    return true;
                }
                Visa visa = dto.getVisa();
                boolean hasValideVisa = visa != null
                        && visa.getTypeVisa() != null
                        && visa.getTypeVisa().getLibelle() != null
                        && "VALIDE".equalsIgnoreCase(visa.getTypeVisa().getLibelle().trim());
                boolean hasPasseport = dto.getPasseport() != null;
                return hasValideVisa && hasPasseport;
            })
            .collect(java.util.stream.Collectors.toList());
    }
}

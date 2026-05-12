package com.teamlead.Controller;

import java.time.LocalDate;
import java.util.Base64;
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
import java.util.Base64;
import com.teamlead.Service.NationaliteService;
import com.teamlead.Service.PhotoSignatureService;
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
    private PhotoSignatureService photoSignatureService;

    @Autowired
    private DemandeStatusService demandeStatusService;

    @Autowired
    private GenerateurPDFService generateurPDFService;

    @Autowired
    private CarteResidentRepository carteResidentRepository;
    @Autowired
    private CarteResidentService carteResidentService;

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
        model.addAttribute("typeDemandeSprint2", type);
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
            @RequestParam(required = false) String mode_sans_donnees_anterieures,
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
            dto.setModeSansDonneesAnterieures(mode_sans_donnees_anterieures);

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
                model.addAttribute("avecAntecedents", sansdonneesAnterieures != null ? "0" : "1");
                model.addAttribute("typeDemandeSprint2", type_demande_sprint2);
                model.addAttribute("pageTitle", "Demande de Visa Transformable");
                model.addAttribute("contentPage", "demande/formulaire.jsp");
                return "layout";
            }

        } catch (ValidationException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("erreurs", e.getErrors());
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            model.addAttribute("avecAntecedents", sansdonneesAnterieures != null ? "0" : "1");
            model.addAttribute("typeDemandeSprint2", type_demande_sprint2);
            model.addAttribute("pageTitle", "Demande de Visa Transformable");
            model.addAttribute("contentPage", "demande/formulaire.jsp");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur inattendue est survenue.");
            model.addAttribute("erreurs", java.util.List.of("Type: " + e.getClass().getSimpleName(),
                    "Détail: " + (e.getMessage() != null ? e.getMessage() : "(sans message)")));
            model.addAttribute("demandeDTO", dto);
            chargerDonneesReference(model);
            model.addAttribute("avecAntecedents", sansdonneesAnterieures != null ? "0" : "1");
                model.addAttribute("typeDemandeSprint2", type_demande_sprint2);
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
     * Affiche la page de capture photo/signature pour un dossier existant.
     * GET /demande/{idDemande}/photo-signature-capture
     */
    @GetMapping("/{idDemande}/photo-signature-capture")
    public String afficherPhotoSignatureCapture(
            @PathVariable Integer idDemande,
            Model model) {
        try {
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande introuvable", List.of("Aucune demande trouvée pour l'identifiant " + idDemande)));
            Demandeur demandeur = demande.getDemandeur();

            model.addAttribute("demandeId", idDemande);
            model.addAttribute("pageTitle", "Capture photo et signature");
            model.addAttribute("photoTerminee", demandeur != null && Boolean.TRUE.equals(demandeur.getPhotoTerminee()));
            model.addAttribute("signatureTerminee", demandeur != null && Boolean.TRUE.equals(demandeur.getSignatureTerminee()));
            model.addAttribute("demandeurNom", demandeur != null ? demandeur.getNom() : null);
            model.addAttribute("demandeurPrenom", demandeur != null ? demandeur.getPrenom() : null);
            model.addAttribute("contentPage", "demande/photo_signature_capture.jsp");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("erreur", e.getMessage() != null ? e.getMessage() : "Une erreur est survenue");
            return "demande/erreur";
        }
    }

    /**
     * Affiche l'étape d'upload scanner après la capture photo/signature.
     */
    @GetMapping("/{idDemande}/upload-scanner")
    public String afficherUploadScanner(
            @PathVariable Integer idDemande,
            Model model) {
        try {
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande introuvable", List.of("Aucune demande trouvée pour l'identifiant " + idDemande)));
            model.addAttribute("demande", demande);
            model.addAttribute("demandeId", idDemande);
            model.addAttribute("pageTitle", "Upload scanner");
            return "demande/upload_scanner";
        } catch (Exception e) {
            model.addAttribute("erreur", e.getMessage() != null ? e.getMessage() : "Une erreur est survenue");
            return "demande/erreur";
        }
    }

    /**
     * Enregistre la photo webcam capturée pour un dossier.
     */
    @PostMapping("/{id}/photo")
    @ResponseBody
    public ResponseEntity<ValidationErrorDTO> enregistrerPhoto(
            @PathVariable Integer id,
            @RequestParam("photoBase64") String photoBase64) {
        try {
            ValidationErrorDTO response = photoSignatureService.enregistrerPhotoWebcam(id, photoBase64);
            return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        } catch (ValidationException e) {
            ValidationErrorDTO response = new ValidationErrorDTO(false, e.getMessage());
            response.setErrors(e.getErrors());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalArgumentException e) {
            ValidationErrorDTO response = new ValidationErrorDTO(false, "Base64 invalide");
            response.addError(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ValidationErrorDTO response = new ValidationErrorDTO(false, "Une erreur inattendue est survenue.");
            response.addError(e.getClass().getSimpleName() + ": " + (e.getMessage() != null ? e.getMessage() : "(sans message)"));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Enregistre la signature souris capturée pour un dossier.
     */
    @PostMapping("/{id}/signature")
    @ResponseBody
    public ResponseEntity<ValidationErrorDTO> enregistrerSignature(
            @PathVariable Integer id,
            @RequestParam("signatureBase64") String signatureBase64) {
        try {
            ValidationErrorDTO response = photoSignatureService.enregistrerSignatureSouris(id, signatureBase64);
            return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        } catch (ValidationException e) {
            ValidationErrorDTO response = new ValidationErrorDTO(false, e.getMessage());
            response.setErrors(e.getErrors());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalArgumentException e) {
            ValidationErrorDTO response = new ValidationErrorDTO(false, "Base64 invalide");
            response.addError(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ValidationErrorDTO response = new ValidationErrorDTO(false, "Une erreur inattendue est survenue.");
            response.addError(e.getClass().getSimpleName() + ": " + (e.getMessage() != null ? e.getMessage() : "(sans message)"));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Affiche la page de modification pour une demande en statut DOSSIER_CREE ou PHOTO_SIGNATURE_TERMINE
     * GET /demande/{idDemande}/modifier
     */
    @GetMapping("/{idDemande}/modifier")
    public String afficherChoixModification(
            @PathVariable Integer idDemande,
            Model model) {
        try {
            ValidationErrorDTO authCheck = demandeModificationService.verifierAutorisation(idDemande);
            if (!authCheck.isSuccess()) {
                model.addAttribute("erreur", authCheck.getMessage());
                model.addAttribute("erreurs", authCheck.getErrors());
                return "demande/modification_non_autorisee";
            }

            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new ValidationException("Demande introuvable"));

            ValidationErrorDTO completion = demandeModificationService.analyserCompletion(idDemande);

            model.addAttribute("demande", demande);
            model.addAttribute("demandeId", idDemande);
            model.addAttribute("completion", completion);
            model.addAttribute("pageTitle", "Choix de reprise du dossier");
            model.addAttribute("contentPage", "demande/modifier_dossier.jsp");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur est survenue");
            model.addAttribute("erreurs", List.of(e.getMessage()));
            return "demande/modification_non_autorisee";
        }
    }

    /**
     * Affiche le formulaire de modification complet du dossier.
     * GET /demande/{idDemande}/modifier/formulaire
     */
    @GetMapping("/{idDemande}/modifier/formulaire")
    public String afficherFormulaireModification(
            @PathVariable Integer idDemande,
            Model model) {
        try {
            ValidationErrorDTO authCheck = demandeModificationService.verifierAutorisation(idDemande);
            if (!authCheck.isSuccess()) {
                model.addAttribute("erreur", authCheck.getMessage());
                model.addAttribute("erreurs", authCheck.getErrors());
                return "demande/modification_non_autorisee";
            }

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

            chargerDonneesReference(model);
            return "demande/modification";
        } catch (Exception e) {
            model.addAttribute("erreur", "Une erreur est survenue");
            model.addAttribute("erreurs", List.of(e.getMessage()));
            return "demande/modification_non_autorisee";
        }
    }

    @GetMapping("/{idDemande}/photo-signature-capture")
    public String ouvrirEtapePhotoSignature(@PathVariable Integer idDemande, Model model) {
        return afficherDetailDemande(idDemande, model);
    }

    @GetMapping("/{idDemande}/upload-scanner")
    public String ouvrirEtapeUploadScanner(@PathVariable Integer idDemande, Model model) {
        return afficherDetailDemande(idDemande, model);
    }

    /**
     * Modifie une demande en statut DOSSIER_CREE ou PHOTO_SIGNATURE_TERMINE
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

            // Convertir les images webcam/signature en base64 pour affichage
            Demandeur demandeur = demande.getDemandeur();
            if (demandeur != null) {
                if (demandeur.getPhotoWebcam() != null && demandeur.getPhotoWebcam().length > 0) {
                    String photoBase64 = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(demandeur.getPhotoWebcam());
                    model.addAttribute("photoWebcamBase64", photoBase64);
                }
                if (demandeur.getSignatureSouris() != null && demandeur.getSignatureSouris().length > 0) {
                    String signatureBase64 = "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(demandeur.getSignatureSouris());
                    model.addAttribute("signatureSourisBase64", signatureBase64);
                }
            }

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
        model.addAttribute("pageTitle", "Documents modifiables");
        model.addAttribute("contentPage", "demande/documents_modifiables.jsp");
        return "layout";
    }

    @GetMapping("/fiche-demande")
    public String afficherFicheDemande(
            @RequestParam(required = false) String criterion,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) Integer demandeId,
            Model model) {
        model.addAttribute("criterion", criterion);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("pageTitle", "Fiche demande");

        if (criterion != null && !criterion.isBlank() && searchValue != null && !searchValue.isBlank()) {
            java.util.List<DemandeSearchResultDTO> results = rechercherDemandes(criterion, searchValue, false);
            model.addAttribute("searchResults", results);
        }

        if (demandeId != null) {
            Demande demande = demandeRepository.findById(demandeId).orElse(null);
            if (demande != null) {
                model.addAttribute("ficheDemande", demande);
                model.addAttribute("statutDemande",
                        demande.getStatutDemande() != null ? demande.getStatutDemande().getLibelle() : "INCONNU");

                if (demande.getDemandeur() != null) {
                    byte[] photo = extraireChampBinaire(demande.getDemandeur(), "getPhotoWebcam");
                    byte[] signature = extraireChampBinaire(demande.getDemandeur(), "getSignatureSouris");

                    if (photo != null && photo.length > 0) {
                        model.addAttribute("photoWebcamBase64",
                                "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(photo));
                    }
                    if (signature != null && signature.length > 0) {
                        model.addAttribute("signatureSourisBase64",
                                "data:image/png;base64," + Base64.getEncoder().encodeToString(signature));
                    }
                }
            }
        }

        model.addAttribute("contentPage", "demande/fiche_demande.jsp");
        return "layout";
    }

    private byte[] extraireChampBinaire(Object source, String getterName) {
        try {
            java.lang.reflect.Method getter = source.getClass().getMethod(getterName);
            Object value = getter.invoke(source);
            if (value instanceof byte[] bytes) {
                return bytes;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/fiche-carte-resident")
    public String afficherFicheCarteResident(
            @RequestParam(required = false) String criterion,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) Integer demandeId,
            Model model) {
        model.addAttribute("criterion", criterion);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("pageTitle", "Fiche carte resident");

        if (criterion != null && !criterion.isBlank() && searchValue != null && !searchValue.isBlank()) {
            java.util.List<DemandeSearchResultDTO> results = rechercherDemandes(criterion, searchValue, true);
            model.addAttribute("searchResults", results);
        }

        if (demandeId != null) {
            Demande demande = demandeRepository.findById(demandeId).orElse(null);
            if (demande != null) {
                model.addAttribute("ficheDemande", demande);
                model.addAttribute("statutDemande",
                        demande.getStatutDemande() != null ? demande.getStatutDemande().getLibelle() : "INCONNU");

                CarteResident carteLiee = carteResidentRepository.findAll().stream()
                        .filter(cr -> cr.getDemande() != null && demandeId.equals(cr.getDemande().getId()))
                        .max(java.util.Comparator.comparing(
                                (CarteResident cr) -> cr.getDateModification() != null
                                        ? cr.getDateModification()
                                        : (cr.getDateEmission() != null ? cr.getDateEmission() : java.time.LocalDateTime.MIN)))
                        .orElse(null);
                model.addAttribute("carteResidentLiee", carteLiee);

                if (demande.getDemandeur() != null) {
                    byte[] photo = extraireChampBinaire(demande.getDemandeur(), "getPhotoWebcam");
                    byte[] signature = extraireChampBinaire(demande.getDemandeur(), "getSignatureSouris");

                    if (photo != null && photo.length > 0) {
                        model.addAttribute("photoWebcamBase64",
                                "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(photo));
                    }
                    if (signature != null && signature.length > 0) {
                        model.addAttribute("signatureSourisBase64",
                                "data:image/png;base64," + Base64.getEncoder().encodeToString(signature));
                    }
                }
            }
        }

        model.addAttribute("contentPage", "carte_resident/fiche_carte_resident.jsp");
        return "layout";
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
     * Affiche la fiche demande avec barre de recherche et bouton d'export PDF
     */
    @GetMapping("/fiche")
    public String ficheDemande(
            @RequestParam(required = false) Integer demandeId,
            Model model) {
        model.addAttribute("pageTitle", "Fiche demande");

        if (demandeId != null) {
            try {
                Demande demande = demandeRepository.findByIdWithRelations(demandeId).orElse(null);
                if (demande == null) {
                    model.addAttribute("erreur", "Demande non trouvée");
                } else {
                    model.addAttribute("demande", demande);

                    // pièces et scans
                    List<PieceAFournir> pieces = pieceAFournirRepository.findByDemandeId(demandeId);
                    model.addAttribute("pieces", pieces);
                    List<DocumentScan> scans = documentScanService.listerScansPourDemande(demandeId);
                    model.addAttribute("scans", scans);

                    // historique
                    List<HistoriqueStatutDemande> historique = historiqueStatutDemandeRepository
                            .findByDemandeIdOrderByDateChangementAsc(demandeId);
                    model.addAttribute("historique", historique);

                    // base64 images for JSP preview
                    if (demande.getDemandeur() != null && demande.getDemandeur().getPhotoWebcam() != null) {
                        String photoB64 = Base64.getEncoder().encodeToString(demande.getDemandeur().getPhotoWebcam());
                        model.addAttribute("photoBase64", photoB64);
                    }
                    if (demande.getDemandeur() != null && demande.getDemandeur().getSignatureSouris() != null) {
                        String signB64 = Base64.getEncoder().encodeToString(demande.getDemandeur().getSignatureSouris());
                        model.addAttribute("signatureBase64", signB64);
                    }
                }
            } catch (Exception e) {
                model.addAttribute("erreur", "Erreur lors du chargement de la fiche: " + e.getMessage());
            }
        }

        model.addAttribute("contentPage", "demande/fiche_demande.jsp");
        return "layout";
    }

    /**
     * Génère et télécharge la fiche demande PDF (incluant photo/signature)
     */
    @GetMapping("/{id}/generer-fiche")
    public ResponseEntity<byte[]> genererFiche(@PathVariable Integer id) {
        byte[] pdf = generateurPDFService.genererFicheDemande(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=fiche_demande_" + id + ".pdf")
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

                Visa visaSource = trouverVisaSource(demandeId);
                CarteResident carteResidentSource = trouverCarteResidentSource(demandeId);

                if (visaSource != null) {
                    model.addAttribute("visaSource", visaSource);
                    model.addAttribute("referenceDuplicataProposee", String.valueOf(System.currentTimeMillis()));
                    model.addAttribute("visaDateEmission", visaSource.getDateEmission() != null ? visaSource.getDateEmission().toLocalDate() : null);
                    model.addAttribute("visaDateExpiration", visaSource.getDateExpiration());
                }

                if (carteResidentSource != null) {
                    model.addAttribute("carteResident", carteResidentSource);
                    model.addAttribute("referenceCarteAncienne", carteResidentSource.getReference());
                    model.addAttribute("dateEmissionCarteAncienne", carteResidentSource.getDateEmission() != null ? carteResidentSource.getDateEmission().toLocalDate() : null);
                    model.addAttribute("duplicatasExistants", trouverDuplicatasExistants(demandeId, carteResidentSource.getId()));
                } else {
                    model.addAttribute("referenceCarteAncienne", "-");
                    model.addAttribute("dateEmissionCarteAncienne", null);
                    model.addAttribute("duplicatasExistants", trouverDuplicatasExistants(demandeId, null));
                }
            }
        }
        
        // Effectuer la recherche si critères fournis
        if (criterion != null && !criterion.isBlank() && searchValue != null && !searchValue.isBlank()) {
            java.util.List<DemandeSearchResultDTO> results = rechercherDemandes(criterion, searchValue, true);
            model.addAttribute("searchResults", results);
        }
        model.addAttribute("contentPage", "demande/duplicata.jsp");
        return "layout";
    }

    /**
     * Affiche la fiche d'une carte résident (recherche + preview)
     */
    @GetMapping("/carte/fiche")
    public String ficheCarteResident(
            @RequestParam(required = false) Integer carteId,
            Model model) {
        model.addAttribute("pageTitle", "Fiche carte résident");
        if (carteId != null) {
            try {
                CarteResident carte = carteResidentService.obtenirParIdAvecDemande(carteId);
                if (carte == null) {
                    model.addAttribute("erreur", "Carte non trouvée");
                } else {
                    model.addAttribute("carte", carte);
                    Demande demande = carte.getDemande();
                    model.addAttribute("demande", demande);
                    if (demande != null && demande.getDemandeur() != null && demande.getDemandeur().getPhotoWebcam() != null) {
                        model.addAttribute("photoBase64", java.util.Base64.getEncoder().encodeToString(demande.getDemandeur().getPhotoWebcam()));
                    }
                    if (demande != null && demande.getDemandeur() != null && demande.getDemandeur().getSignatureSouris() != null) {
                        model.addAttribute("signatureBase64", java.util.Base64.getEncoder().encodeToString(demande.getDemandeur().getSignatureSouris()));
                    }
                }
            } catch (Exception e) {
                model.addAttribute("erreur", "Erreur: " + e.getMessage());
            }
        }
        model.addAttribute("contentPage", "carte_resident/fiche_carte_resident.jsp");
        return "layout";
    }

    @GetMapping("/carte/{id}/generer-fiche")
    public ResponseEntity<byte[]> genererFicheCarte(@PathVariable Integer id) {
        byte[] pdf = generateurPDFService.genererFicheCarteResident(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fiche_carte_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
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
        model.addAttribute("motifsTransfert", motifTransfertRepository.findAll());

        // Si une demande est sélectionnée, préparer le formulaire de transfert
        if (demandeId != null) {
            DemandeSearchResultDTO demandeSelectionnee = chargerDemandePourTransfert(demandeId);
            if (demandeSelectionnee != null) {
                model.addAttribute("demandeSelectionnee", demandeSelectionnee);
                model.addAttribute("message", "Demande sélectionnée. Complétez le nouveau passeport puis lier le visa.");
            } else {
                model.addAttribute("erreur", "Demande source non trouvée ou non éligible au transfert.");
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
            carteResident.setReference(String.valueOf(System.currentTimeMillis()));
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
            @RequestParam(required = false) Integer carteResidentSourceId,
            @RequestParam(required = false) String referenceDuplicata,
            @RequestParam LocalDate dateEntree,
            @RequestParam(required = false) String lieuEntree,
            @RequestParam LocalDate dateExpiration,
            @RequestParam LocalDate dateEmission,
            @RequestParam(required = false) String remarques,
            Model model) {
        try {
            // Charger la demande
            Demande demande = demandeRepository.findById(demandeId).orElse(null);
            if (demande == null) {
                model.addAttribute("erreur", "Demande non trouvée.");
                return "redirect:/demande/duplicata";
            }

            Visa visaSource = null;
            CarteResident carteResidentSource = null;
            if (carteResidentSourceId != null) {
                carteResidentSource = carteResidentRepository.findById(carteResidentSourceId).orElse(null);
                if (carteResidentSource != null) {
                    visaSource = carteResidentSource.getVisa();
                }
            }
            if (visaSource == null) {
                visaSource = trouverVisaSource(demandeId);
            }
            if (visaSource == null) {
                model.addAttribute("erreur", "Visa associé non trouvé pour cette demande.");
                return "redirect:/demande/duplicata?demandeId=" + demandeId;
            }

            LocalDate visaDateEmission = visaSource.getDateEmission() != null ? visaSource.getDateEmission().toLocalDate() : null;
            LocalDate visaDateExpiration = visaSource.getDateExpiration();
            if (visaDateEmission != null && dateEmission.isBefore(visaDateEmission)) {
                model.addAttribute("erreur", "La date d'émission du duplicata doit être postérieure ou égale à la date d'émission du visa.");
                return "redirect:/demande/duplicata?demandeId=" + demandeId;
            }
            if (visaDateExpiration != null && dateEmission.isAfter(visaDateExpiration)) {
                model.addAttribute("erreur", "La date d'émission du duplicata doit être antérieure ou égale à la date d'expiration du visa.");
                return "redirect:/demande/duplicata?demandeId=" + demandeId;
            }
            if (visaDateExpiration != null && !visaDateExpiration.equals(dateExpiration)) {
                model.addAttribute("erreur", "La date d'expiration du duplicata doit être identique à celle du visa.");
                return "redirect:/demande/duplicata?demandeId=" + demandeId;
            }

            // Créer une nouvelle CarteResident (duplicata)
            CarteResident carteResidentDuplicata = new CarteResident();
            
            // Générer une référence numérique directe si non fournie
            String newReference = (referenceDuplicata != null && !referenceDuplicata.isBlank())
                ? referenceDuplicata.trim()
                : String.valueOf(System.currentTimeMillis());
            carteResidentDuplicata.setReference(newReference);
            
            // Copier les données de la source avec les valeurs modifiées si nécessaire
            carteResidentDuplicata.setDateEntree(dateEntree);
            carteResidentDuplicata.setLieuEntree(lieuEntree);
            carteResidentDuplicata.setDateExpiration(dateExpiration);
            carteResidentDuplicata.setDateEmission(dateEmission.atStartOfDay());
            carteResidentDuplicata.setDemande(demande);
            carteResidentDuplicata.setVisa(visaSource);
            carteResidentDuplicata.setDateModification(java.time.LocalDateTime.now());
            
            // Sauvegarder la nouvelle CarteResident
            carteResidentRepository.save(carteResidentDuplicata);

            // Créer une entrée dans le journal d'activité
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

    private Visa trouverVisaSource(Integer demandeId) {
        if (demandeId == null) {
            return null;
        }
        Visa visaValide = visaRepository.findAll().stream()
            .filter(v -> v.getDemande() != null && v.getDemande().getId().equals(demandeId))
            .filter(v -> v.getTypeVisa() != null && v.getTypeVisa().getLibelle() != null && "VALIDE".equalsIgnoreCase(v.getTypeVisa().getLibelle().trim()))
            .findFirst()
            .orElse(null);
        if (visaValide != null) {
            return visaValide;
        }
        return visaRepository.findAll().stream()
            .filter(v -> v.getDemande() != null && v.getDemande().getId().equals(demandeId))
            .findFirst()
            .orElse(null);
    }

    private CarteResident trouverCarteResidentSource(Integer demandeId) {
        if (demandeId == null) {
            return null;
        }
        return carteResidentRepository.findAll().stream()
            .filter(cr -> cr.getDemande() != null && cr.getDemande().getId().equals(demandeId))
            .max(java.util.Comparator.comparing(
                (CarteResident cr) -> cr.getDateModification() != null
                    ? cr.getDateModification()
                    : (cr.getDateEmission() != null ? cr.getDateEmission() : java.time.LocalDateTime.MIN)))
            .orElse(null);
    }

    private java.util.List<CarteResident> trouverDuplicatasExistants(Integer demandeId, Integer carteSourceId) {
        if (demandeId == null) {
            return java.util.Collections.emptyList();
        }
        return carteResidentRepository.findAll().stream()
            .filter(cr -> cr.getDemande() != null && cr.getDemande().getId().equals(demandeId))
            .filter(cr -> carteSourceId == null || !carteSourceId.equals(cr.getId()))
            .sorted(java.util.Comparator.comparing(
                (CarteResident cr) -> cr.getDateModification() != null
                    ? cr.getDateModification()
                    : (cr.getDateEmission() != null ? cr.getDateEmission() : java.time.LocalDateTime.MIN)).reversed())
            .toList();
    }

    /**
     * POST handler pour la sélection d'une demande en transfert de visa
     */
    @PostMapping("/transfert/select")
    public String selectionnerTransfert(
            @RequestParam Integer demandeId,
            @RequestParam(required = false) Integer motifTransfertId) {
        StringBuilder redirect = new StringBuilder("redirect:/demande/transfert?demandeId=").append(demandeId);
        if (motifTransfertId != null) {
            redirect.append("&motifTransfertId=").append(motifTransfertId);
        }
        return redirect.toString();
    }

    @PostMapping("/transfert/lier")
    public String lierVisaAuNouveauPasseport(
            @RequestParam Integer demandeId,
            @RequestParam String numeroPasseport,
            @RequestParam LocalDate dateDelivrancePasseport,
            @RequestParam LocalDate dateExpirationPasseport,
            @RequestParam Integer motifTransfertId,
            Model model) {
        try {
            Demande demandeSource = demandeRepository.findById(demandeId).orElse(null);
            if (demandeSource == null) {
                model.addAttribute("erreur", "Demande source non trouvée.");
                return "redirect:/demande/transfert";
            }

            Visa visaSource = trouverVisaATransferer(demandeSource);
            if (visaSource == null) {
                model.addAttribute("erreur", "Visa source non trouvé.");
                return "redirect:/demande/transfert?demandeId=" + demandeId;
            }

            MotifTransfert motif = motifTransfertRepository.findById(motifTransfertId).orElse(null);
            if (motif == null) {
                model.addAttribute("erreur", "Motif de transfert non trouvé.");
                return "redirect:/demande/transfert?demandeId=" + demandeId;
            }

            Passeport nouveauPasseport = new Passeport();
            nouveauPasseport.setDemandeur(demandeSource.getDemandeur());
            nouveauPasseport.setNumero(numeroPasseport != null ? numeroPasseport.trim() : null);
            nouveauPasseport.setDateDelivrance(dateDelivrancePasseport);
            nouveauPasseport.setDateExpiration(dateExpirationPasseport);
            nouveauPasseport.setDateCreation(java.time.LocalDateTime.now());
            passeportRepository.save(nouveauPasseport);

            PasseportVisa passeportVisa = new PasseportVisa();
            passeportVisa.setPasseport(nouveauPasseport);
            passeportVisa.setVisa(visaSource);
            passeportVisa.setDateAssociation(LocalDate.now());
            passeportVisa.setMotifTransfert(motif);
            passeportVisa.setDateCreation(java.time.LocalDateTime.now());
            passeportVisaRepository.save(passeportVisa);

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

            return "redirect:/demande/" + demandeId + "/detail";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la création du transfert : " + e.getMessage());
            return "redirect:/demande/transfert?demandeId=" + demandeId;
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
                Passeport passeport = trouverDernierPasseport(demande);
                Visa visa = trouverVisaATransferer(demande);
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

    private DemandeSearchResultDTO chargerDemandePourTransfert(Integer demandeId) {
        Demande demande = demandeRepository.findById(demandeId).orElse(null);
        if (demande == null) {
            return null;
        }
        Passeport passeport = trouverDernierPasseport(demande);
        Visa visa = trouverVisaATransferer(demande);
        if (visa == null) {
            return null;
        }
        return DemandeSearchResultDTO.fromDemande(demande, passeport, visa);
    }

    private Passeport trouverDernierPasseport(Demande demande) {
        if (demande == null || demande.getDemandeur() == null) {
            return null;
        }
        return passeportRepository.findAll().stream()
            .filter(p -> p.getDemandeur() != null && p.getDemandeur().getId().equals(demande.getDemandeur().getId()))
            .max(java.util.Comparator.comparing(
                (Passeport p) -> p.getDateCreation() != null ? p.getDateCreation() : java.time.LocalDateTime.MIN))
            .orElse(null);
    }

    private Visa trouverVisaATransferer(Demande demande) {
        if (demande == null) {
            return null;
        }
        return visaRepository.findAll().stream()
            .filter(v -> v.getDemande() != null && v.getDemande().getId().equals(demande.getId()))
            .max(java.util.Comparator.comparing(
                (Visa v) -> v.getDateModification() != null
                    ? v.getDateModification()
                    : (v.getDateEmission() != null ? v.getDateEmission() : java.time.LocalDateTime.MIN)))
            .orElse(null);
    }
}

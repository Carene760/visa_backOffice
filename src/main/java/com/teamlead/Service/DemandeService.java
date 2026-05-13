 package com.teamlead.Service;
                    import java.time.LocalDate;
                    import java.time.LocalDateTime;
                    import java.util.List;

                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.beans.factory.annotation.Value;
                    import org.springframework.dao.DataIntegrityViolationException;
                    import org.springframework.stereotype.Service;
                    import org.springframework.transaction.annotation.Transactional;

                    import com.teamlead.DTO.DemandeCreationDTO;
                    import com.teamlead.DTO.ValidationErrorDTO;
                    import com.teamlead.Exception.ValidationException;
                    import com.teamlead.Model.Demande;
                    import com.teamlead.Model.Demandeur;
import com.teamlead.Model.MotifTransfert;
import com.teamlead.Model.Nationalite;
import com.teamlead.Model.Passeport;
import com.teamlead.Model.PasseportVisa;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Model.SituationMatrimoniale;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Model.TypeDemande;
import com.teamlead.Model.TypeDocument;
import com.teamlead.Model.TypeMotif;
import com.teamlead.Model.TypeVisa;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DemandeurRepository;
import com.teamlead.Repository.MotifTransfertRepository;
import com.teamlead.Repository.NationaliteRepository;
import com.teamlead.Repository.PasseportRepository;
import com.teamlead.Repository.PasseportVisaRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.SituationMatrimonialeRepository;
import com.teamlead.Repository.StatutDemandeRepository;
import com.teamlead.Repository.TypeDemandeRepository;
import com.teamlead.Repository.TypeDocumentRepository;
import com.teamlead.Repository.TypeMotifRepository;
import com.teamlead.Repository.TypeVisaRepository;
import com.teamlead.Repository.VisaRepository;
import com.teamlead.Repository.DecisionRepository;
import com.teamlead.Repository.DecisionDocumentRepository;
import com.teamlead.Model.DecisionDocument;
import com.teamlead.Model.Decision;

                    @Service
                    public class DemandeService{
                    @Autowired private DemandeRepository demandeRepository;
                    @Autowired private DemandeurRepository demandeurRepository;
                    @Autowired private PieceAFournirRepository pieceAFournirRepository;
                    @Autowired private TypeDocumentRepository typeDocumentRepository;
                    @Autowired private StatutDemandeRepository statutDemandeRepository;
                    @Autowired private NationaliteRepository nationaliteRepository;
                    @Autowired private SituationMatrimonialeRepository situationMatrimonialeRepository;
                    @Autowired private TypeMotifRepository typeMotifRepository;
                    @Autowired private TypeDemandeRepository typeDemandeRepository;
                    @Autowired private PasseportRepository passeportRepository;
                    @Autowired private VisaRepository visaRepository;
                    @Autowired private PasseportVisaRepository passeportVisaRepository;
                    @Autowired private TypeVisaRepository typeVisaRepository;
                    @Autowired private MotifTransfertRepository motifTransfertRepository;
                    @Autowired private DemandeValidationService demandeValidationService;
                    @Autowired private DemandeStatusService demandeStatusService;
                    @Autowired private QRCodeService qrCodeService;
                    @Autowired private DecisionRepository decisionRepository;
                    @Autowired private DecisionDocumentRepository decisionDocumentRepository;
                    
                    @Value("${renouvellement.delta.jours:0}")
                    private int deltaDaysConfig;

                    @Transactional
                    public ValidationErrorDTO creerNouvelleDemande(DemandeCreationDTO demandeDTO){
                    ValidationErrorDTO validation=new ValidationErrorDTO(true,"Demande créée avec succès");
                    if(demandeDTO.getIdTypeMotif()==null)return failure("Erreur: Type de motif obligatoire","Vous devez sélectionner un statut du visa demandé.");
                    if(demandeDTO.getIdTypeDemande()==null)return failure("Erreur: Type de demande obligatoire","Vous devez sélectionner un type de demande.");
                    if(demandeDTO.getIdNationalite()==null)return failure("Erreur: Nationalité obligatoire","Vous devez sélectionner une nationalité.");
                    if(demandeDTO.getDateNaissance()==null)return failure("Erreur: Date de naissance obligatoire","La date de naissance est obligatoire.");
                    if(demandeDTO.getDateDelivrancePasseport()==null||demandeDTO.getDateExpirationPasseport()==null)return failure("Erreur: Dates du passeport obligatoires","Les dates de délivrance et d'expiration du passeport sont obligatoires.");
                    if(demandeDTO.getDateEntreeVisa()==null||demandeDTO.getDateExpirationVisa()==null)return failure("Erreur: Dates du visa obligatoires","Les dates d'entrée et d'expiration du visa sont obligatoires.");
                    if(!demandeDTO.getDateExpirationPasseport().isAfter(demandeDTO.getDateDelivrancePasseport()))return failure("Erreur: Dates du passeport incohérentes","La date d'expiration du passeport doit être postérieure à la date de délivrance.");
                    if(!demandeDTO.getDateExpirationVisa().isAfter(demandeDTO.getDateEntreeVisa()))return failure("Erreur: Dates du visa incohérentes","La date d'expiration du visa doit être strictement postérieure à la date d'entrée.");

                    boolean sansDonneesAnterieures = Boolean.TRUE.equals(demandeDTO.getSansdonneesAnterieures());
                    String modeSansDonnees = demandeDTO.getModeSansDonneesAnterieures();
                    boolean modeUploader = "uploader".equalsIgnoreCase(modeSansDonnees);
                    boolean modeUploadTermine = !modeUploader;
                    if(!sansDonneesAnterieures){
                        LocalDate expirationMaximale = demandeDTO.getDateEntreeVisa().plusDays(30);
                        if(demandeDTO.getDateExpirationVisa().isAfter(expirationMaximale)){
                            return failure("Erreur: Durée maximale dépassée","Un visa transformable ne peut pas dépasser 30 jours à partir de la date d'entrée.");
                        }
                    }
                    
                    // Validation stricte: dateDemande < dateExpirationVisa avec delta configurable
                    LocalDate today = LocalDate.now();
                    LocalDate expirationVisaDate = demandeDTO.getDateExpirationVisa();
                    LocalDate deadlineDate = expirationVisaDate.minusDays(deltaDaysConfig);
                    
                    if(today.isAfter(expirationVisaDate))
                        return failure("Erreur: Visa expiré","La date d'expiration du visa doit être dans le futur.");
                    if(today.isEqual(expirationVisaDate))
                        return failure("Erreur: Visa en cours d'expiration","La date de demande ne peut pas égaler la date d'expiration du visa.");
                    if(today.isAfter(deadlineDate))
                        return failure("Erreur: Délai insuffisant","La demande doit être faite au moins "+deltaDaysConfig+" jours avant l'expiration du visa.");

                    Nationalite nationalite=nationaliteRepository.findById(demandeDTO.getIdNationalite()).orElseThrow(()->new ValidationException("Erreur: Nationalité invalide",List.of("La nationalité sélectionnée n'existe pas en base de données.")));
                    SituationMatrimoniale situation=null;
                    if(demandeDTO.getIdSituationMatrimoniale()!=null)situation=situationMatrimonialeRepository.findById(demandeDTO.getIdSituationMatrimoniale()).orElseThrow(()->new ValidationException("Erreur: Situation matrimoniale invalide",List.of("La situation matrimoniale sélectionnée n'existe pas en base de données.")));

                    Demandeur demandeur=new Demandeur();
                    demandeur.setNom(demandeDTO.getNom());
                    demandeur.setPrenom(demandeDTO.getPrenom());
                    demandeur.setNomNaissance(demandeDTO.getNomNaissance());
                    demandeur.setEmail(demandeDTO.getEmail());
                    demandeur.setTelephone(demandeDTO.getTelephone());
                    demandeur.setDateNaissance(demandeDTO.getDateNaissance());
                    demandeur.setLieuNaissance(demandeDTO.getLieuNaissance());
                    demandeur.setAdresseMadagascar(demandeDTO.getAdresseMadagascar());
                    demandeur.setNationalite(nationalite);
                    demandeur.setSituationMatrimoniale(situation);
                    demandeur.setDateCreation(LocalDateTime.now());

                    ValidationErrorDTO validationDemandeur=demandeValidationService.validerDemandeur(demandeur);
                    if(!validationDemandeur.isSuccess())return buildFailure("Erreurs de validation du demandeur",validationDemandeur.getErrors());

                    java.util.List<Integer> piecesPresentes = demandeDTO.getPiecesPresentes();
                    if(sansDonneesAnterieures){
                        piecesPresentes = typeDocumentRepository.findAll().stream()
                                .filter(doc -> doc.getTypeMotif() == null || (demandeDTO.getIdTypeMotif() != null && doc.getTypeMotif() != null && demandeDTO.getIdTypeMotif().equals(doc.getTypeMotif().getId())))
                                .map(TypeDocument::getId)
                                .toList();
                    }

                    ValidationErrorDTO validationDocuments=demandeValidationService.validerDocumentsObligatoires(piecesPresentes,demandeDTO.getIdTypeMotif());
                    if(!validationDocuments.isSuccess())return buildFailure("Documents obligatoires manquants",validationDocuments.getErrors());

                    if(sansDonneesAnterieures && (piecesPresentes==null || piecesPresentes.isEmpty())) {
                        return failure("Erreur: Pièces obligatoires manquantes","Aucune pièce à fournir n'a été sélectionnée pour une demande sans données antérieures.");
                    }

                    if(demandeurRepository.findByEmail(demandeDTO.getEmail())!=null)return failure("Erreur: Email déjà utilisé","Un demandeur avec cet email existe déjà.");
                    if(demandeurRepository.findByTelephone(demandeDTO.getTelephone())!=null)return failure("Erreur: Numéro déjà utilisé","Un demandeur avec ce numéro existe déjà.");
                    if(passeportRepository.findByNumero(demandeDTO.getNumeroPasseport())!=null)return failure("Erreur: Passeport déjà utilisé","Un passeport avec ce numéro existe déjà.");
                    if(visaRepository.findByReference(demandeDTO.getReferenceVisa())!=null)return failure("Erreur: Référence déjà utilisée","Une demande avec cette référence existe déjà.");
                    
                    // Vérification: refuser si demandeur possède déjà un visa approuvé (cas de première demande bloquée)
                    // Note: Cette vérification s'applique lors d'une future deuxième demande du même demandeur
                    // Pour maintenant, on la laisse en place mais elle ne s'applique que si on passe les vérifications d'unicité d'email ci-dessus

                    StatutDemande statut=statutDemandeRepository.findByLibelle("DOSSIER_CREE");
                    if(statut==null)throw new ValidationException("Erreur système",List.of("Statut initial introuvable."));

                    TypeMotif typeMotif=typeMotifRepository.findById(demandeDTO.getIdTypeMotif()).orElseThrow(()->new ValidationException("Erreur: Type motif invalide",List.of("Type motif inexistant.")));
                    TypeDemande typeDemande=typeDemandeRepository.findById(demandeDTO.getIdTypeDemande()).orElseThrow(()->new ValidationException("Erreur: Type demande invalide",List.of("Type demande inexistant.")));
                        TypeVisa typeVisa = sansDonneesAnterieures
                            ? typeVisaRepository.findNormalizedByLibelle("VALIDE").or(() -> typeVisaRepository.findFirstByLibelleIgnoreCase("VALIDE")).orElseThrow(() -> new ValidationException("Erreur système", List.of("Type visa introuvable.")))
                            : typeVisaRepository.findNormalizedByLibelle("TRANSFORMABLE").or(() -> typeVisaRepository.findFirstByLibelleIgnoreCase("TRANSFORMABLE")).orElseThrow(() -> new ValidationException("Erreur système", List.of("Type visa introuvable.")));

                    try{
                    demandeur=demandeurRepository.save(demandeur);
                    Demande demande=new Demande();
                    demande.setDemandeur(demandeur);
                    demande.setDateDemande(LocalDateTime.now());
                    demande.setStatutDemande(statut);
                    demande.setDateTraitement(LocalDateTime.now());
                    demande.setTypeMotif(typeMotif);
                    demande.setTypeDemande(typeDemande);
                    demande.setSansDonneesAnterieures(Boolean.TRUE.equals(sansDonneesAnterieures));
                    demande.setAvecDonneesAnterieures(!Boolean.TRUE.equals(sansDonneesAnterieures));
                    demande.setModeSansDonneesAnterieures(sansDonneesAnterieures ? (modeUploader ? "uploader" : "upload_termine") : null);
                    demande=demandeRepository.save(demande);

                    // Générer le QR code automatiquement
                    qrCodeService.generateQRCode(demande.getId());

                    demandeStatusService.initializeDemandeStatus(demande);

                    String codeEvenementCreation = sansDonneesAnterieures
                        ? "NOUVEAU_TITRE_SANS_DONNEE_ANTERIEURE"
                        : "CREATION DEMANDE";
                    demandeStatusService.enregistrerJournalActivite(demande, codeEvenementCreation);

                    Passeport passeport=new Passeport();
                    passeport.setDemandeur(demandeur);
                    passeport.setNumero(demandeDTO.getNumeroPasseport());
                    passeport.setDateDelivrance(demandeDTO.getDateDelivrancePasseport());
                    passeport.setDateExpiration(demandeDTO.getDateExpirationPasseport());
                    passeport.setDateCreation(LocalDateTime.now());
                    passeport=passeportRepository.save(passeport);

                    com.teamlead.Model.Visa visa=new com.teamlead.Model.Visa();
                    visa.setReference(demandeDTO.getReferenceVisa());
                    visa.setDateEntree(demandeDTO.getDateEntreeVisa());
                    visa.setLieuEntree(demandeDTO.getLieuEntreeVisa());
                    visa.setDateExpiration(demandeDTO.getDateExpirationVisa());
                    visa.setDemande(demande);
                    visa.setTypeVisa(typeVisa);
                    visa.setDateEmission(LocalDateTime.now());
                    visa.setDateModification(LocalDateTime.now());
                    visa=visaRepository.save(visa);

                    MotifTransfert motifTransfert=motifTransfertRepository.findFirstByOrderByIdAsc().orElseGet(()->{MotifTransfert m=new MotifTransfert();m.setLibelle("CREATION DEMANDE");return motifTransfertRepository.save(m);});

                    PasseportVisa pv=new PasseportVisa();
                    pv.setPasseport(passeport);
                    pv.setVisa(visa);
                    pv.setDateAssociation(java.time.LocalDate.now());
                    pv.setMotifTransfert(motifTransfert);
                    pv.setDateCreation(LocalDateTime.now());
                    passeportVisaRepository.save(pv);

                    if(piecesPresentes!=null&&!piecesPresentes.isEmpty()){
                    for(Integer id:piecesPresentes){
                    PieceAFournir piece=new PieceAFournir();
                    piece.setDemande(demande);
                    piece.setTypeDocument(typeDocumentRepository.findById(id).orElseThrow(()->new ValidationException("Erreur doc",List.of("Doc inexistant "+id))));
                    piece.setPresent(Boolean.TRUE);
                    piece.setValide(modeUploadTermine);
                    piece.setScanComplete(false);  // ✓ TOUJOURS false à la création, sera marquée true APRÈS les uploads réels
                    piece.setDateScanComplete(null);
                    piece.setDateDepot(LocalDateTime.now());
                    piece.setDateModification(LocalDateTime.now());
                    pieceAFournirRepository.save(piece);
                    }}

                    validation.setDemandeId(demande.getId());
                    return validation;

                    }catch(DataIntegrityViolationException e){
                    throw new ValidationException("Erreur SQL",List.of("Contrainte violée",extractMostSpecificMessage(e)));
                    }}

                    private String extractMostSpecificMessage(Throwable t){
                    while(t.getCause()!=null)t=t.getCause();
                    String m=t.getMessage();
                    return(m==null||m.isBlank())?"(indisponible)":m.replace('\n',' ').replace('\r',' ').trim();
                    }

                    private ValidationErrorDTO failure(String m,String e){return buildFailure(m,List.of(e));}

                    private ValidationErrorDTO buildFailure(String m,List<String> e){
                    ValidationErrorDTO v=new ValidationErrorDTO(false,m);
                    v.setErrors(e);
                    return v;
                    }
                    }
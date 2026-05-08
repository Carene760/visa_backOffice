package com.teamlead.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Exception.ValidationException;
import com.teamlead.Model.Decision;
import com.teamlead.Model.DecisionDocument;
import com.teamlead.Model.Demande;
import com.teamlead.Model.DocumentScan;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Repository.DecisionDocumentRepository;
import com.teamlead.Repository.DocumentScanRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Repository.DecisionRepository;

@Service
public class DocumentScanService {

    @Autowired
    private DocumentScanRepository documentScanRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private DecisionRepository decisionRepository;

    @Autowired
    private DecisionDocumentRepository decisionDocumentRepository;

    @Autowired
    private DemandeStatusService demandeStatusService;

    @Autowired
    private DocumentScanValidationService documentScanValidationService;

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.max.size:10485760}")
    private long maxFileSize;

    @Value("${upload.allowed.types:pdf,jpg,jpeg,png,gif}")
    private String allowedTypes;

    /**
     * Valide un fichier uploadé
     */
    public ValidationErrorDTO validerFichier(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            return new ValidationErrorDTO(false, "Fichier vide");
        }

        // Vérifier la taille
        if (fichier.getSize() > maxFileSize) {
            long maxMB = maxFileSize / (1024 * 1024);
            return new ValidationErrorDTO(false, "Erreur: Taille du fichier dépasse " + maxMB + "MB");
        }

        // Vérifier l'extension
        String originalName = fichier.getOriginalFilename();
        if (originalName == null || !isValidFileType(originalName)) {
            return new ValidationErrorDTO(false, "Erreur: Format non autorisé. Types acceptés: " + allowedTypes);
        }

        return new ValidationErrorDTO(true, "Fichier valide");
    }

    /**
     * Vérifie si le type de fichier est autorisé
     */
    private boolean isValidFileType(String nomFichier) {
        String[] types = allowedTypes.split(",");
        String extension = nomFichier.substring(nomFichier.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.asList(types).stream().anyMatch(t -> t.trim().equalsIgnoreCase(extension));
    }

    /**
     * Sauvegarde un fichier uploadé
     */
    @Transactional
    public ValidationErrorDTO sauvegarderFichier(MultipartFile fichier, Integer idPiece, Integer numeroPage) {
        try {
            // Vérifier que la pièce existe
            PieceAFournir piece = pieceAFournirRepository.findById(idPiece)
                    .orElseThrow(() -> new ValidationException("Erreur: Pièce non trouvée",
                            List.of("La pièce " + idPiece + " n'existe pas")));

            Demande demande = piece.getDemande();
            if (demande != null && demande.getStatutDemande() != null
                && "SCAN_TERMINE".equalsIgnoreCase(demande.getStatutDemande().getLibelle())) {
            return new ValidationErrorDTO(false,
                "Upload bloqué: la demande est déjà au statut SCAN_TERMINE.");
            }

            // Valider le fichier
            ValidationErrorDTO validation = validerFichier(fichier);
            if (!validation.isSuccess()) {
                return validation;
            }

            // Une seule pièce = un seul scan actif; on remplace l'existant si besoin
            List<DocumentScan> scansExistants = documentScanRepository.findByIdPieceAFournir(idPiece);
            for (DocumentScan scanExistant : scansExistants) {
                Path ancienChemin = Paths.get(scanExistant.getCheminFichier());
                if (Files.exists(ancienChemin)) {
                    Files.delete(ancienChemin);
                }
                documentScanRepository.delete(scanExistant);
            }

            // Créer le répertoire s'il n'existe pas
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Générer un chemin unique
            String fileName = UUID.randomUUID().toString() + "_" + fichier.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // Sauvegarder le fichier
            Files.write(filePath, fichier.getBytes());

            // Créer l'enregistrement DocumentScan
            DocumentScan scan = new DocumentScan();
            scan.setPieceAFournir(piece);
            // Lier au demande via la pièce
            if (piece.getDemande() != null) {
                scan.setDemande(piece.getDemande());
            }
            scan.setCheminFichier(filePath.toString());
            scan.setNomFichier(fichier.getOriginalFilename());
            scan.setTypeMime(fichier.getContentType());
            scan.setTailleFichier((int) fichier.getSize());
            scan.setNumeroPage(numeroPage);
            scan.setDateUpload(LocalDateTime.now());
            scan.setDateModification(LocalDateTime.now());

            documentScanRepository.save(scan);

            piece.setPresent(Boolean.TRUE);
            piece.setScanComplete(Boolean.TRUE);
            piece.setDateScanComplete(LocalDateTime.now());
            piece.setDateModification(LocalDateTime.now());
            pieceAFournirRepository.save(piece);

            finaliserSiDossierCompletEtEnModeUpload(demande);

            ValidationErrorDTO result = new ValidationErrorDTO(true, "Fichier uploadé avec succès");
            result.setDocumentScanId(scan.getId());
            return result;

        } catch (IOException e) {
            return new ValidationErrorDTO(false, "Erreur serveur lors de l'upload: " + e.getMessage());
        } catch (Exception e) {
            return new ValidationErrorDTO(false, "Erreur inattendue: " + e.getMessage());
        }
    }

    /**
     * Supprime un scan de document
     */
    @Transactional
    public ValidationErrorDTO supprimerFichierScan(Integer idDocumentScan) {
        try {
            DocumentScan scan = documentScanRepository.findById(idDocumentScan)
                    .orElseThrow(() -> new ValidationException("Erreur: Scan non trouvé",
                            List.of("Le scan " + idDocumentScan + " n'existe pas")));

            Demande demande = scan.getDemande() != null ? scan.getDemande()
                : (scan.getPieceAFournir() != null ? scan.getPieceAFournir().getDemande() : null);
            if (demande != null && demande.getStatutDemande() != null
                && "SCAN_TERMINE".equalsIgnoreCase(demande.getStatutDemande().getLibelle())) {
            return new ValidationErrorDTO(false,
                "Suppression bloquée: la demande est déjà au statut SCAN_TERMINE. Utilisez une réouverture du dossier pour corriger un scan.");
            }

            // Supprimer le fichier physique
            Path filePath = Paths.get(scan.getCheminFichier());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Supprimer l'enregistrement
            documentScanRepository.deleteById(idDocumentScan);

            PieceAFournir piece = scan.getPieceAFournir();
            if (piece != null) {
                Integer remaining = documentScanRepository.countByIdPieceAFournir(piece.getId());
                boolean hasRemaining = remaining != null && remaining > 0;
                piece.setPresent(hasRemaining);
                piece.setScanComplete(hasRemaining);
                piece.setDateScanComplete(hasRemaining ? LocalDateTime.now() : null);
                piece.setDateModification(LocalDateTime.now());
                pieceAFournirRepository.save(piece);
            }

            return new ValidationErrorDTO(true, "Fichier supprimé avec succès");

        } catch (IOException e) {
            return new ValidationErrorDTO(false, "Erreur lors de la suppression du fichier: " + e.getMessage());
        } catch (Exception e) {
            return new ValidationErrorDTO(false, "Erreur inattendue: " + e.getMessage());
        }
    }

    /**
     * Liste tous les scans d'une pièce
     */
    public List<DocumentScan> listerScansPourPiece(Integer idPiece) {
        return documentScanRepository.findByIdPieceAFournir(idPiece);
    }

    /**
     * Liste tous les scans d'une demande
     */
    public List<DocumentScan> listerScansPourDemande(Integer idDemande) {
        return documentScanRepository.findByIdDemande(idDemande);
    }

    /**
     * Compte les scans d'une pièce
     */
    public Integer compterScansPourPiece(Integer idPiece) {
        return documentScanRepository.countByIdPieceAFournir(idPiece);
    }

    /**
     * Finalise automatiquement le dossier après upload pour les demandes sans données antérieures en mode "uploader".
     */
    private void finaliserSiDossierCompletEtEnModeUpload(Demande demande) {
        if (demande == null || !Boolean.TRUE.equals(demande.getSansDonneesAnterieures())) {
            return;
        }
        if (!"uploader".equalsIgnoreCase(demande.getModeSansDonneesAnterieures())) {
            return;
        }
        if (demande.getStatutDemande() != null && "SCAN_TERMINE".equalsIgnoreCase(demande.getStatutDemande().getLibelle())) {
            return;
        }

        String validation = documentScanValidationService.validerAvantTransitionScanTermine(demande.getId());
        if (validation != null && !validation.isBlank()) {
            return;
        }

        ValidationErrorDTO transition = demandeStatusService.transitionnerVersScanTermine(demande.getId());
        if (transition != null && transition.isSuccess()) {
            DecisionDocument decision = new DecisionDocument();
                                 Decision decisionEntity = decisionRepository.findByLibelle("Approuvee")
                                .orElseThrow(() -> new ValidationException("Erreur système", List.of("Valeur 'Approuve' introuvable dans la table decision")));

            decision.setDemande(demande);
            decision.setTypeDecision("MANUAL_REVIEW");
            decision.setDecision(decisionEntity);
            decision.setCriteresAppliques("Dossier complet après upload pour une demande sans données antérieures.");
            decision.setDecisionAutomatique(true);
            decision.setDateDecision(LocalDateTime.now());
            decisionDocumentRepository.save(decision);
        }
    }
}

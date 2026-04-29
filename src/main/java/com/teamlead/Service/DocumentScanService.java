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
import com.teamlead.Model.DocumentScan;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Repository.DocumentScanRepository;
import com.teamlead.Repository.PieceAFournirRepository;

@Service
public class DocumentScanService {

    @Autowired
    private DocumentScanRepository documentScanRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Value("${upload.dir:/uploads/documents}")
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

            // Valider le fichier
            ValidationErrorDTO validation = validerFichier(fichier);
            if (!validation.isSuccess()) {
                return validation;
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
            scan.setCheminFichier(filePath.toString());
            scan.setNomFichier(fichier.getOriginalFilename());
            scan.setTypeMime(fichier.getContentType());
            scan.setTailleFichier((int) fichier.getSize());
            scan.setNumeroPage(numeroPage);
            scan.setDateUpload(LocalDateTime.now());
            scan.setDateModification(LocalDateTime.now());

            documentScanRepository.save(scan);

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

            // Supprimer le fichier physique
            Path filePath = Paths.get(scan.getCheminFichier());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Supprimer l'enregistrement
            documentScanRepository.deleteById(idDocumentScan);

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
}

package com.teamlead.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.Model.Demande;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Model.TypeDocument;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DocumentScanRepository;
import com.teamlead.Repository.PieceAFournirRepository;

@Service
public class DocumentScanValidationService {

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private DocumentScanRepository documentScanRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    /**
     * Vérifie tous les documents obligatoires ont au moins 1 scan
     */
    public List<String> verifierTousLesObligatoiresScannes(Integer idDemande) {
        List<String> manquants = new ArrayList<>();

        List<PieceAFournir> pieces = pieceAFournirRepository.findByDemandeId(idDemande);

        for (PieceAFournir piece : pieces) {
            TypeDocument typeDoc = piece.getTypeDocument();
            boolean pieceAutoCompletee = Boolean.TRUE.equals(piece.getScanComplete());
            
            // Vérifier si c'est obligatoire
            if (typeDoc != null && isDocumentObligatoire(typeDoc)) {
                boolean hasScans = documentScanRepository.existsByIdPieceAFournir(piece.getId());
                if (!hasScans && !pieceAutoCompletee) {
                    manquants.add(typeDoc.getLibelle() != null ? typeDoc.getLibelle() : "Document sans libellé");
                }
            }
        }

        return manquants;
    }

    /**
     * Vérifie la plétude du dossier (complet/partiel/vide)
     */
    public String verifierCompleteudeDossierScan(Integer idDemande) {
        List<PieceAFournir> pieces = pieceAFournirRepository.findByDemandeId(idDemande);

        int totalObligatoires = 0;
        int scannedObligatoires = 0;
        int totalOptionnels = 0;
        int scannedOptionnels = 0;

        for (PieceAFournir piece : pieces) {
            TypeDocument typeDoc = piece.getTypeDocument();
            boolean hasScans = documentScanRepository.existsByIdPieceAFournir(piece.getId())
                    || Boolean.TRUE.equals(piece.getScanComplete());

            if (typeDoc != null && isDocumentObligatoire(typeDoc)) {
                totalObligatoires++;
                if (hasScans) {
                    scannedObligatoires++;
                }
            } else {
                totalOptionnels++;
                if (hasScans) {
                    scannedOptionnels++;
                }
            }
        }

        // Détermine le statut
        if (scannedObligatoires == totalObligatoires && scannedOptionnels == totalOptionnels) {
            return "COMPLET";
        } else if (scannedObligatoires > 0 || scannedOptionnels > 0) {
            return "PARTIEL";
        } else {
            return "VIDE";
        }
    }

    /**
     * Valide avant transition vers SCAN_TERMINE
     * Prérequis:
     * - Le statut doit être PHOTO_SIGNATURE_TERMINE
     * - Tous les documents obligatoires doivent être scannés
     */
    public String validerAvantTransitionScanTermine(Integer idDemande) {
        Demande demande = demandeRepository.findById(idDemande).orElse(null);
        if (demande == null) {
            return "Erreur: Demande introuvable.";
        }

        String statutCourant = demande.getStatutDemande() != null
                ? demande.getStatutDemande().getLibelle()
                : "INCONNU";
        
        // Accepter DOSSIER_CREE ou PHOTO_SIGNATURE_TERMINE - peu importe l'ordre
        if (!("DOSSIER_CREE".equalsIgnoreCase(statutCourant) 
                || "PHOTO_SIGNATURE_TERMINE".equalsIgnoreCase(statutCourant))) {
            return "Erreur: Transition vers SCAN_TERMINE impossible. Statut actuel: " + statutCourant + ".";
        }

        List<String> manquants = verifierTousLesObligatoiresScannes(idDemande);

        if (!manquants.isEmpty()) {
            return "Dossier(s) à fournir manquant(s): " + String.join(", ", manquants);
        }

        return "";
    }

    /**
     * Détermine si un document est obligatoire
     * Par défaut, tous les documents sont obligatoires sauf indication contraire
     */
    private boolean isDocumentObligatoire(TypeDocument typeDoc) {
        // À adapter selon votre logique métier
        // Pour l'instant, on considère tous les documents comme obligatoires
        return true;
    }

    /**
     * Retourne un résumé de la plétude
     */
    public String getResumePlétude(Integer idDemande) {
        List<PieceAFournir> pieces = pieceAFournirRepository.findByDemandeId(idDemande);

        int totalObligatoires = 0;
        int scannedObligatoires = 0;
        int totalOptionnels = 0;
        int scannedOptionnels = 0;

        for (PieceAFournir piece : pieces) {
            TypeDocument typeDoc = piece.getTypeDocument();
            boolean hasScans = documentScanRepository.existsByIdPieceAFournir(piece.getId())
                    || Boolean.TRUE.equals(piece.getScanComplete());

            if (typeDoc != null && isDocumentObligatoire(typeDoc)) {
                totalObligatoires++;
                if (hasScans) {
                    scannedObligatoires++;
                }
            } else {
                totalOptionnels++;
                if (hasScans) {
                    scannedOptionnels++;
                }
            }
        }

        return String.format("Obligatoires: %d/%d, Optionnels: %d/%d", 
            scannedObligatoires, totalObligatoires, scannedOptionnels, totalOptionnels);
    }
}


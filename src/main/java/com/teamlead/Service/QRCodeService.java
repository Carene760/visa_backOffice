package com.teamlead.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.teamlead.DTO.QRCodeDTO;
import com.teamlead.Model.Demande;
import com.teamlead.Repository.DemandeRepository;

/**
 * Service pour la génération et gestion des QR Codes
 */
@Service
public class QRCodeService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Value("${app.base.url:http://localhost:8085}")
    private String baseUrl;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    
    @Value("${app.frontend.ngrok.url:}")
    private String frontendNgrokUrl;

    /**
     * Génère un QR Code pour une demande
     */
    public QRCodeDTO generateQRCode(Integer demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée: " + demandeId));

        // Générer un token de suivi unique
        String trackingToken = UUID.randomUUID().toString();
        
        // Construire l'URL de suivi (pointe vers le frontend Vue)
        // Utiliser ngrok si configuré, sinon utiliser l'URL locale
        String baseUrlForQR = frontendNgrokUrl != null && !frontendNgrokUrl.trim().isEmpty() 
            ? frontendNgrokUrl 
            : frontendUrl;
        // Utiliser l'ID numérique directement, pas le format DEMANDE-XXXXXX
        String trackingUrl = baseUrlForQR + "/track/" + demandeId;

        try {
            // Générer le QR Code
            String qrCodeBase64 = generateQRCodeImage(trackingUrl);

            // Sauvegarder dans la demande
            demande.setTrackingToken(trackingToken);
            demande.setQrCodeUrl(trackingUrl);
            demande.setQrCodeData(qrCodeBase64);
            demande.setQrCodeGenerated(true);
            demande.setDateGenerationQrCode(java.time.LocalDateTime.now());
            demandeRepository.save(demande);

            // Retourner le DTO
            return new QRCodeDTO(
                demandeId,
                trackingToken,
                trackingUrl,
                qrCodeBase64,
                true,
                "QR Code généré avec succès"
            );

        } catch (WriterException e) {
            throw new RuntimeException("Erreur lors de la génération du QR Code: " + e.getMessage());
        }
    }

    /**
     * Génère l'image QR Code et retourne en Base64
     */
    private String generateQRCodeImage(String text) throws WriterException {
        int width = 300;
        int height = 300;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
            byte[] pngData = out.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(pngData);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'encodage en base64: " + e.getMessage());
        }
    }

    /**
     * Formater le numéro de demande (ex: DEMANDE-000123)
     */
    private String formatNumeroDemande(Integer id) {
        return "DEMANDE-" + String.format("%06d", id);
    }

    /**
     * Récupérer le QR Code d'une demande
     */
    public QRCodeDTO getQRCode(Integer demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée: " + demandeId));

        if (demande.getQrCodeGenerated() == null || !demande.getQrCodeGenerated()) {
            // Générer automatiquement si pas encore créé
            return generateQRCode(demandeId);
        }

        return new QRCodeDTO(
            demandeId,
            demande.getTrackingToken(),
            demande.getQrCodeUrl(),
            demande.getQrCodeData(),
            true,
            "QR Code récupéré avec succès"
        );
    }
}

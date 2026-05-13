package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.DTO.ValidationErrorDTO;
import com.teamlead.Exception.ValidationException;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.DocumentSignature;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DemandeurRepository;
import com.teamlead.Repository.DocumentSignatureRepository;

@Service
public class PhotoSignatureService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandeurRepository demandeurRepository;

    @Autowired
    private DocumentSignatureRepository documentSignatureRepository;

    @Autowired
    private DemandeStatusService demandeStatusService;

    @Transactional
    public ValidationErrorDTO enregistrerPhotoWebcam(Integer demandeId, String photoBase64) {
        return enregistrerImageDemandeur(demandeId, photoBase64, "PHOTO_WEBCAM");
    }

    @Transactional
    public ValidationErrorDTO enregistrerSignatureSouris(Integer demandeId, String signatureBase64) {
        return enregistrerImageDemandeur(demandeId, signatureBase64, "SIGNATURE_SOURIS");
    }

    private ValidationErrorDTO enregistrerImageDemandeur(Integer demandeId, String base64Value, String typeDocument) {
        if (demandeId == null) {
            throw new ValidationException("Demande introuvable", List.of("Identifiant de demande manquant."));
        }
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ValidationException("Demande introuvable",
                        List.of("Aucune demande trouvée pour l'identifiant " + demandeId)));

        Demandeur demandeur = demande.getDemandeur();
        if (demandeur == null) {
            throw new ValidationException("Demandeur introuvable",
                    List.of("La demande ne contient pas de demandeur associé."));
        }

        byte[] imageBytes = decodeBase64Image(base64Value);

        // Enregistrer ou mettre à jour le document signature
        DocumentSignature docSignature = documentSignatureRepository
                .findByDemandeurIdAndDemandeIdAndTypeDocument(demandeur.getId(), demandeId, typeDocument)
                .orElse(new DocumentSignature(demandeur, demande, typeDocument, imageBytes));

        docSignature.setContenu(imageBytes);
        docSignature.setDateModification(LocalDateTime.now());
        if (typeDocument.equals("PHOTO_WEBCAM")) {
            docSignature.setTypeMime("image/jpeg");
            docSignature.setNomFichier("photo_webcam.jpg");
        } else {
            docSignature.setTypeMime("image/png");
            docSignature.setNomFichier("signature_souris.png");
        }

        documentSignatureRepository.save(docSignature);

        ValidationErrorDTO response = new ValidationErrorDTO(true,
                typeDocument.equals("PHOTO_WEBCAM") ? "Photo enregistrée avec succès." : "Signature enregistrée avec succès.");
        response.setDemandeId(demandeId);

        // Vérifier si les 2 documents sont présents
        boolean photoPresente = documentSignatureRepository.existsPhotoWebcam(demandeId);
        boolean signaturePresente = documentSignatureRepository.existsSignatureSouris(demandeId);

        if (photoPresente && signaturePresente) {
            ValidationErrorDTO transition = demandeStatusService.transitionnerVersPhotoSignatureTerminee(demandeId);
            if (!transition.isSuccess()) {
                return transition;
            }
            response.setMessage("Photo et signature enregistrées. Étape photo/signature terminée.");
            response.setRedirectUrl("/demande/" + demandeId + "/upload-scanner");
        }

        return response;
    }

    private byte[] decodeBase64Image(String base64Value) {
        if (base64Value == null || base64Value.isBlank()) {
            throw new IllegalArgumentException("La valeur base64 est vide.");
        }

        String payload = base64Value.trim();
        int commaIndex = payload.indexOf(',');
        if (payload.startsWith("data:") && commaIndex >= 0) {
            payload = payload.substring(commaIndex + 1);
        }

        return Base64.getDecoder().decode(payload);
    }
}

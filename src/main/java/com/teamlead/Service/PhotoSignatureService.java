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
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DemandeurRepository;

@Service
public class PhotoSignatureService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandeurRepository demandeurRepository;

    @Autowired
    private DemandeStatusService demandeStatusService;

    @Transactional
    public ValidationErrorDTO enregistrerPhotoWebcam(Integer demandeId, String photoBase64) {
        return enregistrerImageDemandeur(demandeId, photoBase64, true);
    }

    @Transactional
    public ValidationErrorDTO enregistrerSignatureSouris(Integer demandeId, String signatureBase64) {
        return enregistrerImageDemandeur(demandeId, signatureBase64, false);
    }

    private ValidationErrorDTO enregistrerImageDemandeur(Integer demandeId, String base64Value, boolean photo) {
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
        if (photo) {
            demandeur.setPhotoWebcam(imageBytes);
            demandeur.setPhotoTerminee(Boolean.TRUE);
        } else {
            demandeur.setSignatureSouris(imageBytes);
            demandeur.setSignatureTerminee(Boolean.TRUE);
        }

        demandeur.setDateModification(LocalDateTime.now());
        demandeurRepository.save(demandeur);

        ValidationErrorDTO response = new ValidationErrorDTO(true,
                photo ? "Photo enregistrée avec succès." : "Signature enregistrée avec succès.");
        response.setDemandeId(demandeId);

        boolean photoTerminee = Boolean.TRUE.equals(demandeur.getPhotoTerminee());
        boolean signatureTerminee = Boolean.TRUE.equals(demandeur.getSignatureTerminee());
        if (photoTerminee && signatureTerminee) {
            ValidationErrorDTO transition = demandeStatusService.transitionnerVersPhotoSignatureTermine(demandeId);
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

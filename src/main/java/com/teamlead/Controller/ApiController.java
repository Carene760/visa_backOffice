package com.teamlead.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.teamlead.DTO.DemandeDetailDTO;
import com.teamlead.DTO.DemandeSearchRequestDTO;
import com.teamlead.DTO.DemandeSearchResponseDTO;
import com.teamlead.DTO.QRCodeDTO;
import com.teamlead.Service.DemandeSearchService;
import com.teamlead.Service.QRCodeService;

/**
 * Contrôleur REST API Sprint 4
 * Endpoints publics pour la recherche et le suivi de demandes
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private final DemandeSearchService demandeSearchService;
    private final QRCodeService qrCodeService;

    public ApiController(DemandeSearchService demandeSearchService, QRCodeService qrCodeService) {
        this.demandeSearchService = demandeSearchService;
        this.qrCodeService = qrCodeService;
    }

    /**
     * POST /api/search
     * Recherche une demande par numéro ou passeport (auto-détection)
     */
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody DemandeSearchRequestDTO request) {
        try {
            DemandeSearchResponseDTO response = demandeSearchService.search(request.getSearchCriteria());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    /**
     * GET /api/demandes/{id}
     * Récupère les détails d'une demande
     */
    @GetMapping("/demandes/{id}")
    public ResponseEntity<?> getDemandeDetail(@PathVariable Integer id) {
        try {
            DemandeSearchResponseDTO response = demandeSearchService.search(id.toString());
            DemandeDetailDTO demande = response.getDemandeEnEvidence();
            return ResponseEntity.ok(demande);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Demande non trouvée"));
        }
    }

    /**
     * POST /api/demandes/{id}/qrcode
     * Génère un QR Code pour une demande
     */
    @PostMapping("/demandes/{id}/qrcode")
    public ResponseEntity<?> generateQRCode(@PathVariable Integer id) {
        try {
            QRCodeDTO qrCode = qrCodeService.generateQRCode(id);
            return ResponseEntity.ok(qrCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * GET /api/demandes/track/{numeroDemande}
     * Suivi public d'une demande par QR Code (endpoint pour scanner QR)
     */
    @GetMapping("/demandes/track/{numeroDemande}")
    public ResponseEntity<?> trackDemande(@PathVariable String numeroDemande) {
        try {
            DemandeSearchResponseDTO response = demandeSearchService.search(numeroDemande);
            DemandeDetailDTO demande = response.getDemandeEnEvidence();
            if (demande != null) {
                return ResponseEntity.ok(demande);
            }
            // Si pas en évidence, prendre la première des autres
            if (!response.getAutresDemandesRelatives().isEmpty()) {
                return ResponseEntity.ok(response.getAutresDemandesRelatives().get(0));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Demande non trouvée"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Demande non trouvée: " + e.getMessage()));
        }
    }

    /**
     * GET /api/health
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "API Visa Sprint 4 est en ligne",
                "timestamp", System.currentTimeMillis()
        ));
    }
}

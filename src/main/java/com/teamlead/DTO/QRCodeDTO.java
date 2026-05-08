package com.teamlead.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la génération du QR Code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeDTO {
    private Integer demandeId;
    private String trackingToken;      // UUID du tracking
    private String qrCodeUrl;          // URL publique à encoder
    private String qrCodeBase64;       // Image PNG en base64
    private Boolean success;
    private String message;
}

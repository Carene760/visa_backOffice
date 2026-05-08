package com.teamlead.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour afficher les détails complets d'une demande
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDetailDTO {
    private Integer demandeId;
    private String numeroDemande;          // Format: DEMANDE-000123
    private Integer demandeurId;
    private String nomDemandeur;
    private String prenomDemandeur;
    private String numeroPasport;
    
    // Informations complètes du demandeur
    private String nomNaissance;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String email;
    private String telephone;
    private String adresseMadagascar;
    private String nationalite;
    private String situationMatrimoniale;
    
    private String typeDemande;            // NOUVEAU_TITRE, DUPLICATA, etc.
    private String sousTypeDemande;
    private String typeMotif;
    
    private String statutActuel;
    private String typeDecision;           // Type de décision: APPROUVEE, REJETEE, MANUAL_REVIEW, etc.
    private Boolean avecDonneesAnterieures;
    private Boolean sansDonneesAnterieures;
    
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;
    private LocalDateTime dateModification;
    
    // QR Code
    private String qrCodeUrl;
    private String qrCodeBase64;           // Image en base64
    private Boolean qrCodeGenerated;
    private LocalDateTime dateGenerationQrCode;
    
    // Historique complet des statuts
    private List<HistoriqueStatutDTO> historiqueStatuts;
    
    // Journal d'activité du demandeur
    private List<JournalActiviteDTO> journalActivites;
}

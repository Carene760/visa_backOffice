package com.teamlead.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "demande")
@Getter
@Setter
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur", nullable = false, foreignKey = @ForeignKey(name = "demande_id_demandeur_fkey"))
    private Demandeur demandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_motif", nullable = false, foreignKey = @ForeignKey(name = "demande_id_type_motif_fkey"))
    private TypeMotif typeMotif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_demande", nullable = false, foreignKey = @ForeignKey(name = "demande_id_type_demande_fkey"))
    private TypeDemande typeDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_demande", nullable = false, foreignKey = @ForeignKey(name = "demande_id_statut_demande_fkey"))
    private StatutDemande statutDemande;

    @Column(name = "avec_donnees_anterieures")
    private Boolean avecDonneesAnterieures = Boolean.FALSE;

    @Column(name = "sans_donnees_anterieures")
    private Boolean sansDonneesAnterieures = Boolean.FALSE;

    @Column(name = "date_demande")
    private LocalDateTime dateDemande;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Sprint 4 - QR Code
    @Column(name = "qr_code_data", columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(name = "qr_code_generated")
    private Boolean qrCodeGenerated = Boolean.FALSE;

    @Column(name = "date_generation_qr_code")
    private LocalDateTime dateGenerationQrCode;

    @Column(name = "tracking_token")
    private String trackingToken;

    @Column(name = "mode_sans_donnees_anterieures", length = 30)
    private String modeSansDonneesAnterieures;

    @Column(name = "type_decision")
    private Integer typeDecision;
}
 

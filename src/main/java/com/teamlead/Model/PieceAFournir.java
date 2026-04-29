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
@Table(name = "piece_a_fournir")
@Getter
@Setter
public class PieceAFournir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false, foreignKey = @ForeignKey(name = "piece_a_fournir_id_demande_fkey"))
    private Demande demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_document", nullable = false, foreignKey = @ForeignKey(name = "piece_a_fournir_id_type_document_fkey"))
    private TypeDocument typeDocument;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(nullable = false)
    private Boolean present = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean valide = Boolean.FALSE;

    @Column(name = "date_depot")
    private LocalDateTime dateDepot;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "scan_complete")
    private Boolean scanComplete = Boolean.FALSE;

    @Column(name = "date_scan_complete")
    private LocalDateTime dateScanComplete;
}

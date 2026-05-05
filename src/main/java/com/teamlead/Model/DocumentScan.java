package com.teamlead.Model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "document_scan")
@Getter
@Setter
public class DocumentScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_piece_a_fournir", nullable = false, foreignKey = @ForeignKey(name = "document_scan_id_piece_fkey"))
    private PieceAFournir pieceAFournir;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", foreignKey = @ForeignKey(name = "document_scan_id_demande_fkey"))
    private Demande demande;

    @Column(name = "chemin_fichier", nullable = false, length = 500)
    private String cheminFichier;

    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    @Column(name = "type_mime", length = 50)
    private String typeMime;

    @Column(name = "taille_fichier")
    private Integer tailleFichier;

    @Column(name = "numero_page")
    private Integer numeroPage;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
}

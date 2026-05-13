package com.teamlead.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_signature", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_demandeur", "id_demande", "type_document"})
})
public class DocumentSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur", nullable = false)
    private Demandeur demandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande demande;

    @Column(name = "type_document", nullable = false, length = 50)
    private String typeDocument; // 'PHOTO_WEBCAM' ou 'SIGNATURE_SOURIS'

    @Lob
    @Column(name = "contenu", nullable = false)
    private byte[] contenu;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(name = "type_mime", length = 50)
    private String typeMime;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructors
    public DocumentSignature() {
    }

    public DocumentSignature(Demandeur demandeur, Demande demande, String typeDocument, byte[] contenu) {
        this.demandeur = demandeur;
        this.demande = demande;
        this.typeDocument = typeDocument;
        this.contenu = contenu;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Demandeur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Demandeur demandeur) {
        this.demandeur = demandeur;
    }

    public Demande getDemande() {
        return demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public byte[] getContenu() {
        return contenu;
    }

    public void setContenu(byte[] contenu) {
        this.contenu = contenu;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
}

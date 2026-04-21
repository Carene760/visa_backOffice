package com.teamlead.Model;

import java.time.LocalDate;
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

    @Column(name = "date_demande")
    private LocalDateTime dateDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_motif", foreignKey = @ForeignKey(name = "demande_id_type_motif_fkey"))
    private TypeMotif typeMotif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_demande", foreignKey = @ForeignKey(name = "demande_id_type_demande_fkey"))
    private TypeDemande typeDemande;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "date_expiration_demande")
    private LocalDate dateExpirationDemande;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
}
    private LocalDateTime dateModification;
}

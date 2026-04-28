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
@Table(name = "visa")
@Getter
@Setter
public class Visa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_visa", nullable = false, foreignKey = @ForeignKey(name = "visa_id_type_visa_fkey"))
    private TypeVisa typeVisa;

    @Column(name = "date_entree", nullable = false)
    private LocalDate dateEntree;

    @Column(name = "lieu_entree", length = 150)
    private String lieuEntree;

    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false, foreignKey = @ForeignKey(name = "visa_id_demande_fkey"))
    private Demande demande;

    @Column(name = "id_demandeur", nullable = false)
    private Integer idDemandeur;

    @Column(name = "date_emission")
    private LocalDateTime dateEmission;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
}

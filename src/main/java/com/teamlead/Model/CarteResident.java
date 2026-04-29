package com.teamlead.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

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
@Table(name = "carte_resident")
@Getter
@Setter
public class CarteResident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String reference;

    @Column(name = "date_entree", nullable = false)
    private LocalDate dateEntree;

    @Column(name = "lieu_entree", length = 150)
    private String lieuEntree;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false, foreignKey = @ForeignKey(name = "carte_resident_id_demande_fkey"))
    private Demande demande;

    @ManyToOne(fetch = FetchType.LAZY)

    @ManyToOne
    @JoinColumn(name = "id_demande", nullable = false, foreignKey = @ForeignKey(name = "carte_resident_id_demande_fkey"))
    private Demande demande;

    @ManyToOne
    @JoinColumn(name = "id_passeport", nullable = false, foreignKey = @ForeignKey(name = "carte_resident_id_passeport_fkey"))
    private Passeport passeport;

    @Column(name = "date_emission")
    private LocalDateTime dateEmission;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
}

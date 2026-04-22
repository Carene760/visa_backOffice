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
@Table(name = "visa_transformable")
@Getter
@Setter
public class VisaTransformable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur", nullable = false, foreignKey = @ForeignKey(name = "visa_transformable_id_demandeur_fkey"))
    private Demandeur demandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_passeport", nullable = false, foreignKey = @ForeignKey(name = "visa_transformable_id_passeport_fkey"))
    private Passeport passeport;

    @Column(nullable = false, length = 100)
    private String reference;

    @Column(name = "date_entree", nullable = false)
    private LocalDate dateEntree;

    @Column(name = "lieu_entree", length = 150)
    private String lieuEntree;

    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
}

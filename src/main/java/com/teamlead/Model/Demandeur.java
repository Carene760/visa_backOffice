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
@Table(name = "demandeur")
@Getter
@Setter
public class Demandeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(name = "nom_naissance", length = 100)
    private String nomNaissance;

    @Column(length = 150, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String telephone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nationalite", nullable = false, foreignKey = @ForeignKey(name = "demandeur_id_nationalite_fkey"))
    private Nationalite nationalite;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 150)
    private String lieuNaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situation_matrimoniale", foreignKey = @ForeignKey(name = "demandeur_id_situation_matrimoniale_fkey"))
    private SituationMatrimoniale situationMatrimoniale;

    @Column(name = "adresse_madagascar", nullable = false, length = 255)
    private String adresseMadagascar;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
}

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
@Table(name = "recherche_antecendent")
@Getter
@Setter
public class RechercheAntecedent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false, foreignKey = @ForeignKey(name = "recherche_antecendent_id_demande_fkey"))
    private Demande demande;

    @Column(name = "critere_recherche", nullable = false, length = 50)
    private String critereRecherche;

    @Column(name = "valeur_recherchee", nullable = false, length = 100)
    private String valeurRecherchee;

    @Column(nullable = false)
    private Boolean trouve = Boolean.FALSE;

    @Column(name = "date_recherche")
    private LocalDateTime dateRecherche;
}

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "decision_document")
@Getter
@Setter
public class DecisionDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_demande", nullable = false, foreignKey = @ForeignKey(name = "decision_document_id_demande_fkey"))
    private Demande demande;

    @Column(name = "type_decision", nullable = false, length = 50)
    private String typeDecision;

    @Column(name = "criteres_appliques")
    private String criteresAppliques;

    @Column(name = "decision_automatique")
    private Boolean decisionAutomatique;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;

    @Column(name = "raison_rejet", length = 500)
    private String raisonRejet;
}

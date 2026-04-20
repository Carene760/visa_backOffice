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
@Table(name = "passeport_visa")
@Getter
@Setter
public class PasseportVisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_passeport", nullable = false, foreignKey = @ForeignKey(name = "passeport_visa_id_passeport_fkey"))
    private Passeport passeport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visa", nullable = false, foreignKey = @ForeignKey(name = "passeport_visa_id_visa_fkey"))
    private Visa visa;

    @Column(name = "date_association", nullable = false)
    private LocalDate dateAssociation;

    @Column(name = "date_transfert")
    private LocalDate dateTransfert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motif_transfert", nullable = false, foreignKey = @ForeignKey(name = "passeport_visa_id_motif_transfert_fkey"))
    private MotifTransfert motifTransfert;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
}

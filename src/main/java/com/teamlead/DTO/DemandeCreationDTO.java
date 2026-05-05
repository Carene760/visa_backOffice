package com.teamlead.DTO;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeCreationDTO {
    
    // Informations du demandeur
    private String nom;
    private String prenom;
    private String nomNaissance;
    private String email;
    private String telephone;
    private Integer idNationalite;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private Integer idSituationMatrimoniale;
    private String adresseMadagascar;
    
    // Informations du passeport
    private String numeroPasseport;
    private LocalDate dateDelivrancePasseport;
    private LocalDate dateExpirationPasseport;
    
    // Informations du visa transformable
    private String referenceVisa;
    private LocalDate dateEntreeVisa;
    private String lieuEntreeVisa;
    private LocalDate dateExpirationVisa;
    
    // Informations de la demande
    private Integer idTypeDemande;
    private Integer idTypeMotif;
    private Integer idTypeVisa;
    
    // Pièces à fournir (liste des IDs de documents présents)
    private List<Integer> piecesPresentes;
    
    // Informations Sprint 2 (optionnelles)
    private Integer idSousTypeDemande;
    private Integer idDemandeSource;
    private Boolean avecDonneesAnterieures;
    private Boolean sansdonneesAnterieures; // Checkbox pour NOUVEAU_TITRE variante "sans données antérieures"
}

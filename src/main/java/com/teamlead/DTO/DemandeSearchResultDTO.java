package com.teamlead.DTO;

import com.teamlead.Model.Demande;
import com.teamlead.Model.Passeport;
import com.teamlead.Model.Visa;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Model.Demandeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeSearchResultDTO {
    
    private Integer id;
    private Demandeur demandeur;
    private Passeport passeport;
    private Visa visa;
    private StatutDemande statut;
    
    // Constructeur à partir d'une Demande
    public static DemandeSearchResultDTO fromDemande(Demande demande, Passeport passeport, Visa visa) {
        DemandeSearchResultDTO dto = new DemandeSearchResultDTO();
        dto.setId(demande.getId());
        dto.setDemandeur(demande.getDemandeur());
        dto.setPasseport(passeport);
        dto.setVisa(visa);
        dto.setStatut(demande.getStatutDemande());
        return dto;
    }
}

package com.teamlead.DTO;

import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RechercheAntecedentsDTO {
    private String type;                    // VISA, PASSEPORT, CARTE_RESIDENT
    private String valeur;                  // Le numéro/référence cherché
    private boolean trouve;                 // true si encontré
    private Integer idDemandeur;            // ID du demandeur trouvé
    private Integer idDemande;              // ID de la dernière demande
    private Demandeur demandeur;            // Objet demandeur complet
    private Demande demandeSource;          // Demande source trouvée
    
    public RechercheAntecedentsDTO() {
        this.trouve = false;
    }
    
    public RechercheAntecedentsDTO(String type, String valeur, boolean trouve) {
        this.type = type;
        this.valeur = valeur;
        this.trouve = trouve;
    }
}

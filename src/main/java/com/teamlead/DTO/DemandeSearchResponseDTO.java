package com.teamlead.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse de recherche
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeSearchResponseDTO {
    
    // Demande mise en évidence (si numéro de demande entré)
    private DemandeDetailDTO demandeEnEvidence;
    
    // Toutes les autres demandes du même demandeur
    private List<DemandeDetailDTO> autresDemandesRelatives;
    
    // Statistiques
    private Integer totalDemandesFound;
    private Integer nombreDemandesApprouvees;
    private Integer nombreDemandesRefusees;
    private Integer nombreDemandesEnCours;
    
    // Type de recherche effectuée
    private String typeRecherche;  // "NUMERO_DEMANDE" ou "NUMERO_PASSEPORT"
    private String critereRecherche;
}

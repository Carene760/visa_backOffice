package com.teamlead.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les requêtes de recherche
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeSearchRequestDTO {
    private String searchCriteria;  // Numéro demande ou passeport
}

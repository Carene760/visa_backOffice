package com.teamlead.DTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'historique d'un statut
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueStatutDTO {
    private Integer historiqueId;
    private String statut;                 // Libellé du statut
    private LocalDateTime dateStatut;      // Date du changement
}

package com.teamlead.DTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'activité dans le journal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalActiviteDTO {
    private Integer id;
    private String typeEvenement;    // Code du type d'événement
    private LocalDateTime dateAction;
}

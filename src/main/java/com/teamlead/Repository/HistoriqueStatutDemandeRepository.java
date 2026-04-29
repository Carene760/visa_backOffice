package com.teamlead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.HistoriqueStatutDemande;

@Repository
public interface HistoriqueStatutDemandeRepository extends JpaRepository<HistoriqueStatutDemande, Integer> {
    /**
     * Récupère l'historique de statuts pour une demande (ordre croissant)
     */
    List<HistoriqueStatutDemande> findByDemandeIdOrderByDateChangementAsc(Integer demandeId);
    
    /**
     * Récupère l'historique de statuts pour une demande (ordre décroissant, dernier en premier)
     */
    List<HistoriqueStatutDemande> findByDemandeIdOrderByDateChangementDesc(Integer demandeId);
}

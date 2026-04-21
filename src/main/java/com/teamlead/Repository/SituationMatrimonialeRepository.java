package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.SituationMatrimoniale;

@Repository
public interface SituationMatrimonialeRepository extends JpaRepository<SituationMatrimoniale, Integer> {
    
    /**
     * Récupère une situation matrimoniale par son libellé
     */
    SituationMatrimoniale findByLibelle(String libelle);
}

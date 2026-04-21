package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.Demandeur;

@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Integer> {
    
    /**
     * Recherche un demandeur par son numéro de téléphone
     */
    Demandeur findByTelephone(String telephone);
    
    /**
     * Recherche un demandeur par son email
     */
    Demandeur findByEmail(String email);
}

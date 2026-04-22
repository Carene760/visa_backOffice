package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.StatutDemande;

@Repository
public interface StatutDemandeRepository extends JpaRepository<StatutDemande, Integer> {
    StatutDemande findByLibelle(String libelle);
}

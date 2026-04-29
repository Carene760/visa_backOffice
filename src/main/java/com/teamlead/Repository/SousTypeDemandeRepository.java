package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.SousTypeDemande;

@Repository
public interface SousTypeDemandeRepository extends JpaRepository<SousTypeDemande, Integer> {
    SousTypeDemande findByLibelle(String libelle);
}

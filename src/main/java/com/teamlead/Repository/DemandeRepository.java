package com.teamlead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    List<Demande> findByDemandeur(Demandeur demandeur);
}

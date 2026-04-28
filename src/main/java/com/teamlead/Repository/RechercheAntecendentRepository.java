package com.teamlead.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.RechercheAntecendent;

@Repository
public interface RechercheAntecendentRepository extends JpaRepository<RechercheAntecendent, Integer> {
    List<RechercheAntecendent> findByIdDemande(Integer idDemande);
    List<RechercheAntecendent> findByIdDemandeAndTrouve(Integer idDemande, Boolean trouve);
}

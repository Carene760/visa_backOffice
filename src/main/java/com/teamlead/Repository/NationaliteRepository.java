package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.Nationalite;

@Repository
public interface NationaliteRepository extends JpaRepository<Nationalite, Integer> {
    /**
     * Récupère une nationalité par son libellé
     */
    Nationalite findByLibelle(String libelle);
}

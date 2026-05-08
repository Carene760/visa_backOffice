package com.teamlead.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.JournalActivite;

@Repository
public interface JournalActiviteRepository extends JpaRepository<JournalActivite, Integer> {
    // Récupère toutes les activités d'un demandeur, triées par date décroissante
    List<JournalActivite> findByDemandeurIdOrderByDateActionDesc(Integer demandeurId);
}

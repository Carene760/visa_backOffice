package com.teamlead.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    List<Demande> findByDemandeur(Demandeur demandeur);
    
    // Find demandes by demandeur and typeDemande libelle (used to locate generated bases)
    List<Demande> findByDemandeurAndTypeDemandeLibelle(Demandeur demandeur, String libelle);
    
    // Sprint 4 - API REST search methods
    List<Demande> findByDemandeurIdOrderByDateDemandeDesc(Integer demandeurId);
    
    Optional<Demande> findByTrackingToken(String trackingToken);

    @EntityGraph(attributePaths = {"demandeur", "statutDemande"})
    @Query("SELECT d FROM Demande d WHERE d.id = :id")
    Optional<Demande> findByIdWithRelations(@Param("id") Integer id);
}

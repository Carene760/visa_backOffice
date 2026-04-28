package com.teamlead.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.DecisionDocument;

@Repository
public interface DecisionDocumentRepository extends JpaRepository<DecisionDocument, Integer> {
    List<DecisionDocument> findByIdDemande(Integer idDemande);
    DecisionDocument findFirstByIdDemandeOrderByDateDecisionDesc(Integer idDemande);
}

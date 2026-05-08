package com.teamlead.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.DecisionDocument;
import com.teamlead.Model.Demande;

@Repository
public interface DecisionDocumentRepository extends JpaRepository<DecisionDocument, Integer> {
    
    List<DecisionDocument> findByDemande(Demande demande);
    
    List<DecisionDocument> findByDemandeAndTypeDecision(Demande demande, String typeDecision);

    DecisionDocument findTopByDemandeOrderByIdDesc(Demande demande);

}

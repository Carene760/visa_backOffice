package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.Model.DecisionDocument;
import com.teamlead.Model.Demande;
import com.teamlead.Repository.DecisionDocumentRepository;

@Service
public class DecisionDocumentService {

    @Autowired
    private DecisionDocumentRepository repository;

    /**
     * Enregistre une décision pour une demande
     * 
     * @param demande La demande
     * @param typeDecision Type de décision (APPROUVEE, REJETEE, EN_ATTENTE, SUSPENDUE, etc.)
     * @param criteresAppliques Description des critères appliqués (JSON ou texte)
     * @param decisionAutomatique true si auto-générée, false si manuelle
     * @return DecisionDocument créée
     */
    @Transactional
    public DecisionDocument enregistrerDecision(Demande demande, String typeDecision, String criteresAppliques,
            Boolean decisionAutomatique) {
        DecisionDocument decision = new DecisionDocument();
        decision.setDemande(demande);
        decision.setTypeDecision(typeDecision);
        decision.setCriteresAppliques(criteresAppliques);
        decision.setDecisionAutomatique(decisionAutomatique != null ? decisionAutomatique : false);
        decision.setDateDecision(LocalDateTime.now());
        return repository.save(decision);
    }

    /**
     * Enregistre un rejet de demande avec raison
     * 
     * @param demande La demande
     * @param raison Raison du rejet
     * @return DecisionDocument créée
     */
    @Transactional
    public DecisionDocument enregistrerRejet(Demande demande, String raison) {
        DecisionDocument decision = new DecisionDocument();
        decision.setDemande(demande);
        decision.setTypeDecision("REJETEE");
        decision.setRaisonRejet(raison);
        decision.setDateDecision(LocalDateTime.now());
        decision.setDecisionAutomatique(false);
        return repository.save(decision);
    }

    /**
     * Enregistre une approbation automatique
     * 
     * @param demande La demande
     * @param criteresAppliques Description des critères qui ont mené à l'approbation
     * @return DecisionDocument créée
     */
    @Transactional
    public DecisionDocument enregistrerApprobationAuto(Demande demande, String criteresAppliques) {
        return enregistrerDecision(demande, "APPROUVEE", criteresAppliques, true);
    }

    /**
     * Récupère toutes les décisions d'une demande
     * 
     * @param demande La demande
     * @return Liste des décisions
     */
    public List<DecisionDocument> obtenirDecisionsDemande(Demande demande) {
        return repository.findByDemande(demande);
    }

    /**
     * Récupère les décisions d'un type spécifique pour une demande
     * 
     * @param demande La demande
     * @param typeDecision Type de décision à rechercher
     * @return Liste des décisions du type spécifié
     */
    public List<DecisionDocument> obtenirDecisionsParType(Demande demande, String typeDecision) {
        return repository.findByDemandeAndTypeDecision(demande, typeDecision);
    }

    /**
     * Récupère la décision la plus récente d'une demande
     * 
     * @param demande La demande
     * @return Optional contenant la décision la plus récente
     */
    public Optional<DecisionDocument> obtenirDernierDecision(Demande demande) {
        List<DecisionDocument> decisions = obtenirDecisionsDemande(demande);
        if (decisions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(decisions.get(decisions.size() - 1));
    }

    /**
     * Vérifie si une demande a été approuvée
     * 
     * @param demande La demande
     * @return true si la dernière décision est APPROUVEE
     */
    public boolean estApprouvee(Demande demande) {
        Optional<DecisionDocument> decision = obtenirDernierDecision(demande);
        return decision.isPresent() && "APPROUVEE".equals(decision.get().getTypeDecision());
    }

    /**
     * Vérifie si une demande a été rejetée
     * 
     * @param demande La demande
     * @return true si la dernière décision est REJETEE
     */
    public boolean estRejetee(Demande demande) {
        Optional<DecisionDocument> decision = obtenirDernierDecision(demande);
        return decision.isPresent() && "REJETEE".equals(decision.get().getTypeDecision());
    }
}

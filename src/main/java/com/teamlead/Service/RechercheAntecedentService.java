package com.teamlead.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.Model.Demande;
import com.teamlead.Model.RechercheAntecedent;
import com.teamlead.Repository.RechercheAntecedentRepository;

@Service
public class RechercheAntecedentService {

    @Autowired
    private RechercheAntecedentRepository repository;

    /**
     * Crée une recherche d'antécédent pour une demande
     * 
     * @param demande La demande associée
     * @param critere Le critère de recherche (IDENTITE, PASSPORT, PREVIOUS_VISA, etc.)
     * @param valeur La valeur recherchée (nom, numéro passport, etc.)
     * @param trouve Résultat de la recherche
     * @return RechercheAntecedent créée
     */
    @Transactional
    public RechercheAntecedent creerRecherche(Demande demande, String critere, String valeur, Boolean trouve) {
        RechercheAntecedent recherche = new RechercheAntecedent();
        recherche.setDemande(demande);
        recherche.setCritereRecherche(critere);
        recherche.setValeurRecherchee(valeur);
        recherche.setTrouve(trouve != null ? trouve : Boolean.FALSE);
        recherche.setDateRecherche(LocalDateTime.now());
        return repository.save(recherche);
    }

    /**
     * Récupère toutes les recherches effectuées pour une demande
     * 
     * @param demande La demande
     * @return Liste des recherches d'antécédent
     */
    public List<RechercheAntecedent> obtenirRecherchesDemande(Demande demande) {
        return repository.findByDemande(demande);
    }

    /**
     * Récupère les recherches d'un type spécifique pour une demande
     * 
     * @param demande La demande
     * @param critere Le critère de recherche
     * @return Liste des recherches correspondant au critère
     */
    public List<RechercheAntecedent> obtenirRechercheParCritere(Demande demande, String critere) {
        return repository.findByDemandeAndCritereRecherche(demande, critere);
    }

    /**
     * Vérifie si une antécédent a été trouvée pour une demande
     * 
     * @param demande La demande
     * @param critere Le critère de recherche
     * @return true si au moins une entrée positive existe
     */
    public boolean antecedentTrouve(Demande demande, String critere) {
        List<RechercheAntecedent> recherches = obtenirRechercheParCritere(demande, critere);
        return recherches.stream().anyMatch(r -> r.getTrouve());
    }
}

package com.teamlead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.Demande;
import com.teamlead.Model.RechercheAntecedent;

@Repository
public interface RechercheAntecedentRepository extends JpaRepository<RechercheAntecedent, Integer> {
    
    List<RechercheAntecedent> findByDemande(Demande demande);
    
    List<RechercheAntecedent> findByDemandeAndCritereRecherche(Demande demande, String critereRecherche);

    
}

package com.teamlead.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamlead.Model.StatutDemande;
import com.teamlead.Repository.StatutDemandeRepository;
import java.util.List;

@Service
public class StatutDemandeService {

    @Autowired
    private StatutDemandeRepository statutDemandeRepository;

    public List<StatutDemande> findAll() {
        return statutDemandeRepository.findAll();
    }

    public StatutDemande findById(Integer id) {
        return statutDemandeRepository.findById(id).orElse(null);
    }

    public StatutDemande findByLibelle(String libelle) {
        return statutDemandeRepository.findByLibelle(libelle);
    }
}

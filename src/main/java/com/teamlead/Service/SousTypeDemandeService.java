package com.teamlead.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.Model.SousTypeDemande;
import com.teamlead.Repository.SousTypeDemandeRepository;

@Service
public class SousTypeDemandeService {

    @Autowired
    private SousTypeDemandeRepository repository;

    /**
     * Récupère tous les sous-types de demande
     * 
     * @return Liste de tous les sous-types
     */
    public List<SousTypeDemande> findAll() {
        return repository.findAll();
    }

    /**
     * Récupère un sous-type par ID
     * 
     * @param id L'ID du sous-type
     * @return Optional contenant le sous-type si trouvé
     */
    public Optional<SousTypeDemande> findById(Integer id) {
        return repository.findById(id);
    }

    /**
     * Récupère un sous-type par libelle
     * 
     * @param libelle Le libelle du sous-type
     * @return SousTypeDemande trouvé, null si inexistant
     */
    public SousTypeDemande findByLibelle(String libelle) {
        return repository.findByLibelle(libelle);
    }
}

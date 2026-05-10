package com.teamlead.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.Model.CarteResident;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Visa;
import com.teamlead.Repository.CarteResidentRepository;

@Service
public class CarteResidentService {

    @Autowired
    private CarteResidentRepository repository;

    /**
     * Crée une carte de résident pour une demande
     * 
     * @param demande La demande associée
     * @param visa Le visa associé
     * @param reference Référence unique de la carte
     * @param dateEntree Date d'entrée
     * @param dateExpiration Date d'expiration
     * @return CarteResident créée
     */
    @Transactional
    public CarteResident creerCarteResident(Demande demande, Visa visa, String reference,
            LocalDate dateEntree, LocalDate dateExpiration) {
        CarteResident carte = new CarteResident();
        carte.setDemande(demande);
        carte.setVisa(visa);
        carte.setReference(reference);
        carte.setDateEntree(dateEntree);
        carte.setLieuEntree(demande.getDemandeur().getAdresseMadagascar());
        carte.setDateExpiration(dateExpiration);
        carte.setDateEmission(LocalDateTime.now());
        carte.setDateModification(LocalDateTime.now());
        return repository.save(carte);
    }

    /**
     * Récupère une carte résident par id en chargeant la demande associée
     */
    public CarteResident obtenirParIdAvecDemande(Integer id) {
        return repository.findByIdWithDemande(id);
    }

    /**
     * Récupère une carte de résident par sa référence
     * 
     * @param reference La référence unique
     * @return Optional contenant la carte si trouvée
     */
    public Optional<CarteResident> obtenirParReference(String reference) {
        return Optional.ofNullable(repository.findByReference(reference));
    }

    /**
     * Vérifie si une référence de carte existe déjà
     * 
     * @param reference La référence à vérifier
     * @return true si la référence existe
     */
    public boolean referenceExiste(String reference) {
        return repository.findByReference(reference) != null;
    }

    /**
     * Met à jour la date d'expiration d'une carte
     * 
     * @param carteId ID de la carte
     * @param nouvelleExpiration Nouvelle date d'expiration
     * @return CarteResident mise à jour
     */
    @Transactional
    public CarteResident mettreAJourExpiration(Integer carteId, LocalDate nouvelleExpiration) {
        Optional<CarteResident> carteOpt = repository.findById(carteId);
        if (carteOpt.isPresent()) {
            CarteResident carte = carteOpt.get();
            carte.setDateExpiration(nouvelleExpiration);
            carte.setDateModification(LocalDateTime.now());
            return repository.save(carte);
        }
        return null;
    }
}

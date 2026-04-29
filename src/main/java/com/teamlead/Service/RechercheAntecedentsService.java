package com.teamlead.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.DTO.RechercheAntecedentsDTO;
import com.teamlead.Model.CarteResident;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Passeport;
import com.teamlead.Model.Visa;
import com.teamlead.Repository.CarteResidentRepository;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.PasseportRepository;
import com.teamlead.Repository.VisaRepository;

@Service
public class RechercheAntecedentsService {
    
    @Autowired private VisaRepository visaRepository;
    @Autowired private PasseportRepository passeportRepository;
    @Autowired private CarteResidentRepository carteResidentRepository;
    @Autowired private DemandeRepository demandeRepository;
    
    /**
     * Recherche les antécédents par numéro de visa
     * Retourne les informations du demandeur associé et ses demandes
     */
    public RechercheAntecedentsDTO rechercherParVisa(String numeroVisa) {
        if (numeroVisa == null || numeroVisa.isBlank()) {
            return null;
        }
        
        Visa visa = visaRepository.findByReference(numeroVisa);
        if (visa == null) {
            return null;
        }
        
        RechercheAntecedentsDTO result = new RechercheAntecedentsDTO();
        result.setType("VISA");
        result.setValeur(numeroVisa);
        
        // Récupérer la demande associée
        Demande demande = visa.getDemande();
        if (demande != null) {
            result.setIdDemande(demande.getId());
            result.setIdDemandeur(demande.getDemandeur().getId());
            result.setDemandeur(demande.getDemandeur());
            result.setDemandeSource(demande);
            result.setTrouve(true);
        }
        
        return result;
    }
    
    /**
     * Recherche les antécédents par numéro de passeport
     * Retourne les informations du demandeur et ses demandes
     */
    public RechercheAntecedentsDTO rechercherParPasseport(String numeroPasseport) {
        if (numeroPasseport == null || numeroPasseport.isBlank()) {
            return null;
        }
        
        Passeport passeport = passeportRepository.findByNumero(numeroPasseport);
        if (passeport == null) {
            return null;
        }
        
        RechercheAntecedentsDTO result = new RechercheAntecedentsDTO();
        result.setType("PASSEPORT");
        result.setValeur(numeroPasseport);
        result.setIdDemandeur(passeport.getDemandeur().getId());
        result.setDemandeur(passeport.getDemandeur());
        result.setTrouve(true);
        
        // Récupérer la dernière demande associée au demandeur
        List<Demande> demandes = demandeRepository.findByDemandeur(passeport.getDemandeur());
        if (!demandes.isEmpty()) {
            Demande derniere = demandes.get(0);
            result.setIdDemande(derniere.getId());
            result.setDemandeSource(derniere);
        }
        
        return result;
    }
    
    /**
     * Recherche les antécédents par numéro de carte résident
     * Retourne les informations du demandeur et ses demandes
     */
    public RechercheAntecedentsDTO rechercherParCarteResident(String referenceCarteResident) {
        if (referenceCarteResident == null || referenceCarteResident.isBlank()) {
            return null;
        }
        
        CarteResident carte = carteResidentRepository.findByReference(referenceCarteResident);
        if (carte == null) {
            return null;
        }
        
        RechercheAntecedentsDTO result = new RechercheAntecedentsDTO();
        result.setType("CARTE_RESIDENT");
        result.setValeur(referenceCarteResident);
        
        // Récupérer la demande associée
        Demande demande = carte.getDemande();
        if (demande != null) {
            result.setIdDemande(demande.getId());
            result.setIdDemandeur(demande.getDemandeur().getId());
            result.setDemandeur(demande.getDemandeur());
            result.setDemandeSource(demande);
            result.setTrouve(true);
        }
        
        return result;
    }
    
    /**
     * Recherche multi-critères - vérifie tous les critères possibles
     * Retourne le premier résultat trouvé
     */
    public RechercheAntecedentsDTO rechercherMultiCriteres(
            String numeroVisa,
            String numeroPasseport,
            String referenceCarteResident) {
        
        // Chercher par visa d'abord
        if (numeroVisa != null && !numeroVisa.isBlank()) {
            RechercheAntecedentsDTO result = rechercherParVisa(numeroVisa);
            if (result != null && result.isTrouve()) {
                return result;
            }
        }
        
        // Chercher par passeport
        if (numeroPasseport != null && !numeroPasseport.isBlank()) {
            RechercheAntecedentsDTO result = rechercherParPasseport(numeroPasseport);
            if (result != null && result.isTrouve()) {
                return result;
            }
        }
        
        // Chercher par carte résident
        if (referenceCarteResident != null && !referenceCarteResident.isBlank()) {
            RechercheAntecedentsDTO result = rechercherParCarteResident(referenceCarteResident);
            if (result != null && result.isTrouve()) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Vérifie si un demandeur existe dans les antécédents
     */
    public boolean demandeurExiste(Integer idDemandeur) {
        return idDemandeur != null && idDemandeur > 0;
    }
}

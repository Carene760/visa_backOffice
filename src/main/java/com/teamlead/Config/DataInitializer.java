package com.teamlead.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.teamlead.Model.StatutDemande;
import com.teamlead.Repository.StatutDemandeRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StatutDemandeRepository statutDemandeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialiser les statuts par défaut
        initializeStatuts();
    }

    private void initializeStatuts() {
        List<String> statutsParDefaut = List.of(
            "dossier cree",
            "dossier complet",
            "en_traitement",
            "approuve",
            "rejete"
        );

        for (String libelle : statutsParDefaut) {
            StatutDemande existant = statutDemandeRepository.findByLibelle(libelle);
            if (existant == null) {
                StatutDemande statut = new StatutDemande();
                statut.setLibelle(libelle);
                statutDemandeRepository.save(statut);
                System.out.println("Statut créé: " + libelle);
            }
        }
    }
}

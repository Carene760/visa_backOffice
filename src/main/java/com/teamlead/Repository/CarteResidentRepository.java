package com.teamlead.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.CarteResident;

@Repository
public interface CarteResidentRepository extends JpaRepository<CarteResident, Integer> {

    
    CarteResident findByReference(String reference);

    // @Query("SELECT cr FROM CarteResident cr WHERE cr.demande.id = :idDemande")
    // List<CarteResident> findByIdDemande(Integer idDemande);
}



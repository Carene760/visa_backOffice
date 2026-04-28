package com.teamlead.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.CarteResident;

@Repository
public interface CarteResidentRepository extends JpaRepository<CarteResident, Integer> {
    List<CarteResident> findByIdDemande(Integer idDemande);
    CarteResident findByReference(String reference);
}


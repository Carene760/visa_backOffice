package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.teamlead.Model.Passeport;
import com.teamlead.Model.Demandeur;

@Repository
public interface PasseportRepository extends JpaRepository<Passeport, Integer> {
     Passeport findByNumero(String numero);
     Passeport findFirstByDemandeurOrderByDateCreationDesc(Demandeur demandeur);
}

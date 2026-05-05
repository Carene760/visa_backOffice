package com.teamlead.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.Demande;
import com.teamlead.Model.TypeVisa;
import com.teamlead.Model.Visa;

@Repository
public interface VisaRepository extends JpaRepository<Visa, String> {
    Visa findByReference(String reference);
    Visa findFirstByDemandeOrderByDateEmissionDesc(Demande demande);
    
    @Query("SELECT v FROM Visa v WHERE v.demande.demandeur.id = :demandeurId AND v.demande.statutDemande.libelle = 'VISA_APPROUVEE'")
    List<Visa> findVisaApprouveeByDemandeur(@Param("demandeurId") Integer demandeurId);
}

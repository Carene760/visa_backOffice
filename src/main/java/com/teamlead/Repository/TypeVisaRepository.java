package com.teamlead.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.TypeVisa;

@Repository
public interface TypeVisaRepository extends JpaRepository<TypeVisa, Integer> {
    TypeVisa findByLibelle(String libelle);
    Optional<TypeVisa> findFirstByLibelleIgnoreCase(String libelle);
    @Query("SELECT t FROM TypeVisa t WHERE UPPER(TRIM(t.libelle)) = UPPER(TRIM(:libelle))")
    Optional<TypeVisa> findNormalizedByLibelle(@Param("libelle") String libelle);
}

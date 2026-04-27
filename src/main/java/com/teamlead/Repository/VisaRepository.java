package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.TypeVisa;
import com.teamlead.Model.Visa;

@Repository
public interface VisaRepository extends JpaRepository<Visa, String> {
    Visa findByReference(String reference);
}

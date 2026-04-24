package com.teamlead.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.MotifTransfert;

@Repository
public interface MotifTransfertRepository extends JpaRepository<MotifTransfert, Integer> {
    Optional<MotifTransfert> findFirstByOrderByIdAsc();
}

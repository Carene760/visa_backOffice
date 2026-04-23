package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.teamlead.Model.Passeport;

@Repository
public interface PasseportRepository extends JpaRepository<Passeport, Integer> {
}

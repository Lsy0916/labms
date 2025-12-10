package org.example.labms.repository;

import org.example.labms.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Integer> {
    
    @Query("SELECT t FROM Term t WHERE t.startDate <= :currentDate AND t.endDate >= :currentDate")
    Optional<Term> findCurrentTerm(@Param("currentDate") LocalDate currentDate);
    
    Optional<Term> findBySemesterId(String semesterId);
    
    List<Term> findAllByOrderByStartDateDesc();
}
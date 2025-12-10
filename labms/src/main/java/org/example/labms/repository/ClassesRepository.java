package org.example.labms.repository;

import org.example.labms.model.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, Integer> {
    Optional<Classes> findByClassId(String classId);
    Optional<Classes> findByName(String name);
}
package org.example.labms.repository;

import org.example.labms.model.ComputerLab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ComputerLabRepository extends JpaRepository<ComputerLab, Integer> {
    Optional<ComputerLab> findByRoomId(String roomId);
    
    @Modifying
    @Query("DELETE FROM ComputerLab c WHERE c.roomId = :roomId")
    void deleteByRoomId(@Param("roomId") String roomId);
}
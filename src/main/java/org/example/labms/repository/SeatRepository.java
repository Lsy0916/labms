package org.example.labms.repository;

import org.example.labms.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    
    List<Seat> findByRoomId(String roomId);
    
    Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId);
    
    @Modifying
    @Query("DELETE FROM Seat s WHERE s.roomId = :roomId AND s.id > :seatIdThreshold")
    void deleteByRoomIdAndIdGreaterThan(@Param("roomId") String roomId, @Param("seatIdThreshold") int seatIdThreshold);
    
    @Query("SELECT s FROM Seat s WHERE s.roomId = :roomId ORDER BY CAST(s.seatId AS INTEGER) DESC")
    List<Seat> findByRoomIdOrderBySeatIdDesc(@Param("roomId") String roomId);
    
    // 根据roomId删除座位信息
    @Modifying
    @Query("DELETE FROM Seat s WHERE s.roomId = :roomId")
    void deleteByRoomId(@Param("roomId") String roomId);
}
package org.example.labms.repository;

import org.example.labms.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId AND r.reservationDate = :date")
    List<Reservation> findByRoomIdAndReservationDate(@Param("roomId") String roomId, @Param("date") LocalDate date);

    // 使用 LocalTime 类型进行精确匹配
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId AND r.reservationDate = :date AND r.startTime = :startTime AND r.endTime = :endTime")
    List<Reservation> findByRoomIdAndReservationDateAndStartTimeAndEndTime(@Param("roomId") String roomId, 
                                                                          @Param("date") LocalDate date, 
                                                                          @Param("startTime") LocalTime startTime, 
                                                                          @Param("endTime") LocalTime endTime);
    
    // 根据roomId、日期和时间段查询预约信息
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId AND r.reservationDate = :date AND " +
           "r.startTime <= :endTime AND r.endTime >= :startTime AND " +
           "(r.status = '待确认' OR r.status = '已确认')")
    List<Reservation> findByRoomIdAndReservationDateAndTimeOverlap(@Param("roomId") String roomId, 
                                                                  @Param("date") LocalDate date,
                                                                  @Param("startTime") LocalTime startTime,
                                                                  @Param("endTime") LocalTime endTime);
    
    // 根据roomId、日期查询预约信息，并排除整个机房预约记录（seatId != "-1"）
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId AND r.reservationDate = :date AND r.seatId != '-1'")
    List<Reservation> findByRoomIdAndReservationDateExcludeWholeRoom(@Param("roomId") String roomId, @Param("date") LocalDate date);
    
    // 根据roomId、日期和时间段查询预约信息，并排除整个机房预约记录（seatId != "-1"）
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId AND r.reservationDate = :date AND " +
           "r.startTime <= :endTime AND r.endTime >= :startTime AND " +
           "(r.status = '待确认' OR r.status = '已确认') AND r.seatId != '-1'")
    List<Reservation> findByRoomIdAndReservationDateAndTimeOverlapExcludeWholeRoom(@Param("roomId") String roomId, 
                                                                                 @Param("date") LocalDate date,
                                                                                 @Param("startTime") LocalTime startTime,
                                                                                 @Param("endTime") LocalTime endTime);
    
    // 根据userId查找预约信息
    List<Reservation> findByUserId(String userId);
    
    // 根据reservationId和userId查找预约信息
    Optional<Reservation> findByReservationIdAndUserId(String reservationId, String userId);
    
    // 根据reservationId查找预约信息
    @Query("SELECT r FROM Reservation r WHERE r.reservationId = :reservationId")
    Optional<Reservation> findByReservationId(@Param("reservationId") String reservationId);
    
    // 查找特定状态的预约
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    // 查找某个日期之后的预约
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate >= :date")
    List<Reservation> findByReservationDateAfter(@Param("date") LocalDate date);
    
    // 根据用户ID和状态查找预约
    List<Reservation> findByUserIdAndStatus(String userId, Reservation.ReservationStatus status);
    
    // 根据roomId删除预约信息
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.roomId = :roomId")
    void deleteByRoomId(@Param("roomId") String roomId);
}
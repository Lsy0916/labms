package org.example.labms.service;

import org.example.labms.model.Reservation;
import org.example.labms.repository.ReservationRepository;
import org.example.labms.repository.SeatRepository;
import org.example.labms.model.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    // 定时任务：每小时检查一次过期预约
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void markExpiredReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        for (Reservation reservation : reservations) {
            // 检查预约是否已过期且状态不是已完成、已取消、已过期或已驳回
            if (reservation.getStatus() != Reservation.ReservationStatus.已完成 &&
                reservation.getStatus() != Reservation.ReservationStatus.已取消 &&
                reservation.getStatus() != Reservation.ReservationStatus.已过期 &&
                reservation.getStatus() != Reservation.ReservationStatus.已驳回) {
                
                LocalDateTime reservationDateTime = LocalDateTime.of(reservation.getReservationDate(), reservation.getEndTime());
                if (reservationDateTime.isBefore(now)) {
                    reservation.setStatus(Reservation.ReservationStatus.已过期);
                    reservationRepository.save(reservation);
                }
            }
        }
    }
    
    // 获取所有预约信息
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    public List<Reservation> getReservationsByRoomIdAndDate(String roomId, LocalDate reservationDate) {
        // 返回排除整个机房预约记录的结果，因为整个机房预约不应该影响对具体座位预约的查询
        return reservationRepository.findByRoomIdAndReservationDateExcludeWholeRoom(roomId, reservationDate);
    }
    
    public List<Reservation> getReservationsByRoomIdAndDateAndTime(String roomId, LocalDate reservationDate, String startTime, String endTime) {
        // 解析时间参数
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");
        LocalTime startLocalTime = LocalTime.parse(startTime, formatter);
        LocalTime endLocalTime = LocalTime.parse(endTime, formatter);
        
        // 返回排除整个机房预约记录的结果，因为整个机房预约不应该影响对具体座位预约的查询
        return reservationRepository.findByRoomIdAndReservationDateAndTimeOverlapExcludeWholeRoom(roomId, reservationDate, startLocalTime, endLocalTime);
    }
    
    // 根据userId查找预约记录
    public List<Reservation> getReservationsByUserId(String userId) {
        return reservationRepository.findByUserId(userId);
    }
    
    // 取消预约
    public boolean cancelReservation(String reservationId, String userId) {
        Optional<Reservation> reservationOptional = reservationRepository.findByReservationIdAndUserId(reservationId, userId);
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();
            
            // 检查预约状态是否允许取消
            if (reservation.getStatus() == Reservation.ReservationStatus.已完成) {
                // 已完成的预约不能取消
                return false;
            }
            
            if (reservation.getStatus() == Reservation.ReservationStatus.已取消) {
                // 已经取消的预约不能再次取消
                return false;
            }
            
            if (reservation.getStatus() == Reservation.ReservationStatus.已过期) {
                // 已过期的预约不能取消
                return false;
            }
            
            if (reservation.getStatus() == Reservation.ReservationStatus.已驳回) {
                // 已驳回的预约不能取消
                return false;
            }
            
            // 设置预约状态为已取消
            reservation.setStatus(Reservation.ReservationStatus.已取消);
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }
    
    // 更新预约状态
    public boolean updateReservationStatus(String reservationId, Reservation.ReservationStatus status) {
        Optional<Reservation> reservationOptional = reservationRepository.findByReservationId(reservationId);
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();
            
            // 检查状态转换是否合法
            if (reservation.getStatus() == Reservation.ReservationStatus.已取消 && 
                status != Reservation.ReservationStatus.已取消) {
                // 已取消的预约不能改为其他状态
                return false;
            }
            
            if (reservation.getStatus() == Reservation.ReservationStatus.已过期 && 
                status != Reservation.ReservationStatus.已过期) {
                // 已过期的预约不能改为其他状态
                return false;
            }
            
            if (reservation.getStatus() == Reservation.ReservationStatus.已完成 && 
                status != Reservation.ReservationStatus.已完成) {
                // 已完成的预约不能改为其他状态
                return false;
            }
            
            reservation.setStatus(status);
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }
    
    // 根据预约ID删除预约记录
    public boolean deleteReservationById(String reservationId) {
        Optional<Reservation> reservationOptional = reservationRepository.findByReservationId(reservationId);
        if (reservationOptional.isPresent()) {
            reservationRepository.delete(reservationOptional.get());
            return true;
        }
        return false;
    }
    
    public List<Reservation> createReservations(String roomId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime, 
                                              List<String> seatIds, String mode, String userId) {
        List<Reservation> createdReservations = new ArrayList<>();
        
        if ("single".equals(mode)) {
            // 单座位模式 - 只对指定座位进行预约
            for (String seatId : seatIds) {
                // 检查座位是否可用
                if (isSeatAvailableBySeatId(roomId, reservationDate, startTime, endTime, seatId)) {
                    Reservation reservation = createReservationBySeatId(roomId, seatId, userId, reservationDate, startTime, endTime);
                    createdReservations.add(reservation);
                }
            }
        } else if ("whole".equals(mode)) {
            // 整机房模式 - 检查整个机房是否可用后再创建预约
            if (isWholeRoomAvailable(roomId, reservationDate, startTime, endTime)) {
                Reservation reservation = createWholeRoomReservation(roomId, userId, reservationDate, startTime, endTime);
                createdReservations.add(reservation);
            }
        }
        
        return createdReservations;
    }
    
    // 创建整个机房的预约记录
    private Reservation createWholeRoomReservation(String roomId, String userId, LocalDate reservationDate, 
                                       LocalTime startTime, LocalTime endTime) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(generateReservationId());
        reservation.setRoomId(roomId);
        // 使用特殊值"-1"表示这是整个机房的预约，而不是特定座位的预约
        reservation.setSeatId("-1");
        reservation.setUserId(userId);
        reservation.setReservationDate(reservationDate);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(Reservation.ReservationStatus.待确认);
        
        return reservationRepository.save(reservation);
    }
    
    private boolean isSeatAvailableBySeatId(String roomId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime, String seatId) {
        // 先通过roomId和seatId找到座位
        List<Seat> seats = seatRepository.findByRoomId(roomId);
        Seat targetSeat = null;
        for (Seat seat : seats) {
            if (seatId.equals(seat.getSeatId())) {
                targetSeat = seat;
                break;
            }
        }
        
        if (targetSeat == null) {
            return false; // 未找到对应座位
        }
        
        return isSeatAvailableBySeatDatabaseId(roomId, reservationDate, startTime, endTime, targetSeat.getSeatId());
    }
    
    private Reservation createReservationBySeatId(String roomId, String seatId, String userId, LocalDate reservationDate, 
                                       LocalTime startTime, LocalTime endTime) {
        // 先通过roomId和seatId找到座位
        List<Seat> seats = seatRepository.findByRoomId(roomId);
        Seat targetSeat = null;
        for (Seat seat : seats) {
            if (seatId.equals(seat.getSeatId())) {
                targetSeat = seat;
                break;
            }
        }
        
        if (targetSeat == null) {
            return null; // 未找到对应座位
        }
        
        return createReservationBySeatDatabaseId(roomId, targetSeat.getSeatId(), userId, reservationDate, startTime, endTime);
    }
    
    private boolean isSeatAvailableBySeatDatabaseId(String roomId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime, String seatDatabaseId) {
        List<Reservation> overlappingReservations = reservationRepository.findByRoomIdAndReservationDateAndTimeOverlap(
                roomId, reservationDate, startTime, endTime);
        
        // 检查是否有重叠的预约
        for (Reservation reservation : overlappingReservations) {
            // 检查整个机房预约冲突（seatId为"-1"表示整个机房被预约）
            if ("-1".equals(reservation.getSeatId()) && 
                (reservation.getStatus() == Reservation.ReservationStatus.待确认 || 
                 reservation.getStatus() == Reservation.ReservationStatus.已确认 ||
                 reservation.getStatus() == Reservation.ReservationStatus.已驳回)) {
                return false; // 整个机房已被预约
            }
            
            // 检查特定座位预约冲突
            if (reservation.getSeatId().equals(seatDatabaseId) && 
                (reservation.getStatus() == Reservation.ReservationStatus.待确认 || 
                 reservation.getStatus() == Reservation.ReservationStatus.已确认 ||
                 reservation.getStatus() == Reservation.ReservationStatus.已驳回)) {
                return false; // 特定座位已被预约
            }
        }
        
        return true; // 座位可用
    }
    
    private Reservation createReservationBySeatDatabaseId(String roomId, String seatDatabaseId, String userId, LocalDate reservationDate,
                                       LocalTime startTime, LocalTime endTime) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(generateReservationId());
        reservation.setRoomId(roomId);
        reservation.setSeatId(seatDatabaseId);  // 使用座位的seatId而不是数据库id
        reservation.setUserId(userId);
        reservation.setReservationDate(reservationDate);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(Reservation.ReservationStatus.待确认);
        
        return reservationRepository.save(reservation);
    }
    
    private boolean isWholeRoomAvailable(String roomId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime) {
        List<Reservation> overlappingReservations = reservationRepository.findByRoomIdAndReservationDateAndTimeOverlap(
                roomId, reservationDate, startTime, endTime);
        
        // 检查是否有重叠的预约
        for (Reservation reservation : overlappingReservations) {
            // 任何重叠的预约都会导致整个机房不可用
            if (reservation.getStatus() == Reservation.ReservationStatus.待确认 || 
                reservation.getStatus() == Reservation.ReservationStatus.已确认 ||
                reservation.getStatus() == Reservation.ReservationStatus.已驳回) {
                return false; // 机房已被预约
            }
        }
        
        return true; // 机房可用
    }
    
    private String generateReservationId() {
        return "RES" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 根据机房ID删除所有预约信息
     * @param roomId 机房ID
     */
    @Transactional
    public void deleteReservationsByRoomId(String roomId) {
        reservationRepository.deleteByRoomId(roomId);
    }
}
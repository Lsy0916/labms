package org.example.labms.controller;

import org.example.labms.model.Reservation;
import org.example.labms.service.ReservationService;
import org.example.labms.dto.ReservationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;

    // 获取所有预约信息
    @GetMapping("/reservations/all")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // 获取指定教室指定日期的预约信息
    @GetMapping("/reservations")
    public List<Reservation> getReservationsByRoomIdAndDate(
            @RequestParam String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate) {
        return reservationService.getReservationsByRoomIdAndDate(roomId, reservationDate);
    }

    // 获取指定教室指定日期指定时间段的预约信息
    @GetMapping("/reservations/time")
    public List<Reservation> getReservationsByRoomIdAndDateAndTime(
            @RequestParam String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        // 现在可以接收 "8:20:00" 格式的时间参数
        return reservationService.getReservationsByRoomIdAndDateAndTime(roomId, reservationDate, startTime, endTime);
    }
    
    // 根据用户ID获取预约信息
    @GetMapping("/reservations/user")
    public List<Reservation> getReservationsByUserId(@RequestParam String userId) {
        return reservationService.getReservationsByUserId(userId);
    }
    
    // 取消预约
    @DeleteMapping("/reservations")
    public ResponseEntity<String> cancelReservation(
            @RequestParam String reservationId,
            @RequestParam String userId) {
        boolean success = reservationService.cancelReservation(reservationId, userId);
        if (success) {
            return ResponseEntity.ok("预约取消成功");
        } else {
            return ResponseEntity.badRequest().body("预约取消失败，未找到匹配的预约记录");
        }
    }
    
    // 更新预约状态
    @PutMapping("/reservations/status")
    public ResponseEntity<String> updateReservationStatus(
            @RequestParam String reservationId,
            @RequestParam Reservation.ReservationStatus status) {
        boolean success = reservationService.updateReservationStatus(reservationId, status);
        if (success) {
            return ResponseEntity.ok("预约状态更新成功");
        } else {
            return ResponseEntity.badRequest().body("预约状态更新失败，未找到匹配的预约记录");
        }
    }
    
    // 删除预约
    @DeleteMapping("/deleteReservation")
    public ResponseEntity<String> deleteReservation(@RequestParam String reservationId) {
        boolean success = reservationService.deleteReservationById(reservationId);
        if (success) {
            return ResponseEntity.ok("预约删除成功");
        } else {
            return ResponseEntity.badRequest().body("预约删除失败，未找到匹配的预约记录");
        }
    }
    
    // 创建新的预约 - 使用URL参数
    @PostMapping("/reservations")
    public List<Reservation> createReservations(
            @RequestParam String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) List<String> seatIds,  // 座位ID列表，整机房预约时传空数组或不传
            @RequestParam String mode,
            @RequestParam String userId) {
        
        // 参数校验
        if (roomId == null || roomId.isEmpty()) {
            throw new IllegalArgumentException("机房ID不能为空");
        }
        
        if (mode == null || (!"single".equals(mode) && !"whole".equals(mode))) {
            throw new IllegalArgumentException("预约模式必须是 'single' 或 'whole'");
        }
        
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 处理座位ID列表，默认为空列表
        if (seatIds == null) {
            seatIds = new ArrayList<>();
        }
        
        // 解析时间参数
        LocalTime startLocalTime;
        LocalTime endLocalTime;
        try {
            startLocalTime = LocalTime.parse(startTime);
            endLocalTime = LocalTime.parse(endTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间格式错误，请使用HH:mm:ss格式，如14:00:00");
        }
        
        // 校验时间逻辑
        if (startLocalTime.compareTo(endLocalTime) >= 0) {
            throw new IllegalArgumentException("开始时间必须早于结束时间");
        }
        
        return reservationService.createReservations(
                roomId,
                reservationDate,
                startLocalTime,
                endLocalTime,
                seatIds,
                mode,
                userId
        );
    }
}
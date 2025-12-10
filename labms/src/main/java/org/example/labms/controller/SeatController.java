package org.example.labms.controller;

import org.example.labms.model.Seat;
import org.example.labms.dto.SeatStatusDTO;
import org.example.labms.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SeatController {
    
    @Autowired
    private SeatService seatService;

    /**
     * 获取指定教室的座位信息
     * @param roomId 教室ID
     * @return 座位信息列表
     */
    @GetMapping("/seats")
    public List<Seat> getSeatsByRoomId(@RequestParam String roomId) {
        return seatService.getSeatsByRoomId(roomId);
    }

    /**
     * 获取指定教室指定日期的座位状态
     * @param roomId 教室ID
     * @param date 日期
     * @return 座位状态列表
     */
    @GetMapping("/seats/status")
    public List<SeatStatusDTO> getSeatStatusByRoomIdAndDateAndTime(
            @RequestParam String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        return seatService.getSeatStatusByRoomIdAndDateAndTime(roomId, date, startTime, endTime);
    }
    
    /**
     * 更新座位状态
     * @param roomId 机房ID
     * @param seatId 座位ID（单个）
     * @param seatIds 座位ID（批量）
     * @param status 新的状态
     * @return 更新后的座位对象
     */
    @PutMapping("/seats/status")
    public ResponseEntity<?> updateSeatStatus(
            @RequestParam String roomId,
            @RequestParam(required = false) String seatId,
            @RequestParam(required = false) List<String> seatIds,
            @RequestParam Seat.SeatStatus status) {
        try {
            if (seatIds != null && !seatIds.isEmpty()) {
                // 批量更新
                List<Seat> updatedSeats = seatService.updateSeatStatusBatch(roomId, seatIds, status);
                return ResponseEntity.ok(updatedSeats);
            } else if (seatId != null && !seatId.isEmpty()) {
                // 单个更新
                Seat updatedSeat = seatService.updateSeatStatus(roomId, seatId, status);
                return ResponseEntity.ok(updatedSeat);
            } else {
                return ResponseEntity.badRequest().body("必须提供seatId或seatIds参数");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
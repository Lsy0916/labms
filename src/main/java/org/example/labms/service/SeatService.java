package org.example.labms.service;

import org.example.labms.model.Seat;
import org.example.labms.model.Reservation;
import org.example.labms.repository.SeatRepository;
import org.example.labms.repository.ReservationRepository;
import org.example.labms.dto.SeatStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class SeatService {
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    public List<Seat> getSeatsByRoomId(String roomId) {
        return seatRepository.findByRoomId(roomId);
    }
    
    public List<SeatStatusDTO> getSeatStatusByRoomIdAndDateAndTime(String roomId, LocalDate date, String startTime, String endTime) {
        // 参数校验
        if (roomId == null || date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        // 解析时间参数
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");
        LocalTime startLocalTime = LocalTime.parse(startTime, formatter);
        LocalTime endLocalTime = LocalTime.parse(endTime, formatter);
        
        // 获取该房间的所有座位
        List<Seat> seats = seatRepository.findByRoomId(roomId);
        
        // 获取该房间在指定日期和时间段的预约信息（只包括待确认和已确认的预约）
        List<Reservation> reservations = reservationRepository.findByRoomIdAndReservationDateAndTimeOverlap(
                roomId, date, startLocalTime, endLocalTime);

//        System.out.println("reservations" + reservations);
        
        // 创建座位ID到预约信息的映射
        Map<String, Reservation> reservedSeats = reservations.stream()
                .collect(Collectors.toMap(Reservation::getSeatId, reservation -> reservation));
        
        // 检查是否有整个机房的预约
        boolean isWholeRoomReserved = reservedSeats.containsKey("-1");
        
        // 构建座位状态列表
        List<SeatStatusDTO> seatStatusList = new ArrayList<>();
        for (Seat seat : seats) {
            String status;
            if (seat.getStatus() == Seat.SeatStatus.故障) {
                status = "故障";
            } else if (isWholeRoomReserved) {
                // 如果整个机房被预约了，则所有座位都显示为占用
                status = "占用";
            } else if (reservedSeats.containsKey(seat.getSeatId())) {
                status = "占用";
            } else {
                status = "空闲";
            }
            
            seatStatusList.add(new SeatStatusDTO(seat.getSeatId(), status));
        }
        
        return seatStatusList;
    }
    
    /**
     * 更新单个座位状态
     * @param roomId 机房ID
     * @param seatId 座位ID
     * @param status 新的状态
     * @return 更新后的座位对象
     */
    public Seat updateSeatStatus(String roomId, String seatId, Seat.SeatStatus status) {
        Seat seat = seatRepository.findByRoomIdAndSeatId(roomId, seatId)
                .orElseThrow(() -> new IllegalArgumentException("座位不存在"));
        seat.setStatus(status);
        return seatRepository.save(seat);
    }
    
    /**
     * 批量更新座位状态
     * @param roomId 机房ID
     * @param seatIds 座位ID列表
     * @param status 新的状态
     * @return 更新后的座位对象列表
     */
    public List<Seat> updateSeatStatusBatch(String roomId, List<String> seatIds, Seat.SeatStatus status) {
        List<Seat> updatedSeats = new ArrayList<>();
        for (String seatId : seatIds) {
            Seat seat = seatRepository.findByRoomIdAndSeatId(roomId, seatId)
                    .orElseThrow(() -> new IllegalArgumentException("座位不存在: " + seatId));
            seat.setStatus(status);
            updatedSeats.add(seat);
        }
        return seatRepository.saveAll(updatedSeats);
    }
}
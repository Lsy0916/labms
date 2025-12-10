package org.example.labms.service;

import org.example.labms.model.ComputerLab;
import org.example.labms.model.Seat;
import org.example.labms.repository.ComputerLabRepository;
import org.example.labms.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ComputerLabService {

    @Autowired
    private ComputerLabRepository computerLabRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ReservationService reservationService;

    public List<ComputerLab> getAllComputerLabs() {
        return computerLabRepository.findAll();
    }

    public ComputerLab getComputerLabByRoomId(String roomId) {
        return computerLabRepository.findByRoomId(roomId).orElse(null);
    }
    
    public ComputerLab createComputerLab(ComputerLab computerLab) {
        // 先保存机房信息
        ComputerLab savedLab = computerLabRepository.save(computerLab);
        
        // 如果设置了总座位数且大于0，则创建对应数量的座位
        if (computerLab.getTotalSeats() != null && computerLab.getTotalSeats() > 0) {
            addSeatsForLab(savedLab, computerLab.getTotalSeats());
        }
        
        return savedLab;
    }
    
    @Transactional
    public ComputerLab updateComputerLab(String roomId, ComputerLab computerLabDetails) {
        ComputerLab computerLab = computerLabRepository.findByRoomId(roomId).orElse(null);
        if (computerLab != null) {
            computerLab.setRoomId(computerLabDetails.getRoomId());
            computerLab.setName(computerLabDetails.getName());
            computerLab.setEquipmentInfo(computerLabDetails.getEquipmentInfo());
            computerLab.setTotalSeats(computerLabDetails.getTotalSeats());
            computerLab.setNiceSeats(computerLabDetails.getNiceSeats());
            computerLab.setAvailableSeats(computerLabDetails.getAvailableSeats());
            computerLab.setAllowedRoles(computerLabDetails.getAllowedRoles());
            computerLab.setManagerId(computerLabDetails.getManagerId());
            computerLab.setStatus(computerLabDetails.getStatus());
            computerLab.setUpdatedAt(LocalDateTime.now());
            return computerLabRepository.save(computerLab);
        }
        return null;
    }
    
    @Transactional
    public void deleteComputerLab(String roomId) {
        // 先删除该机房的所有预约信息
        reservationService.deleteReservationsByRoomId(roomId);
        
        // 再删除该机房的所有座位信息
        seatRepository.deleteByRoomId(roomId);
        
        // 最后删除机房信息
        computerLabRepository.deleteByRoomId(roomId);
    }
    
    @Transactional
    public ComputerLab updateComputerLabInfo(String roomId, String name, Integer totalSeats, 
                                          String allowedRoles, String status, String managerId, 
                                          String equipmentInfo) {
        ComputerLab computerLab = computerLabRepository.findByRoomId(roomId).orElse(null);
        if (computerLab != null) {
            // 更新基本信息
            if (name != null) {
                computerLab.setName(name);
            }
            if (allowedRoles != null) {
                computerLab.setAllowedRoles(allowedRoles);
            }
            if (managerId != null) {
                computerLab.setManagerId(managerId);
            }
            if (equipmentInfo != null) {
                computerLab.setEquipmentInfo(equipmentInfo);
            }
            
            // 更新状态
            if (status != null) {
                try {
                    computerLab.setStatus(ComputerLab.LabStatus.valueOf(status));
                } catch (IllegalArgumentException e) {
                    // 如果状态值无效，保持原状态
                }
            }
            
            // 更新座位数
            if (totalSeats != null && totalSeats > 0) {
                int oldTotalSeats = computerLab.getTotalSeats() != null ? computerLab.getTotalSeats() : 0;
                computerLab.setTotalSeats(totalSeats);
                
                // 如果新的座位数大于原来座位数，自动添加座位
                if (totalSeats > oldTotalSeats) {
                    addSeatsForLab(computerLab, totalSeats);
                }
                // 注意：如果新的座位数小于原来座位数，我们不删除座位，因为可能已有预约关联
            }
            
            computerLab.setUpdatedAt(LocalDateTime.now());
            return computerLabRepository.save(computerLab);
        }
        return null;
    }
    
    /**
     * 为机房添加座位
     * @param computerLab 机房对象
     * @param newTotalSeats 新的座位总数
     */
    private void addSeatsForLab(ComputerLab computerLab, int newTotalSeats) {
        String roomId = computerLab.getRoomId();
        
        // 获取当前机房按seatId倒序排列的座位列表
        List<Seat> seats = seatRepository.findByRoomIdOrderBySeatIdDesc(roomId);
        
        // 计算当前座位数量
        int currentSeatCount = seats.size();
        
        // 获取当前最大的座位编号
        int maxSeatNumber = 0;
        if (!seats.isEmpty()) {
            String lastSeatId = seats.get(0).getSeatId(); // 最大的seatId
            try {
                maxSeatNumber = Integer.parseInt(lastSeatId);
            } catch (NumberFormatException e) {
                // 如果解析失败，保持maxSeatNumber为0
            }
        }
        
        // 从最大座位编号+1开始添加新座位，直到达到新的总座位数
        for (int i = currentSeatCount + 1; i <= newTotalSeats; i++) {
            Seat seat = new Seat();
            int seatNumber = maxSeatNumber + (i - currentSeatCount);
            // 使用两位数字格式，如01, 02, ..., 10, 11...
            String formattedSeatNumber = String.format("%02d", seatNumber);
            seat.setSeatId(formattedSeatNumber);
            seat.setRoomId(roomId);
            seat.setStatus(Seat.SeatStatus.空闲); // 默认空闲状态
            seatRepository.save(seat);
        }
    }
}
package org.example.labms.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationRequestDTO {
    private String roomId;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> seatIds;
    private String mode; // 'single' | 'whole'
    private String userId;

    public ReservationRequestDTO() {
    }

    public ReservationRequestDTO(String roomId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime, 
                               List<String> seatIds, String mode, String userId) {
        this.roomId = roomId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seatIds = seatIds;
        this.mode = mode;
        this.userId = userId;
    }

    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ReservationRequestDTO{" +
                "roomId='" + roomId + '\'' +
                ", reservationDate=" + reservationDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", seatIds=" + seatIds +
                ", mode='" + mode + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
package org.example.labms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "reservation_id", length = 30, nullable = false)
    private String reservationId;
    
    @Column(name = "room_id", length = 20, nullable = false)
    private String roomId;
    
    @Column(name = "seat_id", length = 20, nullable = false)
    private String seatId; // 修改为String类型，与数据库表结构一致，当值为"-1"时表示整个机房的预约，其他值表示具体座位的ID
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "status", columnDefinition = "ENUM('待确认', '已确认', '已完成', '已取消', '已过期', '已驳回') DEFAULT '待确认'")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ReservationStatus {
        待确认, 已确认, 已完成, 已取消, 已过期, 已驳回
    }
    
    public Reservation() {
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public String getSeatId() {
        return seatId;
    }
    
    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationId='" + reservationId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", seatId=" + seatId +
                ", userId='" + userId + '\'' +
                ", reservationDate=" + reservationDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
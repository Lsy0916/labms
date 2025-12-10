package org.example.labms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
public class Seat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "seat_id", length = 20, nullable = false)
    private String seatId;
    
    @Column(name = "room_id", length = 20, nullable = false)
    private String roomId;
    
    @Column(name = "status", columnDefinition = "ENUM('空闲', '占用', '故障') DEFAULT '空闲'")
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum SeatStatus {
        空闲, 占用, 故障
    }
    
    public Seat() {
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
    
    public String getSeatId() {
        return seatId;
    }
    
    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public SeatStatus getStatus() {
        return status;
    }
    
    public void setStatus(SeatStatus status) {
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
        return "Seat{" +
                "id=" + id +
                ", seatId='" + seatId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
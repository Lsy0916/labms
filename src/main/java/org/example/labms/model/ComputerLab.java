package org.example.labms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class ComputerLab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_id", length = 20, nullable = false)
    private String roomId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "equipment_info", columnDefinition = "TEXT")
    private String equipmentInfo;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "nice_seats", nullable = false)
    private Integer niceSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "allowed_roles", columnDefinition = "TEXT")
    private String allowedRoles;

    @Column(name = "manager_id", length = 255)
    private String managerId;

    @Column(name = "status", columnDefinition = "ENUM('正常', '维护', '停用') DEFAULT '正常'")
    @Enumerated(EnumType.STRING)
    private LabStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ComputerLab() {
    }

    public ComputerLab(Integer id, String roomId, String name, String equipmentInfo, Integer totalSeats, Integer niceSeats, Integer availableSeats, String allowedRoles, String managerId, LabStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.equipmentInfo = equipmentInfo;
        this.totalSeats = totalSeats;
        this.niceSeats = niceSeats;
        this.availableSeats = availableSeats;
        this.allowedRoles = allowedRoles;
        this.managerId = managerId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public enum LabStatus {
        正常, 维护, 停用
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEquipmentInfo() {
        return equipmentInfo;
    }

    public void setEquipmentInfo(String equipmentInfo) {
        this.equipmentInfo = equipmentInfo;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getNiceSeats() {
        return niceSeats;
    }

    public void setNiceSeats(Integer niceSeats) {
        this.niceSeats = niceSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(String allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public LabStatus getStatus() {
        return status;
    }

    public void setStatus(LabStatus status) {
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
        return "ComputerLab{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                ", equipmentInfo='" + equipmentInfo + '\'' +
                ", totalSeats=" + totalSeats +
                ", niceSeats=" + niceSeats +
                ", availableSeats=" + availableSeats +
                ", allowedRoles='" + allowedRoles + '\'' +
                ", managerId='" + managerId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


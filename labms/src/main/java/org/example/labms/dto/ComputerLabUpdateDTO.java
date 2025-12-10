package org.example.labms.dto;

public class ComputerLabUpdateDTO {
    private String roomId;
    private String name;
    private Integer totalSeats;
    private String allowedRoles;
    private String status;
    private String managerId;
    private String equipmentInfo;

    // Getters and Setters
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

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(String allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getEquipmentInfo() {
        return equipmentInfo;
    }

    public void setEquipmentInfo(String equipmentInfo) {
        this.equipmentInfo = equipmentInfo;
    }
}
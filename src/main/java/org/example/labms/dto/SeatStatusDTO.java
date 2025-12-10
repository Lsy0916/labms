package org.example.labms.dto;

public class SeatStatusDTO {
    private String seatId;
    private String status; // 空闲, 故障, 预约中
    
    public SeatStatusDTO() {
    }
    
    public SeatStatusDTO(String seatId, String status) {
        this.seatId = seatId;
        this.status = status;
    }
    
    // Getters and Setters
    public String getSeatId() {
        return seatId;
    }
    
    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "SeatStatusDTO{" +
                "seatId='" + seatId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
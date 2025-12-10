package org.example.labms.dto;

import java.time.LocalDate;

public class TermDTO {
    private String semesterId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructors
    public TermDTO() {
    }

    public TermDTO(String semesterId, String name, LocalDate startDate, LocalDate endDate) {
        this.semesterId = semesterId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "TermDTO{" +
                "semesterId='" + semesterId + '\'' +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
package org.example.labms.model;

import jakarta.persistence.*;

@Table(name = "course_schedules")
@Entity
public class CourseSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "semester_id")
    private String semesterId;

    @Column(name = "weekday")
    private String weekday;

    @Column(name = "start_section")
    private int startSection;

    @Column(name = "duration")
    private int duration;

    @Column(name = "weeks")
    private String weeks;

    @Column(name = "classroom")
    private String classroom;

    @Column(name = "status")
    private String status;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    // Constructors
    public CourseSchedule() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public int getStartSection() {
        return startSection;
    }

    public void setStartSection(int startSection) {
        this.startSection = startSection;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "CourseSchedule{" +
                "id=" + id +
                ", courseId='" + courseId + '\'' +
                ", classId='" + classId + '\'' +
                ", semesterId='" + semesterId + '\'' +
                ", weekday='" + weekday + '\'' +
                ", startSection=" + startSection +
                ", duration=" + duration +
                ", weeks='" + weeks + '\'' +
                ", classroom='" + classroom + '\'' +
                ", status='" + status + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
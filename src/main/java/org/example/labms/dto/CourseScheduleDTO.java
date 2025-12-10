package org.example.labms.dto;

import java.time.LocalDate;

public class CourseScheduleDTO {
    private String courseId;
    private String courseName; // 课程名称，需要从其他表关联获取
    private String classId;
    private String className; // 班级名称
    private String semesterId;
    private String weekday;
    private int startSection;
    private int duration;
    private String weeks;
    private String classroom;
    private String status;
    private String teacherId; // 新增的教师ID字段

    // Constructors
    public CourseScheduleDTO() {
    }

    public CourseScheduleDTO(String courseId, String courseName, String classId, String className, 
                           String semesterId, String weekday, int startSection, int duration, 
                           String weeks, String classroom, String status) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.classId = classId;
        this.className = className;
        this.semesterId = semesterId;
        this.weekday = weekday;
        this.startSection = startSection;
        this.duration = duration;
        this.weeks = weeks;
        this.classroom = classroom;
        this.status = status;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "CourseScheduleDTO{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", classId='" + classId + '\'' +
                ", className='" + className + '\'' +
                ", semesterId='" + semesterId + '\'' +
                ", weekday='" + weekday + '\'' +
                ", startSection=" + startSection +
                ", duration=" + duration +
                ", weeks='" + weeks + '\'' +
                ", classroom='" + classroom + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
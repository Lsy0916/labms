package org.example.labms.repository;

import org.example.labms.model.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classId = :classId AND cs.semesterId = :semesterId AND cs.status = '正常'")
    List<CourseSchedule> findByClassIdAndSemesterId(@Param("classId") String classId, @Param("semesterId") String semesterId);
    
    List<CourseSchedule> findByClassId(String classId);
    
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacherId = :teacherId AND cs.semesterId = :semesterId AND cs.status = '正常'")
    List<CourseSchedule> findByTeacherIdAndSemesterId(@Param("teacherId") String teacherId, @Param("semesterId") String semesterId);
}
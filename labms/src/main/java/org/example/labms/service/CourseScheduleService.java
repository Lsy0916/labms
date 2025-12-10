package org.example.labms.service;

import org.example.labms.dto.CourseScheduleDTO;
import org.example.labms.dto.TermDTO;
import org.example.labms.model.CourseSchedule;
import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.model.Classes;
import org.example.labms.repository.CourseScheduleRepository;
import org.example.labms.repository.TeacherRepository;
import org.example.labms.repository.UserRepository;
import org.example.labms.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseScheduleService {

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassesRepository classesRepository;
    
    @Autowired
    private TermService termService;
    
    @Autowired
    private CourseService courseService;

    /**
     * 根据用户ID和角色ID获取用户的课程信息
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 课程信息列表
     */
    public List<CourseScheduleDTO> getUserCourses(String roleId, String userId) {
        // 获取当前学期ID（基于系统时间自动识别）
        TermDTO currentTerm = termService.getCurrentTerm();
        String currentSemesterId = currentTerm.getSemesterId();

        // 检查是否获取到有效的学期ID
        if (currentSemesterId == null || currentSemesterId.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据roleId确认用户类型
        if ("student".equals(roleId)) {
            // 根据userId找到用户
            Optional<User> userOptional = userRepository.findByUserId(userId);
            // 检查用户是否存在
            if (!userOptional.isPresent()) {
                return new ArrayList<>();
            }

            User user = userOptional.get();
            String classId = user.getClassId();

            // 检查用户是否有班级
            if (classId == null || classId.isEmpty()) {
                return new ArrayList<>();
            }

            // 根据班级ID和学期ID查找课程安排
            List<CourseSchedule> courseSchedules = courseScheduleRepository.findByClassIdAndSemesterId(classId, currentSemesterId);

            return convertToDTOList(courseSchedules);
        }

        if ("teacher".equals(roleId)) {
            // 根据userId找到用户
            Optional<Teacher> userOptional = teacherRepository.findByTeacherId(userId);
            // 检查用户是否存在
            if (!userOptional.isPresent()) {
                return new ArrayList<>();
            }

            Teacher teacher = userOptional.get();
            String teacherId = teacher.getTeacherId();

            // 根据教师ID和学期ID查找课程安排
            List<CourseSchedule> courseSchedules = courseScheduleRepository.findByTeacherIdAndSemesterId(teacherId, currentSemesterId);

            return convertToDTOList(courseSchedules);
        }
        return new ArrayList<>();
    }

    /**
     * 将课程安排列表转换为DTO列表
     * @param courseSchedules 课程安排列表
     * @return DTO列表
     */
    private List<CourseScheduleDTO> convertToDTOList(List<CourseSchedule> courseSchedules) {
        List<CourseScheduleDTO> courseScheduleDTOs = new ArrayList<>();
        for (CourseSchedule schedule : courseSchedules) {
            CourseScheduleDTO dto = new CourseScheduleDTO();
            dto.setCourseId(schedule.getCourseId());
            // 使用CourseService获取真实的课程名称
            dto.setCourseName(courseService.getCourseNameByCourseId(schedule.getCourseId()));
            dto.setClassId(schedule.getClassId());

            // 获取班级名称
            Optional<Classes> classOptional = classesRepository.findByClassId(schedule.getClassId());
            if (classOptional.isPresent()) {
                dto.setClassName(classOptional.get().getName());
            } else {
                dto.setClassName("未知班级");
            }

            dto.setSemesterId(schedule.getSemesterId());
            dto.setWeekday(schedule.getWeekday());
            dto.setStartSection(schedule.getStartSection());
            dto.setDuration(schedule.getDuration());
            dto.setWeeks(schedule.getWeeks());
            dto.setClassroom(schedule.getClassroom());
            dto.setStatus(schedule.getStatus());
            dto.setTeacherId(schedule.getTeacherId()); // 设置教师ID

            courseScheduleDTOs.add(dto);
        }
        return courseScheduleDTOs;
    }

    /**
     * 获取当前学期信息
     * @return 当前学期信息
     */
    public TermDTO getCurrentTerm() {
        return termService.getCurrentTerm();
    }
}
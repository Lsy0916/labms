// 课表管理 Store
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getStudentSchedule, getTeacherSchedule, getCurrentTerm } from '@/api/schedule'

export const useScheduleStore = defineStore('schedule', () => {
  // 状态
  const schedules = ref({})
  const currentWeek = ref(1)
  const currentSemester = ref(null)

  // 根据周次获取课表
  const getScheduleByWeek = computed(() => (week) => {
    return schedules.value[week] || []
  })

  // 获取当前学期信息
  const fetchCurrentTerm = async () => {
    try {
      const response = await getCurrentTerm()
      if (response.status === 200) {
        currentSemester.value = response.data
        return {
          success: true,
          data: response.data
        }
      } else {
        return {
          success: false,
          message: response.message || '获取学期信息失败'
        }
      }
    } catch (error) {
      console.error('获取学期信息失败:', error)
      return {
        success: false,
        message: error.message || '获取学期信息失败'
      }
    }
  }

  // 计算给定日期是学期的第几周
  const calculateWeekNumber = (date, startDate) => {
    const start = new Date(startDate)
    const target = new Date(date)
    // 计算两个日期之间的毫秒差
    const diffTime = Math.abs(target - start)
    // 转换为天数
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    // 计算周数（从第1周开始）
    return Math.max(1, Math.ceil(diffDays / 7))
  }

  // 获取课表
  const fetchSchedule = async (user, week) => {
    try {
      // 检查userId是否有效
      if (!user || !user.userId) {
        return {
          success: false,
          message: '用户ID不能为空'
        }
      }

      // 如果还没有获取当前学期信息，先获取
      if (!currentSemester.value) {
        const termResult = await fetchCurrentTerm()
        if (!termResult.success) {
          console.error('获取学期信息失败:', termResult.message)
        }
      }

      let response

      // 根据用户类型获取课表
      if (user.roleId === 'student') {
        // 学生课表
        response = await getStudentSchedule(user.userId)
      } else if (user.roleId === 'teacher') {
        // 教师课表
        response = await getTeacherSchedule(user.userId)
      } else {
        // 默认处理为学生课表
        response = await getStudentSchedule(user.userId)
      }

      if (response.status === 200) {
        // 处理返回的课表数据
        let scheduleData = response.data

        // 如果有当前学期信息，则过滤出当前学期的课程
        if (currentSemester.value && scheduleData.length > 0) {
          scheduleData = scheduleData.filter(course =>
            course.semesterId === currentSemester.value.semesterId
          )
        }

        // 格式化数据以适应前端使用
        scheduleData = scheduleData.map(item => ({
          id: item.courseId,
          courseId: item.courseId,
          courseName: item.courseName,
          classId: item.classId,
          className: item.className,
          semesterId: item.semesterId,
          dayOfWeek: parseInt(item.weekday),
          startSection: item.startSection,
          duration: item.duration,
          weeks: item.weeks,
          room: item.classroom,
          status: item.status,
          teacher: item.teacherName || '' // 添加教师字段
        }))

        // 根据课程的周次信息过滤当前周的课程
        let weeklySchedule = scheduleData
        if (currentSemester.value) {
          weeklySchedule = scheduleData.filter(course => {
            const weeks = course.weeks.split(',').map(w => parseInt(w.trim()))
            return weeks.includes(week)
          })
        }

        schedules.value[week] = weeklySchedule
        return {
          success: true,
          data: weeklySchedule
        }
      } else {
        return {
          success: false,
          message: response.message || '获取课表失败'
        }
      }
    } catch (error) {
      console.error('获取课表失败:', error)
      return {
        success: false,
        message: error.message || '获取课表失败'
      }
    }
  }

  return {
    schedules,
    currentWeek,
    currentSemester,
    getScheduleByWeek,
    fetchSchedule,
    fetchCurrentTerm,
    calculateWeekNumber
  }
})

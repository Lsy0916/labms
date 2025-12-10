// 课表相关 API
import request from '@/utils/request'

/**
 * 获取学生课表
 * @param {string} userId - 学生ID
 * @returns {Promise} 课表数据
 */
export const getStudentSchedule = (userId) => {
  return request({
    url: '/user/courses',
    method: 'get',
    params: {
      roleId: 'student',
      userId: userId
    }
  })
}

/**
 * 获取教师课表
 * @param {string} userId - 教师ID
 * @returns {Promise} 课表数据
 */
export const getTeacherSchedule = (userId) => {
  return request({
    url: '/user/courses',
    method: 'get',
    params: {
      roleId: 'teacher',
      userId: userId
    }
  })
}

/**
 * 获取当前学期信息
 * @returns {Promise} 当前学期信息
 */
export const getCurrentTerm = () => {
  return request({
    url: '/current_term',
    method: 'get'
  })
}

export default {
  getStudentSchedule,
  getTeacherSchedule,
  getCurrentTerm
}
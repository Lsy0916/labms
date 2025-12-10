// 学生相关工具函数

import { updateStudentInfo } from '@/api/user'

/**
 * 更新学生信息
 * @param {Object} userInfo - 学生信息对象
 * @returns {Promise<Object>} 包含更新结果的Promise
 */
export async function updateStudentProfile(userInfo) {
  try {
    const response = await updateStudentInfo(userInfo)

    if (response.status === 200 || response.success === true) {
      return {
        success: true,
        data: response.data,
        message: response.message || '信息更新成功'
      }
    } else {
      return {
        success: false,
        message: response.message || '信息更新失败'
      }
    }
  } catch (error) {
    console.error('更新学生信息时出错:', error)
    return {
      success: false,
      message: error.message || '信息更新过程中发生错误'
    }
  }
}

export default {
  updateStudentProfile
}

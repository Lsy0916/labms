// 邮件验证码工具类
import { sendEmailCode } from '@/api/captcha'
import { ElMessage } from 'element-plus'

/**
 * 发送邮件验证码工具函数
 * @param {Object} params - 参数对象
 * @param {string} params.roleId - 角色ID
 * @param {string} params.userId - 用户ID
 * @param {string} params.email - 邮箱地址
 * @returns {Promise<boolean>} - 发送成功返回true，失败返回false
 */
export const sendEmailVerificationCode = async ({ roleId, userId, email }) => {
  // 检查邮箱是否为空
  if (!email) {
    ElMessage.warning('邮箱地址不能为空')
    return false
  }

  try {
    // 调用发送邮件验证码接口
    await sendEmailCode({ roleId, userId, email })
    ElMessage.success(`验证码已发送至 ${email}`)
    return true
  } catch (error) {
    ElMessage.error('验证码发送失败: ' + (error.message || '未知错误'))
    return false
  }
}
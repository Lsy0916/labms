// 验证码相关 API
import request from '@/utils/request'

// 获取邮箱验证码
export const sendEmailCode = (params) => {
  return request({
    url: '/sendCode',
    method: 'post',
    params
  })
}

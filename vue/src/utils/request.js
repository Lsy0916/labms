// axios 封装
import axios from 'axios'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // url = base url + request url (由 Vite 代理处理)
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 添加请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => {
    // 错误处理
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 检查响应结构并处理不同情况
    if (response.status === 200 || response.statusText === 'OK' || response.success === true) {
      // 成功响应
      return response
    } else if (response.status === 401) {
      // 未授权，跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
      return Promise.reject(new Error(response.message || '未授权访问'))
    } else if (response.status >= 500) {
      // 服务器错误
      return Promise.reject(new Error(`服务器内部错误 (${response.status}): ${response.data?.message || response.statusText || '请稍后再试'}`))
    } else {
      // 其他错误情况
      return Promise.reject(new Error(response.data?.message || response.message || `请求失败 (${response.status})`))
    }
  },
  (error) => {
    console.log('err' + error)
    // 提供更详细的错误信息
    if (error.response) {
      // 请求已发出，但服务器响应的状态码不在 2xx 范围内
      if (error.response.status >= 500) {
           // 对于500错误，提供更友好的提示信息
        return Promise.reject(new Error('服务器暂时不可用，请稍后再试'))
      } else {
        return Promise.reject(new Error(`请求失败 (${error.response.status}): ${error.response.data?.message || error.response.statusText || error.message}`))
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      return Promise.reject(new Error('网络错误，请检查网络连接'))
    } else {
      // 其他错误
      return Promise.reject(new Error('请求配置错误'))
    }
  }
)

export default service

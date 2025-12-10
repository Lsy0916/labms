// 认证 Store
import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import {loginApi, UserInfoByIdApi} from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})
  const roleId = ref('')
  const rememberMe = ref(false)

  // 初始化用户信息
  const storedUserInfo = localStorage.getItem('userInfo')

  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)

      // 从userInfo中提取roleId
      roleId.value = userInfo.value.roleId || ''
    } catch (e) {
      console.error('解析用户信息失败', e)
      userInfo.value = {}
      roleId.value = ''
    }
  }

  // 检查是否记住登录信息
  const savedUserId = localStorage.getItem('savedUserId')
  const savedPassword = localStorage.getItem('savedPassword')
  if (savedUserId && savedPassword) {
    rememberMe.value = true
  }

  // 监听状态变化，自动保存到localStorage
  watch([token, userInfo, roleId], () => {
    if (token.value) {
      localStorage.setItem('token', token.value)
    } else {
      localStorage.removeItem('token')
    }

    if (Object.keys(userInfo.value).length > 0) {
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    } else {
      localStorage.removeItem('userInfo')
    }

    if (roleId.value) {
      // roleId通常包含在userInfo中，这里单独保存是为了方便访问
    }
  }, { deep: true })

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isStudent = computed(() => roleId.value === 'student')
  const isTeacher = computed(() => roleId.value === 'teacher')
  const isAdmin = computed(() => roleId.value === 'admin')
  // 用户信息
  const userDate = ref('')
  // 登录方法
  const login = async (loginData) => {
    try {
      const data = {
        userId: loginData.userId,
        password: loginData.password,
        roleId: loginData.roleId
      }

      // 调用真实 API 请求
      const response = await loginApi(data)

      // 检查响应结构，确保包含必要的字段
      if (response.status === 200 || response.statusText === 'ok') {
        // 登录成功，将用户信息保存到本地存储中
        // 如果已存在则进行覆盖
        const responseData = response.data;
        token.value = responseData.token

        try {
          userInfo.value = responseData.userInfo ? responseData.userInfo : JSON.parse(localStorage.getItem('userInfo'))
        } catch (e) {
          userInfo.value = responseData.userInfo || {}
        }
        roleId.value = responseData.userInfo?.roleId || userInfo.value?.roleId

        // 如果选择了记住登录信息
        if (loginData.remember) {
          rememberMe.value = true
          localStorage.setItem('savedUserId', data.userId)
          localStorage.setItem('savedPassword', data.password)
        } else {
          rememberMe.value = false
          localStorage.removeItem('savedUserId')
          localStorage.removeItem('savedPassword')
        }

        return {
          success: true,
          data: {
            token: token.value,
            userInfo: userInfo.value,
            roleId: roleId.value
          },
          message: '登录成功'
        }
      } else {
        // 登录失败
        return {
          success: false,
          message: response?.message || '登录失败'
        }
      }
    } catch (error) {
      console.error('登录失败:', error)
      // 根据错误类型提供更具体的错误信息
      let errorMessage = '登录过程中发生错误'
      if (error.message) {
        if (error.message.includes('500')) {
          errorMessage = '服务器内部错误，请稍后再试'
        } else if (error.message.includes('404')) {
          errorMessage = '请求的登录接口不存在'
        } else if (error.message.includes('网络错误')) {
          errorMessage = '网络连接失败，请检查网络设置'
        } else {
          errorMessage = error.message
        }
      }

      return {
        success: false,
        message: errorMessage
      }
    }
  }

  // 登出方法
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    roleId.value = ''
    userDate.value = ''
    rememberMe.value = false

    // 清除 localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    // 清除缓存
    localStorage.removeItem('authStatus')
    // 刷新页面
    window.location.reload()
  }

  // 获取用户信息
  const getUserInfoById = async (userId) => {
    try {
      // 检查userId参数
      if (!userId) {
        throw new Error('请提供有效的用户ID')
      }

      // 生成userId对象
      userId = { userId: userId }

      const response = await UserInfoByIdApi(userId, roleId.value)

      // 检查响应状态
      if (response && (response.status === 200 || response.success === true)) {
        userDate.value = response.data

        return {
          success: true,
          data: userDate.value,
          message: '获取用户信息成功'
        }
      } else {
        throw new Error(response?.message || '获取用户信息失败')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 根据错误类型提供更具体的错误信息
      let errorMessage = '获取用户信息失败'
      if (error.message) {
        if (error.message.includes('500')) {
          errorMessage = '服务器内部错误，请稍后再试'
        } else if (error.message.includes('404')) {
          errorMessage = '请求的资源不存在'
        } else if (error.message.includes('网络错误')) {
          errorMessage = '网络连接失败，请检查网络设置'
        } else {
          errorMessage = error.message
        }
      }

      return {
        success: false,
        message: errorMessage
      }
    }
  }

  // 页面刷新时保持登录状态的方法
  const refreshAuthStatus = () => {
    const storedToken = localStorage.getItem('token')
    const storedUserInfo = localStorage.getItem('userInfo')

    if (storedToken && storedUserInfo) {
      token.value = storedToken
      try {
        userInfo.value = JSON.parse(storedUserInfo)
        roleId.value = userInfo.value.roleId || ''
      } catch (e) {
        console.error('解析存储的用户信息时出错:', e)
        userInfo.value = {}
        roleId.value = ''
      }
    }
  }

  return {
    token,
    userInfo,
    roleId,
    userDate,
    rememberMe,
    isLoggedIn,
    isStudent,
    isTeacher,
    isAdmin,
    login,
    logout,
    getUserInfoById,
    refreshAuthStatus
  }
})

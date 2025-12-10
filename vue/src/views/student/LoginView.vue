<!-- 学生登录页面 -->
<template>
  <div class="login-container">
    <div class="theme-toggle">
      <el-switch
        v-model="isDark"
        inline-prompt
        :active-icon="Moon"
        :inactive-icon="Sunny"
        @change="toggleDark"
      />
    </div>

    <div class="login-box">
      <div class="login-header">
        <h2>学校机房管理系统</h2>
        <p>学生登录</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="userId">
          <el-input
            v-model="loginForm.userId"
            placeholder="请输入学号"
            prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.remember" label="保存登录信息" />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
            block
            round
          >
            <el-icon><User/></el-icon>
            学生登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Sunny, Moon, User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { debounce } from '@/utils/debounce.js'

// 使用 Pinia store
const authStore = useAuthStore()

// 路由
const router = useRouter()

// 表单引用
const loginFormRef = ref()

// 状态
const loading = ref(false) // 加载开关
const isDark = ref(false) // 暗黑模式开关

// 表单数据
const loginForm = reactive({
  userId: '',
  password: '',
  roleId: 'student',
  remember: false
})

// 表单验证规则
const loginRules = {
  userId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const toggleDark = (val) => {
  isDark.value = val
  if (val) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
  localStorage.setItem('darkMode', val)
}

const handleLogin = debounce(async () => {
  if (!loginFormRef.value) {
    ElMessage.error('请填写完整信息')
    return
  }

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请填写完整信息')
      return
    }

    try {
      // 开启加载动画
      loading.value = true

      // 调用登录接口
      const result = await authStore.login({
        userId: loginForm.userId,
        password: loginForm.password,
        roleId: loginForm.roleId,
        remember: loginForm.remember
      })

      if (result.success) {
        // 确保是学生角色
        const data = result.data

        if (data.roleId === 'student') {
          ElMessage.success('学生登录成功')
          // 跳转到首页
          router.push('/dashboard')
        } else {
          ElMessage.error('请使用正确的登录入口')
          authStore.logout()
          router.push('/login')
        }
      } else {
        // 根据错误类型提供更具体的提示
        if (result.message && result.message.includes('服务器')) {
          ElMessage.error('服务器暂时不可用，请稍后再试')
        } else {
          ElMessage.error(result.message || '登录失败')
        }
        // 添加更多调试信息
        console.log('登录失败详情:', result)
      }
    } catch (error) {
      console.error('登录异常:', error)
      if (error.message && error.message.includes('服务器')) {
        ElMessage.error('服务器暂时不可用，请稍后再试')
      } else {
        ElMessage.error('登录过程中发生错误: ' + (error.message || '未知错误'))
      }
    } finally {
      loading.value = false
    }
  })
}, 1000, true)

// 页面关闭前的处理
const handleBeforeUnload = () => {
  // 如果用户没有选择记住登录信息，则清除本地存储
  if (!loginForm.remember) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
}

// 生命周期
onMounted(() => {
  // 恢复深色模式设置
  const darkMode = localStorage.getItem('darkMode') === 'true'
  isDark.value = darkMode
  if (darkMode) {
    document.documentElement.classList.add('dark')
  }

  // 如果用户选择了记住登录信息，则尝试自动登录
  const savedUserId = localStorage.getItem('savedUserId')
  const savedPassword = localStorage.getItem('savedPassword')

  if (savedUserId && savedPassword) {
    loginForm.userId = savedUserId
    loginForm.password = savedPassword
    loginForm.remember = true
  }

  // 添加页面关闭事件监听器
  window.addEventListener('beforeunload', handleBeforeUnload)
})

// 组件销毁前移除事件监听器
onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
})
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .theme-toggle {
    position: absolute;
    top: 20px;
    right: 20px;
  }

  .login-box {
    width: 100%;
    max-width: 400px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 10px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);

    .login-header {
      text-align: center;
      margin-bottom: 30px;

      h2 {
        margin: 0 0 10px;
        font-size: 24px;
        color: #333;
      }

      p {
        margin: 0;
        color: #666;
      }
    }

    .login-form {
      :v-deep(.el-form-item) {
        margin-bottom: 20px;
      }

      :v-deep(.el-input__wrapper) {
        background: transparent;
        box-shadow: 0 0 0 1px #dcdfe6 inset;
        border-radius: 6px;

        &.is-focus {
          box-shadow: 0 0 0 1px var(--el-color-primary) inset;
        }
      }

      .login-button {
        width: 100%;
        margin-top: 10px;
        border-radius: 6px;
        height: 48px;
        font-size: 16px;
        font-weight: bold;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
        }
      }
    }
  }
}

// 深色主题适配
html.dark {
  .login-container {
    background: linear-gradient(135deg, #3e007d 0%, #000000 100%);

    .login-box {
      background: rgba(30, 30, 30, 0.95);

      .login-header {
        h2 {
          color: #fff;
        }

        p {
          color: #ccc;
        }
      }

      :deep(.el-input__wrapper) {
        background: transparent;
        box-shadow: 0 0 0 1px #434343 inset;

        &.is-focus {
          box-shadow: 0 0 0 1px var(--el-color-primary) inset;
        }
      }
    }
  }
}
</style>

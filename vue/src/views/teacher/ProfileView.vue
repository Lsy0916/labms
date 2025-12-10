<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8">
          <div class="avatar-container">
            <el-avatar :size="120" :src="userAvatar" />
            <el-button type="primary" size="small" @click="changeAvatar" class="change-avatar-btn">
              更换头像
            </el-button>
          </div>
        </el-col>

        <el-col :span="16">
          <el-form
            :model="profileForm"
            :rules="rules"
            ref="profileFormRef"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="工号" prop="userId">
              <el-input v-model="profileForm.userId" disabled />
            </el-form-item>

            <el-form-item label="姓名" prop="name">
              <el-input v-model="profileForm.name" disabled />
            </el-form-item>

            <el-form-item label="性别" prop="gender">
              <el-input v-model="profileForm.gender" disabled />
            </el-form-item>

            <el-form-item label="部门" prop="department">
              <el-input v-model="profileForm.department" disabled />
            </el-form-item>

            <el-form-item label="职称" prop="title">
              <el-input v-model="profileForm.title" disabled />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>

            <el-form-item label="学历" prop="degree">
              <el-input v-model="profileForm.degree" disabled />
            </el-form-item>

            <el-form-item label="入职时间" prop="hireDate">
              <el-input v-model="profileForm.hireDate" disabled />
            </el-form-item>

            <el-form-item label="状态" prop="status">
              <el-input v-model="profileForm.status" disabled />
            </el-form-item>

            <el-form-item label="角色" prop="role">
              <el-input v-model="profileForm.role" disabled />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存信息</el-button>
              <el-button type="warning" @click="passwordDialogVisible = true">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px" class="profile-dialog">
      <el-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="100px"
        class="password-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>

        <el-form-item label="邮箱验证" prop="emailVerification">
          <el-input v-model="passwordForm.emailVerification" placeholder="请输入邮箱验证码">
            <template #append>
              <el-button @click="sendEmailCode" :disabled="countdown > 0">
                {{ countdown > 0 ? `${countdown}s后重新发送` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
          <div class="email-info">验证码将发送至: {{ maskedEmail }}</div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button type="default" @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="changePassword">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {updateStudentProfile} from "@/utils/updateData.js";
import { updatePassword } from '@/api/user'
import {sendEmailVerificationCode} from "@/utils/emailCode.js";
import { debounce } from '@/utils/debounce.js';

// 使用认证 store
const authStore = useAuthStore()

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()

// 对话框控制
const passwordDialogVisible = ref(false)

// 邮箱验证码倒计时
const countdown = ref(0)
let countdownTimer = null

// 用户信息
const user = ref({})

// 计算头像路径
const userAvatar = ref('')

// 表单数据
const profileForm = reactive({
  userId: '',
  name: '',
  gender: '',
  department: '',
  title: '',
  phone: '',
  email: '',
  degree: '',
  hireDate: '',
  status: '',
  role: ''
})

// 密码表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  emailVerification: ''
})

// 表单验证规则
const rules = {
  userId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  class: [
    { required: true, message: '请输入班级', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
}

// 密码表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  emailVerification: [
    { required: true, message: '请输入邮箱验证码', trigger: 'blur' }
  ]
}

// 遮蔽邮箱显示（只显示前三位和后缀）
const maskedEmail = computed(() => {
  const email = profileForm.email
  if (!email || !email.includes('@')) return ''

  const [localPart, domain] = email.split('@')
  if (localPart.length <= 3) {
    return `***@${domain}`
  }

  return `${localPart.substring(0, 3)}***@${domain}`
})

// 初始化用户信息
const initUserInfo = async () => {
  // 解析用户信息
  const userId = authStore.userInfo.userId;

  // 检查userId是否存在
  if (!userId) {
    console.error('用户ID不存在');
    ElMessage.error('无法获取用户信息：用户ID缺失');
    return;
  }

  // 调用真实 API 获取用户信息
  try {
    const result = await authStore.getUserInfoById(userId);

    if (result.success && result.data) {
      user.value = result.data;

      // 填充表单数据
      profileForm.userId = user.value?.teacherId || '';
      profileForm.name = user.value?.name || '';
      profileForm.gender = user.value?.gender || '';
      profileForm.department = user.value?.department || '';
      profileForm.title = user.value?.title || '';
      profileForm.phone = user.value?.phone || '';
      profileForm.email = user.value?.email || '';
      profileForm.degree = user.value?.degree || '';
      profileForm.hireDate = user.value?.hireDate || '';
      profileForm.status = user.value?.status || '';
      profileForm.role = user.value?.role || '';

      // 设置头像
      userAvatar.value = user.value?.avatarUrl
          ? `/src/assets/avatar/teacher/${user.value?.avatarUrl}`
          : '/src/assets/avatar/default.png';
    } else {
      // 根据错误类型提供更具体的提示
      if (result.message && result.message.includes('服务器')) {
        ElMessage.error('服务器暂时不可用，请稍后再试');
      } else {
        ElMessage.error(result.message || '获取用户信息失败');
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息失败: ' + (error.message || '未知错误'));
  }
}

// 保存个人信息
const saveProfile = debounce(async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 准备要更新的信息
        const teacherInfo = {
          userId: profileForm.userId,
          name: profileForm.name,
          gender: profileForm.gender,
          department: profileForm.department,
          position: profileForm.position,
          phone: profileForm.phone,
          email: profileForm.email,
          roleId: authStore.roleId,
        };

        // 调用工具函数更新教师信息
        const result = await updateStudentProfile(teacherInfo);

        if (result.success) {
          ElMessage.success('个人信息保存成功');

          // 更新store中的用户信息
          await authStore.getUserInfoById(profileForm.userId);
        } else {
          ElMessage.error(result.message || '保存个人信息失败');
        }
      } catch (error) {
        console.error('保存个人信息时出错:', error);
        ElMessage.error('保存个人信息失败: ' + (error.message || '未知错误'));
      }
    }
  });
}, 1000, true)

// 更换头像
const changeAvatar = debounce(() => {
  ElMessage.info('更换头像功能待实现')
}, 1000, true)

// 修改密码
const changePassword = debounce(async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用 API 修改密码
        const requestData = {
          userId: profileForm.userId,
          roleId: authStore.roleId,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
          emailVerification: passwordForm.emailVerification
        }

        const response = await updatePassword(requestData)

        if (response.status === 200) {
          ElMessage.success('密码修改成功')
          passwordDialogVisible.value = false

          // 重置密码表单
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
          passwordForm.emailVerification = ''

          // 重置倒计时
          countdown.value = 0
          if (countdownTimer) {
            clearInterval(countdownTimer)
          }
        } else {
          ElMessage.error(response.message || '密码修改失败')
        }
      } catch (error) {
        ElMessage.error('密码修改失败: ' + (error.message || '未知错误'))
      }
    }
  })
}, 1000, true)

// 发送邮箱验证码
const sendEmailCode = debounce(async () => {
  // 启动倒计时
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
    }
  }, 1000)
  // 发送邮件验证码
  const isSuccess = await sendEmailVerificationCode({
    roleId: authStore.roleId,
    userId: profileForm.userId,
    email: profileForm.email
  })
  if (!isSuccess) {
    // 发送失败重置倒计时
    countdown.value = 0
    if (countdownTimer) {
      clearInterval(countdownTimer)
    }
  }
}, 1000, true)

// 生命周期钩子
onMounted(() => {
  initUserInfo()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style lang="scss" scoped>
.profile-container {
  .profile-card {
    border: none;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .card-header {
      font-size: 18px;
      font-weight: bold;
      border: none;
    }

    .avatar-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 15px;

      .change-avatar-btn {
        margin-top: 10px;
      }
    }

    .profile-form {
      margin-top: 20px;
      border: none;
    }
  }

  .email-info {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
  }
}

// 深色主题适配
html.dark {
  .profile-container {
    .profile-card {
      background-color: #1d1e1f;
      color: #ffffff;
      border: none;

      :deep(.el-dialog) {
        background-color: #1d1e1f;
      }
      :deep(.el-card__header) {
        background-color: #1d1e1f;
        border-color: #333;
        color: #ffffff;
        border: none;
      }

      :deep(.el-card__body) {
        background-color: #1d1e1f;
        color: #ffffff;
        border: none;
      }

      .card-header {
        color: #ffffff;
      }

      // 深色模式下的表单适配
      :deep(.el-input__wrapper) {
        background-color: #1d1e1f;
        border: 1px solid #434343;

        .el-input {
          background-color: #1d1e1f;
          color: #ffffff;
        }
      }

      :deep(.el-form-item__label) {
        color: #ffffff;
      }

    }

    :deep(.el-dialog) {
      background-color: #1d1e1f;
      border: none;

      .el-dialog__header {
        background-color: #1d1e1f;
        color: #ffffff;
        border: none;
      }

      .el-dialog__body {
        background-color: #1d1e1f;
        color: #ffffff;
        border: none;
      }

      .el-dialog__footer {
        background-color: #1d1e1f;
        border: none;
      }
    }

    // 深色模式下的表单项目适配
    :deep(.el-form-item) {
      background-color: #1d1e1f;
      color: #ffffff;
      border: none;
    }

    .password-form {
      :deep(.el-input__wrapper) {
        background-color: #1d1e1f;
        border: 1px solid #434343;

        .el-input {
          background-color: #1d1e1f;
          color: #ffffff;
        }
      }

      :deep(.el-form-item__label) {
        color: #ffffff;
      }
    }

    .dialog-footer {
      // 取消按钮颜色背景设置
      :deep(.el-button--default) {
        background-color: #434343;
        border-color: #434343;
        color: #ffffff;
      }
    }

    .email-info {
      color: #aaa;
    }
  }
}
</style>

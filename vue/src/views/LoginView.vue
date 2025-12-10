<!-- 通用登录页面 -->
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>学校机房管理系统</h2>
        <p>请选择您的身份登录</p>
      </div>


      <div class="role-selection">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="8">
            <div class="role-card" @click="goToStudent">
              <div class="role-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="role-name">学生</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="role-card" @click="goToTeacher">
              <div class="role-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="role-name">教师</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="role-card" @click="goToAdmin">
              <div class="role-icon">
                <el-icon><Lock /></el-icon>
              </div>
              <div class="role-name">管理员</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="login-footer">
        <p>请选择对应身份登录系统</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { User, UserFilled, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { debounce } from '@/utils/debounce.js'

const router = useRouter()

const goToStudent = debounce(() => {
  router.push('/student')
}, 1000, true)

const goToTeacher = debounce(() => {
  router.push('/teacher')
}, 1000, true)

const goToAdmin = debounce(() => {
  router.push('/admin')
}, 1000, true)

</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-box {
    width: 100%;
    max-width: 500px;
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

    .role-selection {
      margin: 30px 0;

      .role-card {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        min-height: 120px;
        border: none; /* 移除边框 */
        margin-bottom: 10px;

        &:hover {
          background: #e9ecef;
          transform: translateY(-5px);
          box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .role-icon {
          font-size: 32px;
          margin-bottom: 15px;
          color: #409eff;
        }

        .role-name {
          font-size: 16px;
          font-weight: bold;
          color: #333;
        }
      }

      // 在小屏幕上添加间距
      :deep(.el-col) {
        margin-bottom: 10px;
      }
    }

    .login-footer {
      text-align: center;

      p {
        margin: 0;
        color: #666;
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

      .role-selection {
        .role-card {
          background: #1d1e1f;

          &:hover {
            background: #2d2e2f;
          }

          .role-name {
            color: #fff;
          }
        }
      }

      .login-footer {
        p {
          color: #ccc;
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .login-container .login-box {
    padding: 20px;
    margin: 0 15px;
  }

  .role-selection {
    margin: 20px 0;
  }
}
</style>

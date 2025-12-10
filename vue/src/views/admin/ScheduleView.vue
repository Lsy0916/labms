<!-- 管理员课表页面 -->
<template>
  <div class="schedule-container">
    <el-card class="empty-schedule-card">
      <el-result
        icon="info"
        title="无课表信息"
        sub-title="作为管理员，您没有课程安排。请前往相应管理页面进行操作。"
      >
        <template #extra>
          <el-button type="primary" @click="goToDashboard">返回首页</el-button>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToDashboard = () => {
  router.push('/dashboard')
}
</script>

<style lang="scss" scoped>
.schedule-container {
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  
  .empty-schedule-card {
    width: 100%;
    max-width: 500px;
    
    :deep(.el-card__body) {
      padding: 40px 20px;
    }
  }
  
  // 深色主题适配
  html.dark {
    .empty-schedule-card {
      background-color: #1d1e1f;
      border: none;
      
      :deep(.el-card__body) {
        background-color: #1d1e1f;
      }
      
      :deep(.el-result__title) {
        color: #fff;
      }
      
      :deep(.el-result__subtitle) {
        color: #ccc;
      }
    }
  }
}
</style>
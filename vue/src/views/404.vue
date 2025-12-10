<!-- 404页面 -->
<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-graphic">
        <div class="error-code">404</div>
      </div>

      <div class="error-content">
        <h1>页面未找到</h1>
        <p class="error-description">抱歉，您访问的页面不存在或已被移除</p>
        <p class="countdown">将在 {{ countdown }} 秒后自动返回上一页</p>
        
        <div class="actions">
          <el-button type="primary" @click="goBack" size="large" round>
            <el-icon><Back /></el-icon>
            立即返回
          </el-button>
          <el-button @click="goHome" size="large" round>
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="background-pattern"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { Back, HomeFilled } from '@element-plus/icons-vue'
import { debounce } from '@/utils/debounce.js'

const router = useRouter()
const countdown = ref(5)

let timer = null

onMounted(() => {
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
      goBack()
    }
  }, 1000)
})

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }
})

const goBack = debounce(() => {
  router.go(-1)
}, 1000, true)

const goHome = debounce(() => {
  router.push('/')
}, 1000, true)
</script>

<style lang="scss" scoped>
.not-found-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;

  .not-found-content {
    text-align: center;
    max-width: 600px;
    z-index: 10;
    padding: 40px;
    
    @media (max-width: 768px) {
      padding: 20px;
      margin: 20px;
    }
  }

  .error-graphic {
    margin-bottom: 30px;
    
    .error-code {
      font-size: 96px;
      font-weight: 700;
      color: rgba(255, 255, 255, 0.9);
      line-height: 1;
      text-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
    }
  }

  .error-content {
    h1 {
      font-size: 36px;
      margin: 0 0 20px;
      color: white;
      font-weight: 600;
    }

    .error-description {
      font-size: 20px;
      color: rgba(255, 255, 255, 0.85);
      margin: 0 0 20px;
      line-height: 1.6;
    }
    
    .countdown {
      font-size: 18px;
      color: rgba(255, 255, 255, 0.9);
      margin: 0 0 40px;
      font-weight: 500;
    }

    .actions {
      display: flex;
      justify-content: center;
      gap: 20px;
      
      @media (max-width: 480px) {
        flex-direction: column;
        gap: 15px;
        
        .el-button {
          width: 100%;
        }
      }
    }
  }
  
  .background-pattern {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: 
      radial-gradient(circle at 10% 20%, rgba(255, 255, 255, 0.05) 0%, transparent 20%),
      radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.05) 0%, transparent 20%),
      radial-gradient(circle at 40% 40%, rgba(255, 255, 255, 0.05) 0%, transparent 20%);
    z-index: 1;
  }
}

// 深色主题适配
html.dark {
  .not-found-container {
    background: linear-gradient(135deg, #2c3e50 0%, #4a235a 100%);
  }
}
</style>
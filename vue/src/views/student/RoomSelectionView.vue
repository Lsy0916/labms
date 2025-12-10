<!-- 学生选机房页面 -->
<template>
  <div class="room-selection-container">
    <el-card class="room-selection-card">
      <template #header>
        <div class="card-header">
          <span>选择机房</span>
        </div>
      </template>

      <div class="room-list" v-loading="loading">
        <el-alert
          v-if="rooms.length === 0 && !loading"
          title="暂无可用机房"
          type="info"
          show-icon
          :closable="false"
        />

        <el-row :gutter="20">
          <el-col
            v-for="room in rooms"
            :key="room.id"
            :span="8"
            class="room-col"
          >
            <el-card
              class="room-card"
              :class="{ 'room-selected': selectedRoom?.id === room.id }"
              @click="selectRoom(room)"
              shadow="hover"
            >
              <div class="room-info">
                <div class="room-name">{{ room.name }}</div>
                <div class="room-details">
                  <div class="room-capacity">
                    <el-icon><User /></el-icon>
                    容量: {{ room.capacity }}人
                  </div>
                  <div class="room-time">
                    <el-icon><Clock /></el-icon>
                    {{ room.openTime }}-{{ room.closeTime }}
                  </div>
                </div>
                <div class="room-status">
                  <el-tag :type="room.status === 'active' ? 'success' : 'info'">
                    {{ room.status === 'active' ? '正常开放' : '暂停使用' }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="room-actions" v-if="rooms.length > 0">
        <el-button 
          type="primary" 
          @click="proceedToBooking"
          :disabled="!selectedRoom"
          size="large"
        >
          开始预约
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRoomStore } from '@/stores/room'
import { ElMessage } from 'element-plus'
import { User, Clock } from '@element-plus/icons-vue'

// 使用 store 和 router
const roomStore = useRoomStore()
const router = useRouter()

// 状态
const loading = ref(false)
const selectedRoom = ref(null)

// 计算属性
const rooms = computed(() => roomStore.rooms)

// 方法
const selectRoom = (room) => {
  if (room.status === 'active') {
    selectedRoom.value = room
  } else {
    ElMessage.warning('该机房当前不可用')
  }
}

const proceedToBooking = () => {
  if (!selectedRoom.value) {
    ElMessage.warning('请先选择一个机房')
    return
  }
  
  // 跳转到预约页面并传递选中的机房信息
  router.push({
    path: '/booking',
    query: { 
      roomId: selectedRoom.value.id 
    }
  })
}

// 生命周期
onMounted(async () => {
  try {
    loading.value = true
    await roomStore.fetchRooms()
    
    // 如果URL中有roomId参数，则自动选中对应机房
    const roomId = router.currentRoute.value.query.roomId
    if (roomId) {
      const room = rooms.value.find(r => r.id === parseInt(roomId))
      if (room) {
        selectedRoom.value = room
      }
    }
  } catch (error) {
    ElMessage.error('获取机房列表失败')
    console.error('获取机房列表失败:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.room-selection-container {
  padding: 20px;
}

.room-list {
  min-height: 300px;
}

.room-col {
  margin-bottom: 20px;
}

.room-card {
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.room-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.room-card.room-selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}

.room-info .room-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 15px;
  text-align: center;
  color: var(--el-text-color-primary);
}

.room-details {
  margin-bottom: 15px;
}

.room-capacity,
.room-time {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.room-status {
  text-align: center;
}

.room-actions {
  margin-top: 30px;
  text-align: center;
}

/* 深色主题适配 */
html.dark .room-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
}

html.dark .room-selection-card {
  background-color: #1d1e1f;
  color: #ffffff;
  border: none;
}

html.dark .room-selection-card :deep(.el-card__header) {
  background-color: #1d1e1f;
  border-color: #333;
  color: #ffffff;
  border: none;
}

html.dark .room-info .room-name {
  color: #ffffff;
}

html.dark .room-capacity,
html.dark .room-time {
  color: #ccc;
}

html.dark .el-alert {
  background-color: #1d1e1f;
  border: 1px solid #333;
}

html.dark .el-alert :deep(.el-alert__title) {
  color: #ffffff;
}

html.dark .room-card {
  background-color: #1d1e1f;
  color: #ffffff;
  border: 1px solid #333;
}

html.dark .room-card :deep(.el-card__body) {
  background-color: #1d1e1f;
  color: #ffffff;
}

html.dark .room-card.room-selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}
</style>
<template>
  <div class="seat-management-container">
    <el-card class="seat-management-card">
      <template #header>
        <div class="card-header">
          <span>座位管理</span>
          <div class="header-actions">
            <el-select
              v-model="selectedRoomId"
              placeholder="请选择机房"
              clearable
              class="room-selector"
              @change="handleRoomChange"
            >
              <el-option
                v-for="room in rooms"
                :key="room.roomId"
                :label="`${room.name} (${room.roomId})`"
                :value="room.roomId"
              />
            </el-select>
            <el-button
              type="primary"
              @click="refreshData"
              :icon="Refresh"
              :disabled="!selectedRoomId"
              circle
            />
          </div>
        </div>
      </template>

      <div v-if="!selectedRoomId" class="empty-placeholder">
        <el-empty description="请先选择一个机房">
          <el-button type="primary" @click="loadRooms">刷新机房列表</el-button>
        </el-empty>
      </div>

      <div v-else>
        <div class="seat-info-section">
          <el-descriptions :column="4" border size="small">
            <el-descriptions-item label="机房编号">{{ currentRoom.roomId }}</el-descriptions-item>
            <el-descriptions-item label="机房名称">{{ currentRoom.name }}</el-descriptions-item>
            <el-descriptions-item label="总座位数">
              <el-tag type="info">{{ currentRoom.totalSeats }} 个</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag :type="currentRoom.status === '正常' ? 'success' : 'danger'">
                {{ currentRoom.status }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="seat-map-section">
          <div class="seat-map-header">
            <h3>座位分布图</h3>
            <div class="seat-map-controls">
              <el-button
                type="primary"
                @click="openBatchOperation('broken')"
                :disabled="selectedSeats.length === 0"
              >
                设为故障
              </el-button>
              <el-button
                type="success"
                @click="openBatchOperation('available')"
                :disabled="selectedSeats.length === 0"
              >
                设为可用
              </el-button>
              <el-button @click="clearSelection">清空选择</el-button>
            </div>
          </div>

          <div class="seat-map-wrapper">
            <SeatMap
              :seat-template="seats"
              :occupied="[]"
              :broken="brokenSeats"
              v-model:selectedSeats="selectedSeats"
            />
          </div>
        </div>

        <div class="seat-list-section">
          <h3>座位列表</h3>
          <el-table
            :data="seats"
            v-loading="loading"
            style="width: 100%"
            stripe
            border
            height="400"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="座位编号" width="120" align="center" />
            <el-table-column prop="status" label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getSeatStatusType(row.status)">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template #default="{ row }">
                <el-button
                  v-if="row.status !== '故障'"
                  type="warning"
                  size="small"
                  @click="setSeatStatus(row, 'broken')"
                >
                  设为故障
                </el-button>
                <el-button
                  v-else
                  type="success"
                  size="small"
                  @click="setSeatStatus(row, 'available')"
                >
                  设为可用
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>

    <!-- 批量操作确认对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      :title="`批量设置座位状态为${batchOperationType === 'broken' ? '故障' : '可用'}`"
      width="500px"
    >
      <p>确定要将选中的 {{ selectedSeats.length }} 个座位设置为{{ batchOperationType === 'broken' ? '故障' : '空闲' }}状态吗？</p>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button
            :type="batchOperationType === 'broken' ? 'warning' : 'success'"
            @click="confirmBatchOperation"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getComputerLabs, getSeatsByRoomId, updateSeatStatus } from '@/api/room'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SeatMap from '@/components/SeatMap.vue'

// 数据
const rooms = ref([])
const selectedRoomId = ref('')
const seats = ref([])
const brokenSeats = ref([])
const selectedSeats = ref([])
const loading = ref(false)
const batchDialogVisible = ref(false)
const batchOperationType = ref('')

// 计算属性
const currentRoom = computed(() => {
  return rooms.value.find(room => room.roomId === selectedRoomId.value) || {}
})

// 方法
const loadRooms = async () => {
  try {
    const response = await getComputerLabs()
    if (response.status === 200) {
      rooms.value = response.data
    } else {
      ElMessage.error('获取机房列表失败: ' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取机房列表异常: ' + error.message)
  }
}

const handleRoomChange = async (roomId) => {
  if (!roomId) {
    seats.value = []
    brokenSeats.value = []
    selectedSeats.value = []
    return
  }

  loading.value = true
  try {
    // 获取座位信息
    const response = await getSeatsByRoomId(roomId)
    if (response.status === 200) {
      // 初始化座位数据
      const seatData = []
      const brokenSeatIds = []

      response.data.forEach(seat => {
        seatData.push({
          id: seat.seatId,
          status: seat.status === '可用' ? 'available' :
                 seat.status === '占用' ? 'occupied' :
                 seat.status === '故障' ? 'broken' : 'available'
        })

        if (seat.status === '故障') {
          brokenSeatIds.push(seat.seatId)
        }
      })

      seats.value = seatData
      brokenSeats.value = brokenSeatIds
      selectedSeats.value = []
    } else {
      ElMessage.error('获取座位信息失败: ' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取座位信息异常: ' + error.message)
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadRooms()
  if (selectedRoomId.value) {
    handleRoomChange(selectedRoomId.value)
  }
}

// 获取座位状态对应的 Element UI 类型
const getSeatStatusType = (status) => {
  // 根据标签返回对应的 Element UI 类型
  const statusMap = {
    'available': 'success',
    'selected': 'primary',
    'occupied': 'warning',
    'broken': 'danger'
  }

  return statusMap[status] || 'success'
}

// 获取座位状态的中文标签
const getStatusLabel = (status) => {
  const statusLabelMap = {
    'available': '空闲',
    'selected': '已选',
    'occupied': '占用',
    'broken': '故障'
  }

  return statusLabelMap[status] || '空闲'
}



const handleSelectionChange = (selection) => {
  selectedSeats.value = selection.map(seat => seat.id)
}

const clearSelection = () => {
  selectedSeats.value = []
}

const setSeatStatus = (seat, status) => {
  ElMessageBox.confirm(
    `确定要将座位 ${seat.id} 设置为${status === 'broken' ? '故障' : '空闲'}状态吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 调用API更新座位状态
      const response = await updateSeatStatus({
        roomId: selectedRoomId.value,
        seatId: seat.id,
        status: status === 'broken' ? '故障' : '空闲'
      })

      if (response.status === 200) {
        // 更新本地数据
        const seatIndex = seats.value.findIndex(s => s.id === seat.id)
        if (seatIndex > -1) {
          seats.value[seatIndex].status = status === 'broken' ? 'broken' : 'available'

          if (status === 'broken') {
            if (!brokenSeats.value.includes(seat.id)) {
              brokenSeats.value.push(seat.id)
            }
          } else {
            const index = brokenSeats.value.indexOf(seat.id)
            if (index > -1) {
              brokenSeats.value.splice(index, 1)
            }
          }
        }
        ElMessage.success('座位状态更新成功')
      } else {
        ElMessage.error('座位状态更新失败: ' + response.message)
      }
    } catch (error) {
      ElMessage.error('座位状态更新异常: ' + error.message)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

const openBatchOperation = (type) => {
  batchOperationType.value = type
  batchDialogVisible.value = true
}

const confirmBatchOperation = async () => {
  const status = batchOperationType.value === 'broken' ? 'broken' : 'available'
  const statusLabel = batchOperationType.value === 'broken' ? '故障' : '空闲'

  try {
    // 调用API批量更新座位状态
    const response = await updateSeatStatus({
      roomId: selectedRoomId.value,
      seatIds: selectedSeats.value,
      status: batchOperationType.value === 'broken' ? '故障' : '空闲'
    })

    if (response.status === 200) {
      // 更新本地数据
      selectedSeats.value.forEach(seatId => {
        const seatIndex = seats.value.findIndex(s => s.id === seatId)
        if (seatIndex > -1) {
          seats.value[seatIndex].status = status
        }

        if (status === 'broken') {
          if (!brokenSeats.value.includes(seatId)) {
            brokenSeats.value.push(seatId)
          }
        } else {
          const index = brokenSeats.value.indexOf(seatId)
          if (index > -1) {
            brokenSeats.value.splice(index, 1)
          }
        }
      })

      ElMessage.success(`成功将 ${selectedSeats.value.length} 个座位设置为${statusLabel}状态`)
    } else {
      ElMessage.error('座位状态更新失败: ' + response.message)
    }
  } catch (error) {
    ElMessage.error('座位状态更新异常: ' + error.message)
  }

  batchDialogVisible.value = false
  selectedSeats.value = []
}

// 生命周期
onMounted(() => {
  loadRooms()
})
</script>

<style lang="scss" scoped>
.seat-management-container {
  padding: 20px;
}

.seat-management-card {
  min-height: calc(100vh - 120px);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  :deep(.el-card__header) {
    padding: 20px 24px;
    background-color: #f5f7fa;
    border-bottom: 1px solid #ebeef5;
    border-radius: 8px 8px 0 0 !important;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: #303133;

  .header-actions {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .room-selector {
    width: 300px;
  }
}

.empty-placeholder {
  padding: 60px 0;
}

.seat-info-section {
  margin-bottom: 30px;
}

.seat-map-section {
  margin-bottom: 30px;

  .seat-map-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      margin: 0;
    }

    .seat-map-controls {
      display: flex;
      gap: 12px;
    }
  }

  .seat-map-wrapper {
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
  }
}

.seat-list-section {
  h3 {
    margin: 0 0 20px 0;
  }
}

// 深色主题适配
html.dark {
  .seat-management-card {
    background-color: #1d1e1f;
    border: none;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);

    :deep(.el-card__header) {
      background-color: #2d2e2f;
      border-color: #333;
      color: #ffffff;
    }

    :deep(.el-card__body) {
      background-color: #1d1e1f;
      color: #ffffff;
    }
  }

  .card-header {
    color: #ffffff;
  }

  .seat-map-wrapper {
    background: #1d1e1f;
  }
}

@media (max-width: 768px) {
  .seat-management-container {
    padding: 12px;
  }

  .card-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;

    .header-actions {
      justify-content: space-between;
      flex-wrap: wrap;
    }

    .room-selector {
      width: 100%;
    }
  }
}
</style>

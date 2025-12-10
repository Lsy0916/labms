<template>
  <div class="reservation-management-container">
    <el-card class="reservation-card">
      <template #header>
        <div class="card-header">
          <span>预约管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索预约 (预约编号/机房/用户ID)"
              clearable
              style="width: 300px; margin-right: 10px;"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="loadReservations">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="filteredReservations"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column label="编号" width="60">
          <template #default="{ $index }">
            {{ $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="reservationId" label="预约编号" width="120" sortable />
        <el-table-column prop="roomId" label="机房" width="80" sortable />
        <el-table-column prop="seatId" label="座位号" width="80" sortable>
          <template #default="{ row }">
            <span v-if="row.seatId && row.seatId !== '-1'">{{ row.seatId }}</span>
            <span v-else>整机房</span>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="120" sortable />
        <el-table-column prop="reservationDate" label="预约日期" width="120" sortable />
        <el-table-column prop="startTime" label="开始时间" width="100" sortable />
        <el-table-column prop="endTime" label="结束时间" width="100" sortable />
        <el-table-column prop="status" label="状态" width="100" sortable :filters="[
          { text: '待确认', value: '待确认' },
          { text: '已确认', value: '已确认' },
          { text: '已完成', value: '已完成' },
          { text: '已取消', value: '已取消' },
          { text: '已过期', value: '已过期' }
        ]" :filter-method="filterStatus">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" sortable />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-select
              v-model="row.newStatus"
              placeholder="选择状态"
              size="small"
              @change="status => handleUpdateReservationStatus(row, status)"
            >
              <el-option
                v-for="status in reservationStatusOptions"
                :key="status.value"
                :label="status.label"
                :value="status.value"
              />
            </el-select>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredReservations.length === 0 && !loading" description="暂无预约记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { getAllReservations, updateReservationStatus } from '@/api/reservation'

// 数据
const reservations = ref([])
const loading = ref(false)
const searchKeyword = ref('')

// 预约状态选项
const reservationStatusOptions = [
  { label: '待确认', value: '待确认' },
  { label: '已确认', value: '已确认' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' },
  { label: '已驳回', value: '已驳回' },
  { label: '已过期', value: '已过期' }
]

// 过滤后的预约数据
const filteredReservations = computed(() => {
  if (!searchKeyword.value) {
    return reservations.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return reservations.value.filter(reservation =>
    reservation.reservationId.toLowerCase().includes(keyword) ||
    reservation.roomId.toLowerCase().includes(keyword) ||
    reservation.userId.toLowerCase().includes(keyword)
  )
})

// 方法
const getStatusTagType = (status) => {
  const statusMap = {
    '已确认': 'success',  // 绿色
    '待确认': 'primary',  // 蓝色
    '已完成': 'info',     // 灰色
    '已取消': 'danger',   // 红色
    '已驳回': 'danger',   // 红色
    '已过期': 'warning'   // 黄色
  }
  return statusMap[status] || 'info'
}

// 状态筛选方法
const filterStatus = (value, row) => {
  return row.status === value
}

// 搜索处理
const handleSearch = () => {
  // 搜索已在 computed 中处理
}

// 加载所有预约信息
const loadReservations = async () => {
  try {
    loading.value = true

    // 调用 API 获取所有预约信息
    const response = await getAllReservations()

    if (response.status === 200) {
      reservations.value = response.data.map(reservation => ({
        ...reservation,
        newStatus: reservation.status // 添加用于状态变更的字段
      }))
    } else {
      ElMessage.error(response.message || '获取预约信息失败')
    }
  } catch (error) {
    console.error('获取预约信息异常:', error)
    ElMessage.error('获取预约信息失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 更新预约状态
const handleUpdateReservationStatus = async (row, status) => {
  try {
    // 检查状态转换是否合法
    const finalStatus = row.status;
    if ((finalStatus === '已取消' && status !== '已取消') ||
        (finalStatus === '已过期' && status !== '已过期') ||
        (finalStatus === '已完成' && status !== '已完成')) {
      ElMessage.warning(`无法将状态从"${finalStatus}"更改为"${status}"`);
      // 恢复原状态
      row.newStatus = row.status;
      return;
    }

    await ElMessageBox.confirm(
      `确定要将预约 ${row.reservationId} 的状态更新为 ${status} 吗？`,
      '确认更新',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await updateReservationStatus(row.reservationId, status)

    if (response.status === 200) {
      ElMessage.success('预约状态更新成功')
      // 更新本地状态
      row.status = status
    } else {
      ElMessage.error(response.message || '预约状态更新失败')
      // 恢复原状态
      row.newStatus = row.status
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新预约状态异常:', error)
      ElMessage.error('预约状态更新失败: ' + error.message)
      // 恢复原状态
      row.newStatus = row.status
    }
  }
}

// 生命周期
onMounted(async () => {
  await loadReservations()
})
</script>

<style lang="scss" scoped>
.reservation-management-container {
  .reservation-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .header-actions {
      display: flex;
      align-items: center;
    }
  }
}

// 深色模式适配
html.dark {
  .reservation-management-container {
    .reservation-card {
      background-color: #1d1e1f;
      border: 1px solid #333;

      :deep(.el-card__header) {
        background-color: #1d1e1f;
        border-color: #333;
        color: #ffffff;
      }

      :deep(.el-table) {
        background-color: #1d1e1f;

        th {
          background-color: #2d2e2f;
          color: #ffffff;
        }

        tr {
          background-color: #1d1e1f;

          &.el-table__row--striped {
            td {
              background-color: #252627;
            }
          }

          &:hover {
            td {
              background-color: #2d2e2f;
            }
          }
        }

        td {
          background-color: #1d1e1f;
          color: #ffffff;
          border-color: #333;
        }

        .el-table__inner-wrapper::before {
          background-color: #333;
        }

        .el-table__border-left {
          border-color: #333;
        }
      }

      :deep(.el-tag) {
        &.el-tag--success {
          --el-tag-bg-color: #2d2e2f;
          --el-tag-border-color: #67c23a;
          --el-tag-text-color: #67c23a;
        }

        &.el-tag--primary {
          --el-tag-bg-color: #2d2e2f;
          --el-tag-border-color: #409eff;
          --el-tag-text-color: #409eff;
        }

        &.el-tag--danger {
          --el-tag-bg-color: #2d2e2f;
          --el-tag-border-color: #f56c6c;
          --el-tag-text-color: #f56c6c;
        }

        &.el-tag--info {
          --el-tag-bg-color: #2d2e2f;
          --el-tag-border-color: #909399;
          --el-tag-text-color: #909399;
        }

        &.el-tag--warning {
          --el-tag-bg-color: #2d2e2f;
          --el-tag-border-color: #e6a23c;
          --el-tag-text-color: #e6a23c;
        }
      }
    }
  }
}
</style>

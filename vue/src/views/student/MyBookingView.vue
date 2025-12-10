<!-- 我的预约页面 -->
<template>
  <div class="my-booking-container">
    <el-card class="booking-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索预约 (预约编号/机房/座位号)"
              clearable
              style="width: 300px; margin-right: 10px;"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="loadBookings">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="filteredBookings"
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
        <el-table-column prop="reservationDate" label="预约日期" width="120" sortable />
        <el-table-column prop="startTime" label="开始时间" width="100" sortable />
        <el-table-column prop="endTime" label="结束时间" width="100" sortable />
        <el-table-column prop="seatId" label="座位号" width="80" sortable>
          <template #default="{ row }">
            <span v-if="row.seatId && row.seatId !== '-1'">{{ row.seatId }}</span>
            <span v-else>整机房</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" sortable :filters="[
          { text: '待确认', value: '待确认' },
          { text: '已确认', value: '已确认' },
          { text: '已完成', value: '已完成' },
          { text: '已取消', value: '已取消' },
          { text: '已驳回', value: '已驳回' },
          { text: '已过期', value: '已过期' }
        ]" :filter-method="filterStatus">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" sortable />
        <el-table-column label="操作" width="120">
          <template #default="{ row, $index }">
            <el-button
              v-if="row.status === '待确认'"
              type="danger"
              size="small"
              @click="handleCancelReservation(row, $index)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredBookings.length === 0 && !loading" description="暂无预约记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getUserReservations, cancelReservation } from '@/api/room'

// 使用 store
const authStore = useAuthStore()
const userId = authStore.userInfo.userId

// 数据
const bookings = ref([])
const loading = ref(false)
const searchKeyword = ref('')

// 过滤后的预约数据
const filteredBookings = computed(() => {
  if (!searchKeyword.value) {
    return bookings.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return bookings.value.filter(booking =>
    booking.reservationId.toLowerCase().includes(keyword) ||
    booking.roomId.toLowerCase().includes(keyword) ||
    (booking.seatId && booking.seatId.toString().includes(keyword))
  )
})

// 方法
const getTagType = (status) => {
  const statusMap = {
    '已确认': 'success', // 绿色
    '待确认': 'primary', // 蓝色
    '已完成': 'info',    // 灰色
    '已取消': 'danger',  // 红色
    '已驳回': 'danger',  // 红色
    '已过期': 'warning'  // 黄色
  }
  return statusMap[status] || 'info' // 默认使用info类型
}

// 状态筛选方法
const filterStatus = (value, row) => {
  return row.status === value
}

// 搜索处理
const handleSearch = () => {
  // 搜索已在 computed 中处理
}

const loadBookings = async () => {
  try {
    loading.value = true

    // 调用 API 获取用户预约信息
    const response = await getUserReservations(userId)

    if (response.status === 200 || response.statusText === 'OK' || response.success === true) {
      bookings.value = response.data
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

// 取消预约
const handleCancelReservation = async (row, index) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消第 ${index + 1} 号预约吗？`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await cancelReservation({
      reservationId: row.reservationId,
      userId
    })

    if (response.status === 200 || response.statusText === 'OK' || response.success === true) {
      ElMessage.success('预约取消成功')
      // 重新加载预约列表
      await loadBookings()
    } else {
      ElMessage.error(response.message || '取消预约失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约异常:', error)
      ElMessage.error('取消预约失败: ' + error.message)
    }
  }
}

// 生命周期
onMounted(async () => {
  await loadBookings()
})
</script>

<style lang="scss" scoped>
.my-booking-container {
  .booking-card {
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
  .my-booking-container {
    .booking-card {
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

<template>
  <div class="reservation-audit-container">
    <el-card class="reservation-card">
      <template #header>
        <div class="card-header">
          <span>预约审核</span>
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
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === '待确认'"
                type="success"
                size="small"
                @click="auditReservation(row, '已确认')"
              >
                通过
              </el-button>
              <el-button
                v-if="row.status === '待确认'"
                type="danger"
                size="small"
                @click="auditReservation(row, '已驳回')"
              >
                拒绝
              </el-button>
              <el-button
                v-if="row.status !== '待确认'"
                type="primary"
                size="small"
                plain
                @click="viewReservationDetails(row)"
              >
                详情
              </el-button>
              <el-dropdown 
                v-if="row.status !== '待确认'" 
                @command="command => handleDropdownCommand(command, row)"
              >
                <el-button size="small" type="primary">
                  更多<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">修改状态</el-dropdown-item>
                    <el-dropdown-item command="delete">删除预约</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredReservations.length === 0 && !loading" description="暂无预约记录" />
    </el-card>

    <!-- 预约详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预约详情"
      width="600px"
      class="detail-dialog"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约编号">{{ selectedReservation.reservationId }}</el-descriptions-item>
        <el-descriptions-item label="机房">{{ selectedReservation.roomId }}</el-descriptions-item>
        <el-descriptions-item label="座位号">
          <span v-if="selectedReservation.seatId && selectedReservation.seatId !== '-1'">
            {{ selectedReservation.seatId }}
          </span>
          <span v-else>整机房</span>
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ selectedReservation.userId }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ selectedReservation.reservationDate }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ selectedReservation.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ selectedReservation.endTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(selectedReservation.status)">
            {{ selectedReservation.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedReservation.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ selectedReservation.updatedAt }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改状态对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="修改预约状态"
      width="500px"
      class="status-dialog"
    >
      <el-form
        :model="statusForm"
        :rules="statusFormRules"
        ref="statusFormRef"
        label-width="100px"
      >
        <el-form-item label="当前状态">
          <el-tag :type="getStatusTagType(selectedReservation.status)">
            {{ selectedReservation.status }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态" prop="newStatus">
          <el-select v-model="statusForm.newStatus" placeholder="请选择新状态" style="width: 100%">
            <el-option
              v-for="status in reservationStatusOptions"
              :key="status.value"
              :label="status.label"
              :value="status.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateReservationStatus">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import {
  getAllReservations,
  updateReservationStatus,
  deleteReservation
} from '@/api/reservation'

// 数据
const reservations = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const detailDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const statusFormRef = ref()

// 表单数据
const selectedReservation = ref({})
const statusForm = ref({
  newStatus: ''
})

// 表单验证规则
const statusFormRules = {
  newStatus: [
    { required: true, message: '请选择新状态', trigger: 'change' }
  ]
}

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
      reservations.value = response.data
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

// 审核预约
const auditReservation = async (row, status) => {
  try {
    // 检查状态转换是否合法
    const finalStatus = row.status;
    if ((finalStatus === '已取消' && status !== '已取消') ||
        (finalStatus === '已过期' && status !== '已过期') ||
        (finalStatus === '已完成' && status !== '已完成')) {
      ElMessage.warning(`无法将状态从"${finalStatus}"更改为"${status}"`);
      return;
    }

    const actionText = status === '已确认' ? '通过' : (status === '已驳回' ? '驳回' : '拒绝')
    
    await ElMessageBox.confirm(
      `确定要${actionText}预约 ${row.reservationId} 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await updateReservationStatus(row.reservationId, status)

    if (response.status === 200) {
      ElMessage.success(`预约${actionText}成功`)
      // 更新本地状态
      row.status = status
    } else {
      ElMessage.error(response.message || `预约${actionText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核预约异常:', error)
      ElMessage.error('审核预约失败: ' + error.message)
    }
  }
}

// 查看预约详情
const viewReservationDetails = (row) => {
  selectedReservation.value = { ...row }
  detailDialogVisible.value = true
}

// 处理下拉菜单命令
const handleDropdownCommand = (command, row) => {
  selectedReservation.value = { ...row }
  switch (command) {
    case 'edit':
      statusForm.value.newStatus = row.status
      statusDialogVisible.value = true
      break
    case 'delete':
      handleDeleteReservation(row)
      break
  }
}

// 更新预约状态
const handleUpdateReservationStatus = async () => {
  try {
    // 检查状态转换是否合法
    const finalStatus = selectedReservation.value.status;
    if ((finalStatus === '已取消' && statusForm.value.newStatus !== '已取消') ||
        (finalStatus === '已过期' && statusForm.value.newStatus !== '已过期') ||
        (finalStatus === '已完成' && statusForm.value.newStatus !== '已完成')) {
      ElMessage.warning(`无法将状态从"${finalStatus}"更改为"${statusForm.value.newStatus}"`);
      return;
    }

    // 表单验证
    if (!statusForm.value.newStatus) {
      ElMessage.warning('请选择新状态');
      return;
    }

    const response = await updateReservationStatus(
      selectedReservation.value.reservationId,
      statusForm.value.newStatus
    )

    if (response.status === 200) {
      ElMessage.success('预约状态更新成功')
      statusDialogVisible.value = false
      
      // 更新本地状态
      const index = reservations.value.findIndex(r => r.reservationId === selectedReservation.value.reservationId)
      if (index !== -1) {
        reservations.value[index].status = statusForm.value.newStatus
      }
    } else {
      ElMessage.error(response.message || '预约状态更新失败')
    }
  } catch (error) {
    console.error('更新预约状态异常:', error)
    ElMessage.error('预约状态更新失败: ' + error.message)
  }
}

// 删除预约
const handleDeleteReservation = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除预约 ${row.reservationId} 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteReservation(row.reservationId)

    if (response.status === 200) {
      ElMessage.success('预约删除成功')
      // 从本地列表中移除
      const index = reservations.value.findIndex(r => r.reservationId === row.reservationId)
      if (index !== -1) {
        reservations.value.splice(index, 1)
      }
    } else {
      ElMessage.error(response.message || '预约删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除预约异常:', error)
      ElMessage.error('预约删除失败: ' + error.message)
    }
  }
}

// 生命周期
onMounted(async () => {
  await loadReservations()
})
</script>

<style lang="scss" scoped>
.reservation-audit-container {
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
    
    .action-buttons {
      display: flex;
      gap: 5px;
      flex-wrap: wrap;
    }
  }
}

// 深色模式适配
html.dark {
  .reservation-audit-container {
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
    
    .detail-dialog,
    .status-dialog {
      :deep(.el-dialog) {
        background-color: #1d1e1f;
      }

      :deep(.el-dialog__header) {
        background-color: #1d1e1f;
        border-color: #333;
        color: #ffffff;
      }

      :deep(.el-dialog__body) {
        background-color: #1d1e1f;
        color: #ffffff;
      }

      :deep(.el-dialog__footer) {
        background-color: #1d1e1f;
        border-color: #333;
      }

      :deep(.el-form-item__label) {
        color: #ffffff;
      }
    }
  }
}
</style>
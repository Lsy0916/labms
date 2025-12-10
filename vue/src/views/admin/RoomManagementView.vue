<template>
  <div class="room-management-container">
    <el-card class="room-management-card">
      <template #header>
        <div class="card-header">
          <span>机房管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showAddForm" :icon="Plus">新增机房</el-button>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索机房 (ID/名称/位置)"
              clearable
              class="search-input"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="refreshData" :icon="Refresh" circle />
          </div>
        </div>
      </template>

      <!-- 机房列表表格 -->
      <el-table
        :data="filteredRooms"
        v-loading="loading"
        element-loading-text="加载中..."
        style="width: 100%"
        stripe
        border
        class="room-table"
        :row-class-name="tableRowClassName"
        highlight-current-row
        @row-click="handleRowClick"
      >
        <el-table-column prop="id" label="编号" width="80" align="center" sortable />
        <el-table-column prop="roomId" label="机房号" width="100" align="center" sortable />
        <el-table-column prop="name" label="机房名" min-width="120" sortable>
          <template #default="{ row }">
            <span class="room-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="equipmentInfo" label="机房信息" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="equipment-info">{{ row.equipmentInfo || '暂无设备信息' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="totalSeats" label="座位数" width="80" sortable align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.totalSeats }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="niceSeats" label="可用座位数" width="100" sortable align="center">
          <template #default="{ row }">
            <el-tag type="success">{{ row.niceSeats }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="availableSeats" label="可预约座位数" width="120" sortable align="center">
          <template #default="{ row }">
            <el-tag type="primary">{{ row.availableSeats }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="managerId" label="负责人ID" width="120" align="center" sortable />
        <el-table-column prop="status" label="机房状态" width="100" align="center" sortable>
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click.stop="viewDetails(row)" title="查看详情">
                <el-icon><View /></el-icon>
              </el-button>
              <el-button type="warning" size="small" @click.stop="showEditForm(row)" title="编辑">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="danger" size="small" @click.stop="deleteRoomHandler(row)" title="删除">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑机房对话框 -->
    <el-dialog
      v-model="roomDialogVisible"
      :title="(isEditMode ? '编辑' : '新增') + '机房'"
      width="700px"
      class="room-dialog"
      :before-close="handleDialogClose"
    >
      <el-form
        :model="currentRoom"
        :rules="roomFormRules"
        ref="roomFormRef"
        label-width="110px"
        label-position="right"
        :disabled="formLoading"
        @submit.prevent="submitRoomForm"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="机房号" prop="roomId">
              <el-input
                v-model="currentRoom.roomId"
                placeholder="请输入机房号"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机房名称" prop="name">
              <el-input
                v-model="currentRoom.name"
                placeholder="请输入机房名称"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="座位数" prop="totalSeats">
              <el-input-number
                v-model="currentRoom.totalSeats"
                :min="0"
                :max="1000"
                style="width: 100%"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="允许角色" prop="allowedRoles">
              <el-select v-model="currentRoom.allowedRoles" placeholder="请选择允许的角色" style="width: 100%">
                <el-option label="全部" value="all"></el-option>
                <el-option label="学生" value="student"></el-option>
                <el-option label="教师" value="teacher"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="currentRoom.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" value="正常"></el-option>
                <el-option label="停用" value="停用"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人ID" prop="managerId">
              <el-input
                v-model="currentRoom.managerId"
                placeholder="请输入负责人ID"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="设备信息" prop="equipmentInfo">
          <el-input
            v-model="currentRoom.equipmentInfo"
            type="textarea"
            :rows="4"
            placeholder="请输入设备信息"
            resize="vertical"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelRoomForm">取消</el-button>
          <el-button type="primary" @click="submitRoomForm" :loading="formLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 机房详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="机房详情"
      width="700px"
      class="detail-dialog"
    >
      <el-descriptions :column="2" border size="large">
        <el-descriptions-item label="编号">{{ selectedRoom.id }}</el-descriptions-item>
        <el-descriptions-item label="机房号">{{ selectedRoom.roomId }}</el-descriptions-item>
        <el-descriptions-item label="机房名称">
          <el-tag type="primary">{{ selectedRoom.name }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="座位数">
          <el-tag type="info">{{ selectedRoom.totalSeats }} 个</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="可用座位数">
          <el-tag type="success">{{ selectedRoom.niceSeats }} 个</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="可预约座位数">
          <el-tag type="primary">{{ selectedRoom.availableSeats }} 个</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="负责人ID">
          {{ selectedRoom.managerId }}
        </el-descriptions-item>
        <el-descriptions-item label="允许角色">
          <el-tag :type="selectedRoom.allowedRoles === 'all' ? 'primary' : 
                   selectedRoom.allowedRoles === 'student' ? 'success' : 'warning'">
            {{ selectedRoom.allowedRoles === 'all' ? '全部' : 
               selectedRoom.allowedRoles === 'student' ? '学生' : '教师' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="selectedRoom.status === '正常' ? 'success' : 'danger'">
            {{ selectedRoom.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDate(selectedRoom.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ formatDate(selectedRoom.updatedAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="设备信息" :span="2">
          <div class="equipment-detail">{{ selectedRoom.equipmentInfo || '暂无设备信息' }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getComputerLabs, createComputerLab, updateComputerLab, deleteComputerLab } from '@/api/room'
import { Search, Refresh, Plus, Delete, Edit, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 数据
const rooms = ref([])
const loading = ref(false)
const formLoading = ref(false)
const searchKeyword = ref('')
const roomDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const roomFormRef = ref()
const isEditMode = ref(false)

// 当前机房数据（用于新增/编辑）
const currentRoom = ref({
  id: '',
  roomId: '',
  name: '',
  equipmentInfo: '',
  totalSeats: 0,
  niceSeats: 0,
  availableSeats: 0,
  allowedRoles: 'all',
  managerId: '',
  status: '正常'
})

// 选中的机房（用于查看详情）
const selectedRoom = ref({})

// 表单验证规则
const roomFormRules = {
  roomId: [
    { required: true, message: '请输入机房号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入机房名称', trigger: 'blur' }
  ],
  totalSeats: [
    { required: true, message: '请输入座位数', trigger: 'blur' }
  ],
  managerId: [
    { required: true, message: '请输入负责人ID', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择机房状态', trigger: 'change' }
  ]
}

// 搜索过滤后的机房数据
const filteredRooms = computed(() => {
  if (!searchKeyword.value) {
    return rooms.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return rooms.value.filter(room =>
    (room.id && room.id.toString().toLowerCase().includes(keyword)) ||
    (room.roomId && room.roomId.toLowerCase().includes(keyword)) ||
    (room.name && room.name.toLowerCase().includes(keyword)) ||
    (room.managerId && room.managerId.toLowerCase().includes(keyword))
  )
})

// 获取机房列表
const fetchRooms = async () => {
  loading.value = true
  try {
    const response = await getComputerLabs()

    if (response.status === 200) {
      // 直接使用后端返回的数据，无需转换
      rooms.value = response.data
    } else {
      console.error('获取机房列表失败:', response.message)
      ElMessage.error('获取机房列表失败: ' + response.message)
    }
  } catch (error) {
    console.error('获取机房列表异常:', error)
    ElMessage.error('获取机房列表异常: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  // 搜索已在 computed 中处理
}

// 刷新数据
const refreshData = () => {
  fetchRooms()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  if (rowIndex % 4 === 0) {
    return 'highlight-row'
  }
  return ''
}

// 表格行点击事件
const handleRowClick = (row) => {
  viewDetails(row)
}

// 显示新增表单
const showAddForm = () => {
  isEditMode.value = false
  // 重置表单数据
  currentRoom.value = {
    id: '',
    roomId: '',
    name: '',
    equipmentInfo: '',
    totalSeats: 0,
    niceSeats: 0,
    availableSeats: 0,
    allowedRoles: 'all',
    managerId: '',
    status: '正常'
  }
  roomDialogVisible.value = true
}

// 显示编辑表单
const showEditForm = (room) => {
  isEditMode.value = true
  // 设置当前机房数据
  currentRoom.value = { ...room }
  roomDialogVisible.value = true
}

// 提交机房表单
const submitRoomForm = async () => {
  if (!roomFormRef.value) return

  try {
    await roomFormRef.value.validate()
    formLoading.value = true

    let response
    if (isEditMode.value) {
      // 编辑机房
      response = await updateComputerLab(currentRoom.value)
    } else {
      // 新增机房
      const { id, ...newRoomData } = currentRoom.value
      response = await createComputerLab(newRoomData)
    }

    if (response.status === 200) {
      ElMessage.success(`${isEditMode.value ? '编辑' : '新增'}机房成功`)
      roomDialogVisible.value = false
      fetchRooms() // 重新加载数据
    } else {
      ElMessage.error(`${isEditMode.value ? '编辑' : '新增'}机房失败: ` + response.message)
    }
  } catch (error) {
    console.error(`${isEditMode.value ? '编辑' : '新增'}机房异常:`, error)
    ElMessage.error(`${isEditMode.value ? '编辑' : '新增'}机房异常: ` + (error.message || error))
  } finally {
    formLoading.value = false
  }
}

// 取消表单
const cancelRoomForm = () => {
  roomDialogVisible.value = false
}

// 关闭对话框前的处理
const handleDialogClose = (done) => {
  ElMessageBox.confirm('确定要关闭对话框吗？未保存的数据将会丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    done()
  }).catch(() => {
    // 用户取消关闭
  })
}

// 查看详情
const viewDetails = (room) => {
  selectedRoom.value = room
  detailDialogVisible.value = true
}

// 删除机房
const deleteRoomHandler = (room) => {
  ElMessageBox.confirm(
    `确定要删除机房"<strong>${room.name}</strong>"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
    }
  ).then(async () => {
    try {
      const response = await deleteComputerLab(room.roomId)

      if (response.status === 200) {
        ElMessage.success('删除成功')
        fetchRooms() // 重新加载数据
      } else {
        ElMessage.error('删除失败: ' + response.message)
      }
    } catch (error) {
      console.error('删除机房异常:', error)
      ElMessage.error('删除机房异常: ' + (error.message || error))
    }
  }).catch(() => {
    // 用户取消删除
    ElMessage.info('已取消删除')
  })
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '无'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchRooms()
})

</script>

<style lang="scss" scoped>
.room-management-container {
  padding: 20px;
}

.room-management-card {
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

  .search-input {
    width: 320px;
  }
}

/* 表格样式优化 */
.room-table {
  margin-top: 16px;
  border-radius: 4px;

  :deep(.el-table__header th) {
    background-color: #f5f7fa;
    color: #606266;
    font-weight: 600;
    padding: 12px 0;
  }

  :deep(.el-table__row td) {
    padding: 12px 0;
  }

  :deep(.highlight-row) {
    background-color: #f0f9eb;
  }

  :deep(.room-name) {
    font-weight: 600;
    color: #409eff;
    font-size: 14px;
  }

  :deep(.equipment-info) {
    font-size: 13px;
    color: #606266;
    line-height: 1.4;
  }

  :deep(.operation-buttons) {
    display: flex;
    gap: 12px;
    justify-content: center;
  }
}

/* 详情对话框样式 */
:deep(.equipment-detail),
:deep(.description-detail) {
  line-height: 1.6;
  color: #606266;
  font-size: 14px;
  white-space: pre-wrap;
}

/* 深色主题适配 */
html.dark {
  .room-management-card {
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

  .room-table {
    :deep(.el-table__header th) {
      background-color: #2d2e2f;
      color: #ffffff;
    }

    :deep(.el-table__row td) {
      background-color: #1d1e1f;
      color: #ffffff;
    }

    :deep(.el-table__row--striped td) {
      background-color: #2a2b2c !important;
    }

    :deep(.el-table) {
      background-color: #1d1e1f;
    }

    :deep(.el-table__empty-block) {
      background-color: #1d1e1f;
    }

    :deep(.el-table__empty-text) {
      color: #ffffff;
    }

    :deep(.el-loading-mask) {
      background-color: rgba(29, 30, 31, 0.8);
    }

    :deep(.highlight-row) {
      background-color: #2d3a2d;
    }

    :deep(.room-name) {
      color: #409eff;
    }

    :deep(.equipment-info) {
      color: #c0c4cc;
    }
  }

  :deep(.el-table--border th),
  :deep(.el-table--border td) {
    border-color: #333;
  }

  :deep(.el-table::before),
  :deep(.el-table--border::after) {
    background-color: #333;
  }

  .room-dialog,
  .detail-dialog {
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

    :deep(.equipment-detail),
    :deep(.description-detail) {
      color: #c0c4cc;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .room-management-container {
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

    .search-input {
      width: 100%;
    }
  }
}

@media (max-width: 480px) {
  .room-management-card {
    :deep(.el-card__header) {
      padding: 16px;
    }
  }

  .card-header {
    font-size: 18px;
  }

  :deep(.operation-buttons) {
    gap: 8px;

    .el-button {
      margin-bottom: 4px;
    }
  }
}
</style>

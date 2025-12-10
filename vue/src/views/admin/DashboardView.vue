<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <h2>欢迎回来，{{ userInfo.name }}</h2>
            <p>今天是 {{ currentDate }}，祝您工作顺利！</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-blue">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-green">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ roomCount }}</div>
              <div class="stat-label">机房总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-orange">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ bookingCount }}</div>
              <div class="stat-label">今日预约</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-purple">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingReservations }}</div>
              <div class="stat-label">待处理预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card class="booking-card">
          <template #header>
            <div class="card-header">
              <span>最新预约</span>
              <el-button link @click="goToReservationAudit">查看更多</el-button>
            </div>
          </template>

          <el-table
            :data="latestBookings"
            style="width: 100%"
            stripe
          >
            <el-table-column label="编号" width="60">
              <template #default="{ $index }">
                {{ $index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="reservationId" label="预约编号" width="120" />
            <el-table-column prop="roomId" label="机房" width="80" />
            <el-table-column prop="seatId" label="座位号" width="80">
              <template #default="{ row }">
                <span v-if="row.seatId && row.seatId !== '-1'">{{ row.seatId }}</span>
                <span v-else>整机房</span>
              </template>
            </el-table-column>
            <el-table-column prop="userId" label="用户ID" width="120" />
            <el-table-column prop="reservationDate" label="预约日期" width="120" />
            <el-table-column prop="startTime" label="开始时间" width="100" />
            <el-table-column prop="endTime" label="结束时间" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="system-card">
          <template #header>
            <div class="card-header">
              <span>机房信息</span>
            </div>
          </template>

          <el-table 
            :data="roomInfo.slice(0, 5)" 
            style="width: 100%"
            max-height="300"
          >
            <el-table-column prop="roomId" label="机房号" width="80" />
            <el-table-column prop="name" label="机房名" width="120" />
            <el-table-column prop="totalSeats" label="总座位" width="80" />
            <el-table-column prop="availableSeats" label="可预约" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="view-all-link">
            <el-button link type="primary" @click="goToRoomManagement">查看全部机房</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { User, Monitor, Calendar, Bell } from '@element-plus/icons-vue'
import { getBeijingTime, getCurrentDateTimeFormatted } from '@/utils/timeUtils.js'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import { useRouter } from 'vue-router'
import { getAllUsers, getAllTeachers, getAllAdmins } from '@/api/admin'
import { getComputerLabs } from '@/api/room'
import { getAllReservations } from '@/api/reservation'
import { ElMessage } from 'element-plus'

// 扩展 dayjs 功能
dayjs.extend(utc)
dayjs.extend(timezone)

// 使用 stores
const authStore = useAuthStore()
const router = useRouter()

// 状态
const currentDate = ref(getCurrentDateTimeFormatted())
const userCount = ref(0)
const roomCount = ref(0)
const bookingCount = ref(0)
const pendingReservations = ref(0)
const cpuUsage = ref(45)
const memoryUsage = ref(68)
const diskUsage = ref(32)

// 计算属性
const userInfo = computed(() => authStore.userInfo)
const latestBookings = ref([])
const roomInfo = ref([])

// 方法
const getStatusText = (status) => {
  const statusMap = {
    confirmed: '已确认',
    pending: '待审核',
    cancelled: '已取消',
    completed: '已完成'
  }
  return statusMap[status] || status
}

// 获取状态标签类型（与预约审核页面保持一致）
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

// 获取所有用户数量（学生+教师+管理员）
const fetchUserCount = async () => {
  try {
    const [studentsRes, teachersRes, adminsRes] = await Promise.all([
      getAllUsers(),
      getAllTeachers(),
      getAllAdmins()
    ])

    let total = 0
    if (studentsRes.status === 200) {
      total += studentsRes.data.length
    } else {
      ElMessage.error('获取学生数量失败: ' + studentsRes.message)
    }

    if (teachersRes.status === 200) {
      total += teachersRes.data.length
    } else {
      ElMessage.error('获取教师数量失败: ' + teachersRes.message)
    }

    if (adminsRes.status === 200) {
      total += adminsRes.data.length
    } else {
      ElMessage.error('获取管理员数量失败: ' + adminsRes.message)
    }

    userCount.value = total
  } catch (error) {
    console.error('获取用户数量异常:', error)
    ElMessage.error('获取用户数量异常: ' + error.message)
  }
}

// 获取机房数量
const fetchRoomCount = async () => {
  try {
    const response = await getComputerLabs()
    if (response.status === 200) {
      roomCount.value = response.data.length
      roomInfo.value = response.data
    } else {
      ElMessage.error('获取机房数量失败: ' + response.message)
    }
  } catch (error) {
    console.error('获取机房数量异常:', error)
    ElMessage.error('获取机房数量异常: ' + error.message)
  }
}

// 获取预约相关信息
const fetchReservationInfo = async () => {
  try {
    const response = await getAllReservations()
    if (response.status === 200) {
      // 获取今日预约数量（创建时间为今天的预约）
      const today = dayjs().format('YYYY-MM-DD')
      bookingCount.value = response.data.filter(
        booking => {
          // 使用创建时间判断是否为今天
          const createTime = booking.createTime || booking.createdAt
          if (createTime) {
            return dayjs(createTime).format('YYYY-MM-DD') === today
          }
          return false
        }
      ).length

      // 获取待处理预约数量
      pendingReservations.value = response.data.filter(
        booking => booking.status === 'pending' || booking.status === '待确认'
      ).length

      // 获取最近的预约（最多5条）
      latestBookings.value = response.data
        .sort((a, b) => new Date(b.createTime || b.createdAt) - new Date(a.createTime || a.createdAt))
        .slice(0, 5)
        .map(booking => ({
          reservationId: booking.id || booking.reservationId,
          userId: booking.userId,
          userName: booking.userName,
          roomId: booking.roomId,
          roomName: booking.roomName,
          seatId: booking.seatId,
          reservationDate: booking.date || booking.reservationDate,
          startTime: booking.startTime,
          endTime: booking.endTime,
          status: booking.status,
          createdAt: booking.createTime || booking.createdAt
        }))
    } else {
      ElMessage.error('获取预约信息失败: ' + response.message)
    }
  } catch (error) {
    console.error('获取预约信息异常:', error)
    ElMessage.error('获取预约信息异常: ' + error.message)
  }
}

// 跳转到预约审核页面
const goToReservationAudit = () => {
  router.push('/reservation-audit')
}

// 跳转到机房管理页面
const goToRoomManagement = () => {
  router.push('/rooms')
}

// 生命周期
onMounted(() => {
  fetchUserCount()
  fetchRoomCount()
  fetchReservationInfo()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .welcome-card {
    :deep(.el-card__body) {
      padding: 20px;
    }

    .welcome-content {
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
  }

  .mt-20 {
    margin-top: 20px;
  }

  .stat-card {
    :deep(.el-card__body) {
      padding: 20px;
    }

    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20px;

        .el-icon {
          font-size: 30px;
          color: white;
        }

        &.bg-blue {
          background: #409eff;
        }

        &.bg-green {
          background: #67c23a;
        }

        &.bg-orange {
          background: #e6a23c;
        }

        &.bg-purple {
          background: #722ed1;
        }
      }

      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #333;
        }

        .stat-label {
          font-size: 14px;
          color: #999;
        }
      }
    }
  }

  .booking-card,
  .system-card {
    :deep(.el-card__body) {
      padding: 0;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
    }
  }

  .view-all-link {
    padding: 15px 20px;
    text-align: right;
    border-top: 1px solid #eee;
  }

  .system-status {
    padding: 0 20px 20px;

    .status-item {
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }

      .status-label {
        margin-bottom: 10px;
        font-weight: bold;
      }
    }
  }
}

// 深色主题适配
html.dark {
  .welcome-card {
    background-color: #1d1e1f;
    border: none;
    .welcome-content {
      h2 {
        color: #fff;
      }

      p {
        color: #ccc;
      }
    }
  }

  .stat-card {
    background-color: #1d1e1f;
    border: none;
    .stat-content {
      .stat-info {
        .stat-value {
          color: #fff;
        }

        .stat-label {
          color: #999;
        }
      }
    }
  }

  .booking-card,
  .system-card {
    background-color: #1d1e1f;
    border: none;
    :deep(.el-card__header) {
      background-color: #1d1e1f;
      border-color: #333;

      span {
        color: #fff;
      }
    }

    :deep(.el-card__body) {
      background-color: #1d1e1f;
    }
  }

  .booking-card {
    .el-table {
      background-color: #1d1e1f;

      :deep(.el-table__header-wrapper) {
        background-color: #1d1e1f;

        th {
          background-color: #1d1e1f;
          color: #fff;
        }
      }

      :deep(.el-table__body-wrapper) {
        background-color: #1d1e1f;

        td {
          background-color: #1d1e1f;
          color: #fff;
        }
      }
    }
  }

  .system-card {
    .el-table {
      background-color: #1d1e1f;

      :deep(.el-table__header-wrapper) {
        background-color: #1d1e1f;

        th {
          background-color: #1d1e1f;
          color: #fff;
        }
      }

      :deep(.el-table__body-wrapper) {
        background-color: #1d1e1f;

        td {
          background-color: #1d1e1f;
          color: #fff;
        }
      }
    }
    
    .view-all-link {
      border-color: #333;
    }
  }

  .system-status {
    .status-item {
      .status-label {
        color: #fff;
      }
    }
  }
}
</style>
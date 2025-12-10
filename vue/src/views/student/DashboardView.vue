<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <h2>欢迎回来，{{ userInfo.name }}</h2>
            <p>今天是 {{ currentDate }}，祝您学习愉快！</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 左侧主要内容 -->
      <el-col :span="16">
        <!-- 快捷操作卡片 -->
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>

          <div class="quick-actions">
            <el-button type="primary" @click="goToBooking">
              <el-icon><Monitor /></el-icon>
              预约机房
            </el-button>
            <el-button type="success" @click="goToMyBookings">
              <el-icon><Calendar /></el-icon>
              我的预约
            </el-button>
            <el-button type="info" @click="goToSchedule">
              <el-icon><Bell /></el-icon>
              查看课表
            </el-button>
          </div>
        </el-card>

        <!-- 今日课表卡片 -->
        <el-card class="schedule-card mt-20">
          <template #header>
            <div class="card-header">
              <span>今日课表</span>
              <el-button link @click="goToSchedule">查看更多</el-button>
            </div>
          </template>

          <div class="schedule-list">
            <div
              v-for="item in todaySchedule"
              :key="item.id"
              class="schedule-item"
            >
              <div class="schedule-time">
                <div class="time">{{ item.startTime }}</div>
                <div class="time-line"></div>
                <div class="time">{{ item.endTime }}</div>
              </div>
              <div class="schedule-info">
                <div class="course-name">{{ item.courseName }}</div>
                <div class="course-room">{{ item.room }}</div>
                <div class="course-teacher">{{ item.teacher }}</div>
              </div>
            </div>

            <el-empty
              v-if="todaySchedule.length === 0"
              description="今日无课程安排"
              :image-size="80"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧统计和公告 -->
      <el-col :span="8">
        <!-- 统计卡片 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon bg-blue">
                  <el-icon><Monitor /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ bookingCount }}</div>
                  <div class="stat-label">我的预约</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="24" class="mt-20">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon bg-green">
                  <el-icon><Calendar /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ scheduleCount }}</div>
                  <div class="stat-label">今日课程</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useScheduleStore } from '@/stores/schedule'
import { useRouter } from 'vue-router'
import { Monitor, Calendar, Bell } from '@element-plus/icons-vue'
import { getBeijingTime, getDayOfWeek, getCurrentDateTimeFormatted } from '@/utils/timeUtils.js'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import { getUserReservations } from '@/api/room'

// 扩展 dayjs 功能
dayjs.extend(utc)
dayjs.extend(timezone)

// 使用 stores
const authStore = useAuthStore()
const scheduleStore = useScheduleStore()
const router = useRouter()

// 状态
const currentDate = ref(getCurrentDateTimeFormatted())
const bookingCount = ref(0)
const noticeCount = ref(5)

// 计算属性
const userInfo = computed(() => authStore.userInfo)

// 映射课程时间
const timeMapping = {
  1: { start: '08:20', end: '09:00' },
  2: { start: '09:10', end: '09:50' },
  3: { start: '10:20', end: '11:00' },
  4: { start: '11:10', end: '11:50' },
  5: { start: '14:00', end: '14:40' },
  6: { start: '14:50', end: '15:30' },
  7: { start: '16:00', end: '16:40' },
  8: { start: '16:50', end: '17:30' },
  9: { start: '19:00', end: '19:40' },
  10: { start: '19:50', end: '20:30' }
}

const todaySchedule = computed(() => {
  const today = getDayOfWeek() // 周日为7
  const schedule = scheduleStore.getScheduleByWeek(scheduleStore.currentWeek)
    .filter(item => item.dayOfWeek === today)
    .map(item => {
      const startSection = item.startSection
      const endSection = item.startSection + item.duration - 1

      return {
        ...item,
        startTime: timeMapping[startSection]?.start || '未知',
        endTime: timeMapping[endSection]?.end || '未知'
      }
    })
    .sort((a, b) => a.startSection - b.startSection)

  // 更新今日课程数量
  scheduleCount.value = schedule.length
  return schedule
})

const scheduleCount = ref(0)

// 方法
const loadSchedule = async () => {
  await scheduleStore.fetchSchedule({ roleId: 'student', userId: authStore.userInfo.userId }, scheduleStore.currentWeek)
}

const loadBookingCount = async () => {
  try {
    // 获取用户的预约信息
    const response = await getUserReservations(authStore.userInfo.userId)

    if (response.status === 200 || response.statusText === 'OK' || response.success === true) {
      // 过滤出待确认和已确认的预约
      const validBookings = response.data.filter(booking =>
        booking.status === '待确认' || booking.status === '已确认'
      )
      bookingCount.value = validBookings.length
    }
  } catch (error) {
    console.error('获取预约信息失败:', error)
  }
}

const goToBooking = () => {
  router.push('/booking')
}

const goToMyBookings = () => {
  router.push('/my-bookings')
}

const goToSchedule = () => {
  router.push('/schedule')
}

// 生命周期
onMounted(() => {
  loadSchedule()
  loadBookingCount()
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

  .quick-actions-card {
    :deep(.el-card__body) {
      padding: 20px;
    }

    .quick-actions {
      display: flex;
      gap: 20px;
      flex-wrap: wrap;

      .el-button {
        flex: 1;
        min-width: 120px;
        height: 60px;
        font-size: 16px;

        .el-icon {
          margin-right: 8px;
        }
      }
    }
  }

  .schedule-card,
  .notice-card {
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

  .schedule-list {
    padding: 0 20px 20px;

    .schedule-item {
      display: flex;
      padding: 15px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .schedule-time {
        width: 80px;
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-right: 20px;

        .time {
          font-size: 12px;
          color: #999;
        }

        .time-line {
          flex: 1;
          width: 2px;
          background: #409eff;
          margin: 5px 0;
        }
      }

      .schedule-info {
        flex: 1;

        .course-name {
          font-weight: bold;
          margin-bottom: 5px;
        }

        .course-room,
        .course-teacher {
          font-size: 14px;
          color: #666;
          margin-bottom: 3px;
        }
      }
    }
  }

  .notice-list {
    padding: 0 20px 20px;

    .notice-item {
      padding: 12px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .notice-title {
        font-size: 14px;
        margin-bottom: 5px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .notice-date {
        font-size: 12px;
        color: #999;
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

  .quick-actions-card {
    background-color: #1d1e1f;
    border: none;

    :deep(.el-card__header) {
      background-color: #1d1e1f;
      border-color: #333;
      color: #ffffff;
    }

    :deep(.el-card__body) {
      background-color: #1d1e1f;
    }
  }

  .schedule-card,
  .notice-card {
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

  .schedule-list {
    .schedule-item {
      border-bottom: 1px solid #333;

      .schedule-time {
        .time {
          color: #999;
        }

        .time-line {
          background: #409eff;
        }
      }

      .schedule-info {
        .course-name {
          color: #fff;
        }

        .course-room,
        .course-teacher {
          color: #ccc;
        }
      }
    }
  }

  .notice-list {
    .notice-item {
      border-bottom: 1px solid #333;

      .notice-title {
        color: #fff;
      }

      .notice-date {
        color: #999;
      }
    }
  }
}
</style>

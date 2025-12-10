<!-- 学生课表页面 -->
<template>
  <div class="schedule-container">
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <h2>我的课表</h2>
          <div class="week-selector">
            <el-button
              :disabled="currentWeek <= 1"
              @click="changeWeek(-1)"
              size="small"
            >
              上一周
            </el-button>
            <span class="current-week">第 {{ currentWeek }} 周</span>
            <el-button
              :disabled="currentWeek >= maxWeeks"
              @click="changeWeek(1)"
              size="small"
            >
              下一周
            </el-button>
          </div>
        </div>
      </template>

      <div class="schedule-table">
        <div class="table-header">
          <div class="time-column"></div>
          <div
            v-for="(day, index) in weekdays"
            :key="index"
            class="day-column"
            :class="{ today: isToday(index + 1) }"
          >
            {{ day }}
            <div class="date">{{ getDateString(index + 1) }}</div>
          </div>
        </div>

        <div
          v-for="(timeSlot, slotIndex) in timeSlots"
          :key="slotIndex"
          class="table-row"
        >
          <div class="time-column">{{ timeSlot }}</div>

          <!-- 周一到周日的课程 -->
          <div
            v-for="dayIndex in 7"
            :key="dayIndex"
            class="day-cell"
          >
            <div
              v-for="course in getCoursesForTime(dayIndex, timeSlot)"
              :key="course.id"
              class="course-item"
              :class="`course-${course.courseId}`"
            >
              <div class="course-name">{{ course.courseName }}</div>
              <div class="course-room">{{ course.room }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue'
import {useAuthStore} from '@/stores/auth'
import {useScheduleStore} from '@/stores/schedule'
import { getBeijingTime, getDayOfWeek } from '@/utils/timeUtils.js'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

// 扩展 dayjs 功能
dayjs.extend(utc)
dayjs.extend(timezone)

// Stores
const authStore = useAuthStore()
const scheduleStore = useScheduleStore()

// 状态
const currentWeek = ref(1)

// 时间段定义
const timeSlots = [
  '08:20-10:00',
  '10:20-12:00',
  '14:00-15:40',
  '16:00-17:40',
  '19:00-20:40'
]

// 星期定义
const weekdays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// 计算属性：获取当前周的课表
const weeklySchedule = computed(() => {
  return scheduleStore.getScheduleByWeek(currentWeek.value)
})

// 计算最大周数
const maxWeeks = computed(() => {
  if (scheduleStore.currentSemester && scheduleStore.currentSemester.startDate && scheduleStore.currentSemester.endDate) {
    const startDate = new Date(scheduleStore.currentSemester.startDate)
    const endDate = new Date(scheduleStore.currentSemester.endDate)
    // 计算两个日期之间的毫秒差
    const diffTime = Math.abs(endDate - startDate)
    // 转换为天数
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    // 计算周数（从第1周开始）
    return Math.max(1, Math.ceil(diffDays / 7))
  }
  return 20 // 默认值
})

// 判断是否为今天
const isToday = (dayOfWeek) => {
  const today = getDayOfWeek()
  return dayOfWeek === today && getCurrentWeekNumber() === currentWeek.value
}

// 获取日期字符串（基于当前周次）
const getDateString = (dayOfWeek) => {
  // 获取学期开始日期
  if (!scheduleStore.currentSemester || !scheduleStore.currentSemester.startDate) {
    return ''
  }

  const startDate = new Date(scheduleStore.currentSemester.startDate)
  // 计算当前周的周一日期
  const monday = new Date(startDate)
  monday.setDate(monday.getDate() + (currentWeek.value - 1) * 7)

  // 计算目标日期
  const targetDate = new Date(monday)
  targetDate.setDate(targetDate.getDate() + (dayOfWeek - 1))

  return `${targetDate.getMonth() + 1}-${targetDate.getDate()}`
}

// 获取当前周数
const getCurrentWeekNumber = () => {
  if (scheduleStore.currentSemester && scheduleStore.currentSemester.startDate) {
    const startDate = new Date(scheduleStore.currentSemester.startDate)
    const today = dayjs().tz('Asia/Shanghai').toDate()
    // 计算两个日期之间的毫秒差
    const diffTime = Math.abs(today - startDate)
    // 转换为天数
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    // 计算周数（从第1周开始）
    return Math.max(1, Math.ceil(diffDays / 7))
  }
  return 1
}

// 获取指定时间和日期的课程
const getCoursesForTime = (dayOfWeek, timeSlot) => {
  const courses = weeklySchedule.value.filter(course => {
    // 检查是否是同一天
    if (course.dayOfWeek !== dayOfWeek) return false

    // 定义每个时间段对应的节次范围
    const timeSlotSections = {
      '08:20-10:00': {start: 1, end: 2},
      '10:20-12:00': {start: 3, end: 4},
      '14:00-15:40': {start: 5, end: 6},
      '16:00-17:40': {start: 7, end: 8},
      '19:00-20:40': {start: 9, end: 10}
    }

    const slot = timeSlotSections[timeSlot]
    if (!slot) return false

    // 检查课程节次是否与时间段重叠
    const courseStart = course.startSection
    const courseEnd = course.startSection + course.duration - 1

    // 检查是否有重叠：课程开始节次 <= 时间段结束节次 且 课程结束节次 >= 时间段开始节次

    return courseStart <= slot.end && courseEnd >= slot.start
  })

  return courses
}

// 更改周次
const changeWeek = (delta) => {
  const newWeek = currentWeek.value + delta
  if (newWeek >= 1 && newWeek <= maxWeeks.value) {
    currentWeek.value = newWeek
    loadSchedule()
  }
}

// 加载课表数据
const loadSchedule = async () => {
  // 确保有有效的用户ID
  const userId = authStore.userInfo?.userId

  if (userId) {
    await scheduleStore.fetchSchedule({ roleId: 'student', userId: userId }, currentWeek.value)
  } else {
    console.error('无法获取用户ID')
  }
}

// 计算当前是学期的第几周
const calculateCurrentWeek = () => {
  if (scheduleStore.currentSemester && scheduleStore.currentSemester.startDate) {
    const startDate = new Date(scheduleStore.currentSemester.startDate)
    const today = dayjs().tz('Asia/Shanghai').toDate()
    // 计算两个日期之间的毫秒差
    const diffTime = Math.abs(today - startDate)
    // 转换为天数
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    // 计算周数（从第1周开始）
    const weekNumber = Math.max(1, Math.ceil(diffDays / 7))
    currentWeek.value = Math.min(maxWeeks.value, weekNumber) // 根据学期结束日期确定最大周数
  }
}

// 监听学期信息变化
watch(() => scheduleStore.currentSemester, (newSemester) => {
  if (newSemester) {
    calculateCurrentWeek()
    loadSchedule()
  }
})

// 生命周期钩子
onMounted(async () => {
  // 先获取学期信息
  await scheduleStore.fetchCurrentTerm()
  // 计算当前周次
  calculateCurrentWeek()
  // 加载课表数据
  await loadSchedule()
})
</script>

<style lang="scss" scoped>
.schedule-container {
  padding: 20px;

  .schedule-card {
    :deep(.el-card__body) {
      padding: 0;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;

      h2 {
        margin: 0;
        font-size: 20px;
      }

      .week-selector {
        display: flex;
        align-items: center;
        gap: 15px;

        .current-week {
          font-weight: bold;
          min-width: 80px;
          text-align: center;
        }
      }
    }

    .schedule-table {
      width: 100%;
      border-collapse: collapse;

      .table-header {
        display: flex;
        background-color: #f5f7fa;

        .time-column {
          width: 80px;
          padding: 12px;
          text-align: center;
          font-weight: bold;
          border-right: 1px solid #ebeef5;
        }

        .day-column {
          flex: 1;
          padding: 12px;
          text-align: center;
          border-right: 1px solid #ebeef5;
          position: relative;

          &.today {
            background-color: #ecf5ff;

            &::after {
              content: '今天';
              position: absolute;
              top: 5px;
              right: 5px;
              font-size: 12px;
              color: #409eff;
            }
          }

          .date {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
          }

          &:last-child {
            border-right: none;
          }
        }
      }

      .table-row {
        display: flex;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .time-column {
          width: 80px;
          padding: 12px;
          text-align: center;
          background-color: #f5f7fa;
          border-right: 1px solid #ebeef5;
          font-size: 12px;
        }

        .day-cell {
          flex: 1;
          min-height: 100px; /* 固定最小高度确保布局稳定 */
          border-right: 1px solid #ebeef5;
          padding: 5px;
          position: relative;

          &:last-child {
            border-right: none;
          }

          .course-item {
            background-color: #ecf5ff;
            border-left: 3px solid #409eff;
            padding: 8px;
            margin-bottom: 5px;
            border-radius: 4px;
            font-size: 12px;

            .course-name {
              font-weight: bold;
              margin-bottom: 3px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }

            .course-room,
            .course-class {
              color: #666;
              margin-bottom: 2px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }
        }
      }
    }
  }

  // 深色主题适配
  html.dark {
    .schedule-card {
      background-color: #1d1e1f;
      border: none;

      :deep(.el-card__header) {
        background-color: #1d1e1f;
        border-color: #333;
      }

      .card-header {
        h2 {
          color: #fff;
        }
      }

      .schedule-table {
        .table-header {
          background-color: #2d2e2f;

          .day-column {
            border-color: #333;

            &.today {
              background-color: #3d3e3f;
            }

            .date {
              color: #aaa;
            }
          }

          .time-column {
            background-color: #2d2e2f;
            border-color: #333;
            color: #fff;
          }
        }

        .table-row {
          border-color: #333;

          .time-column {
            background-color: #2d2e2f;
            border-color: #333;
            color: #fff;
          }

          .day-cell {
            border-color: #333;

            .course-item {
              background-color: #3d3e3f;
              border-color: #409eff;

              .course-name {
                color: #fff;
              }

              .course-room,
              .course-class {
                color: #ccc;
              }
            }
          }
        }
      }
    }
  }
}
</style>

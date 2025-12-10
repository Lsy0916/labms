<!-- 机房预约页面 -->
<template>
  <div class="booking-container">
    <el-card class="booking-card">
      <template #header>
        <div class="card-header">
          <span>机房预约</span>
        </div>
      </template>

      <el-steps :active="currentStep" finish-status="success" align-center class="mb-30">
        <el-step title="选择机房" />
        <el-step title="选择日期" />
        <el-step title="选择座位" />
        <el-step title="确认预约" />
      </el-steps>

      <!-- 步骤1: 选择机房 -->
      <div v-show="currentStep === 0" class="step-content">
        <el-alert
          v-if="!route.query.roomId"
          title="请选择一个机房开始预约"
          type="info"
          show-icon
          :closable="false"
          class="mb-20"
        />

        <el-row :gutter="20">
          <el-col
            v-for="room in computerLabs"
            :key="room.id"
            :span="8"
            class="room-col"
          >
            <el-card
              class="room-card"
              :class="{ 'room-selected': selectedRoom?.id === room.id, 'room-disabled': room.status === '停用' }"
              @click="selectRoomAndProceed(room)"
            >
              <div class="room-info">
                <div class="room-name">{{ room.name }} ({{ room.roomId }})</div>
                <div class="room-capacity">总座位数: {{ room.totalSeats }}</div>
                <div class="room-available">可用座位: {{ room.availableSeats }}</div>
                <div class="room-status">
                  <el-tag :type="room.status === '正常' ? 'success' : 'danger'">
                    {{ room.status }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤2: 选择日期 -->
      <div v-show="currentStep === 1" class="step-content">
        <transition name="fade" mode="out-in">
          <div v-if="loadingRoomDetail" key="loading" class="loading-room-detail">
            <el-skeleton :rows="3" animated />
            <div class="loading-text">正在加载机房信息...</div>
          </div>

          <div v-else key="content">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="请选择预约日期"
              :disabled-date="disabledDate"
              :default-value="todayBeijing"
              format="YYYY年MM月DD日"
              value-format="YYYY-MM-DD"
              @change="onDateChange"
            />

            <div class="time-slots" v-if="selectedDate">
              <h3>可预约时间段</h3>
              <el-row :gutter="20">
                <el-col
                  v-for="slot in timeSlots"
                  :key="slot.value"
                  :span="12"
                  :md="8"
                  :lg="6"
                >
                  <el-card
                    class="time-slot"
                    :class="{
                      'slot-selected': selectedTimeSlot?.value === slot.value,
                      'slot-available': slot.available
                    }"
                    @click="selectTimeSlot(slot)"
                  >
                    <div class="slot-time">{{ slot.label }}</div>
                    <div class="slot-info">
                      <div class="slot-status">
                        <el-tag :type="slot.available ? 'success' : 'info'" size="small">
                          {{ getTimeSlotStatusText(slot) }}
                        </el-tag>
                      </div>
                      <div class="slot-remaining" v-if="slot.available">
                        剩余: {{ slot.remaining }}/{{ slot.total }}
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </div>

            <div class="room-info-summary" v-if="selectedRoom">
              <h3>机房信息</h3>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="机房">{{ selectedRoom.name }} ({{ selectedRoom.roomId }})</el-descriptions-item>
                <el-descriptions-item label="总座位数">{{ selectedRoom.totalSeats }}</el-descriptions-item>
                <el-descriptions-item label="可用座位">{{ selectedRoom.availableSeats }}</el-descriptions-item>
                <el-descriptions-item label="设备信息" v-if="selectedRoom.equipmentInfo && selectedRoom.equipmentInfo !== ''">
                  {{ selectedRoom.equipmentInfo }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </transition>

        <div class="step-actions">
          <el-button @click="prevStep">上一步</el-button>
          <el-button
            type="primary"
            @click="nextStep"
            :disabled="!selectedDate || !selectedTimeSlot || !selectedTimeSlot.available"
          >
            下一步
          </el-button>
        </div>
      </div>

      <!-- 步骤3: 选择座位 -->
      <div v-show="currentStep === 2" class="step-content">
        <div class="seat-selection-header">
          <h3>{{ selectedRoom?.name }} - {{ selectedDate }} {{ selectedTimeSlot?.label }}</h3>
          <el-radio-group v-model="selectionMode" @change="onSelectionModeChange">
            <el-radio-button value="single">单座位选择</el-radio-button>
            <el-radio-button value="whole">整机房预约</el-radio-button>
          </el-radio-group>
        </div>

        <div class="seat-map-wrapper">
          <SeatMap
            v-if="selectedRoom"
            :seat-template="seats"
            :occupied="occupiedSeats"
            :broken="brokenSeats"
            :disabled="selectionMode === 'whole'"
            :selected-seats="selectedSeats"
            v-model:selectedSeats="selectedSeats"
          />
        </div>

        <div class="step-actions">
          <el-button @click="prevStep">上一步</el-button>
          <el-button
            type="primary"
            @click="nextStep"
            :disabled="(selectionMode === 'single' && selectedSeats.length === 0) || (selectionMode === 'whole' && !canWholeRoomBooking)"
          >
            下一步
          </el-button>
        </div>
      </div>

      <!-- 步骤4: 确认预约 -->
      <div v-show="currentStep === 3" class="step-content">
        <el-card class="confirmation-card">
          <h3>预约信息确认</h3>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="机房">{{ selectedRoom?.name }} ({{ selectedRoom?.roomId }})</el-descriptions-item>
            <el-descriptions-item label="预约时间">{{ formatDate(selectedDate) }} {{ selectedTimeSlot?.label }}</el-descriptions-item>
            <el-descriptions-item label="预约时长">{{ getTimeDuration(selectedTimeSlot) }}</el-descriptions-item>
            <el-descriptions-item label="座位">
              <div v-if="selectionMode === 'whole'">整机房预约</div>
              <div v-else>{{ selectedSeats.join(', ') }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="预约类型">
              <el-tag :type="selectionMode === 'whole' ? 'warning' : 'primary'">
                {{ selectionMode === 'whole' ? '整机房预约' : '单座位预约' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="设备信息" v-if="selectedRoom?.equipmentInfo">
              {{ selectedRoom.equipmentInfo }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="confirmation-actions">
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="confirmBooking" :loading="confirming">
              确认预约
            </el-button>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SeatMap from '@/components/SeatMap.vue'
import { getComputerLabs, getComputerLabById, getSeatsStatusByTime, createBooking } from '@/api/room.js'
import { debounce } from '@/utils/debounce.js'
import { useAuthStore } from '@/stores/auth.js'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import { getBeijingTime, formatDate, getTimeDuration, getCurrentDateFormat, getStartOfDay } from '@/utils/timeUtils.js'

// 扩展 dayjs 功能
dayjs.extend(utc)
dayjs.extend(timezone)

// 路由
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 状态
const currentStep = ref(0) // 当前步骤
const computerLabs = ref([]) // 机房列表
const selectedRoom = ref(null) // 当前选中的机房
const loadingRoomDetail = ref(false) // 机房详情加载中
const selectedDate = ref('') // 选中的日期
const selectedTimeSlot = ref(null) // 选中的时间槽
const selectionMode = ref('single') // 选择模式
const selectedSeats = ref([]) // 选中的座位
const confirming = ref(false) // 预约确认中

// 座位状态 - 改为对象，以时间槽为键存储各自的状态
const timeSlotSeatStatus = ref({})

// 时间槽定义（根据需求固定）
const timeSlotDefinitions = [
  { label: '08:20-10:00', value: '08:20-10:00', startTime: '08:20:00', endTime: '10:00:00' },
  { label: '10:20-12:00', value: '10:20-12:00', startTime: '10:20:00', endTime: '12:00:00' },
  { label: '14:00-15:40', value: '14:00-15:40', startTime: '14:00:00', endTime: '15:40:00' },
  { label: '16:00-17:40', value: '16:00-17:40', startTime: '16:00:00', endTime: '17:40:00' }
]

// 计算属性
const timeSlots = computed(() => {
  if (!selectedDate.value) return []

  // 获取当前时间
  const now = getBeijingTime().toDate()
  const today = now.toISOString().split('T')[0]
  const currentTime = now.toTimeString().substring(0, 5) // HH:mm格式

  // 判断是否是今天
  const isToday = selectedDate.value === today

  return timeSlotDefinitions.map(slot => {
    // 如果是今天，标记已经过去的时段
    let isPastTime = false
    if (isToday && currentTime > slot.label.split('-')[1]) {
      isPastTime = true
    }

    // 获取该时段的座位状态
    const seatStatus = timeSlotSeatStatus.value[slot.value] || []
    // 计算该时段的空闲座位数
    const freeSeats = seatStatus.filter(seat => seat.status === '空闲').length

    // 检查是否已经有整机房预约（座位号为-1）
    const hasWholeRoomBooking = seatStatus.some(seat => seat.seatId === -1 && seat.status === '占用')

    return {
      ...slot,
      isPastTime, // 是否为过去的时间段
      available: !isPastTime && freeSeats > 0 && !hasWholeRoomBooking, // 只有不是过去的时间且有空位且没有整机房预约才可用
      remaining: freeSeats,
      total: selectedRoom.value?.totalSeats || 0
    }
  })
})

// 座位相关计算属性
const seats = computed(() => {
  if (!selectedRoom.value) return []

  const result = []
  for (let i = 1; i <= selectedRoom.value.totalSeats; i++) {
    const seatId = i.toString().padStart(2, '0')
    result.push({
      id: seatId,
      status: 'available'
    })
  }
  return result
})

const occupiedSeats = computed(() => {
  if (!selectedTimeSlot.value) return []

  const seatStatus = timeSlotSeatStatus.value[selectedTimeSlot.value.value] || []
  return seatStatus
    .filter(seat => seat.status === '占用')
    .map(seat => seat.seatId)
})

const brokenSeats = computed(() => {
  if (!selectedTimeSlot.value) return []

  const seatStatus = timeSlotSeatStatus.value[selectedTimeSlot.value.value] || []
  return seatStatus
    .filter(seat => seat.status === '故障')
    .map(seat => seat.seatId)
})

// 整机房预约是否可行（当没有任何座位被占用或者只有故障座位时才可以）
const canWholeRoomBooking = computed(() => {
  if (!selectedTimeSlot.value) return false

  const seatStatus = timeSlotSeatStatus.value[selectedTimeSlot.value.value] || []
  // 检查是否有占用的座位（排除故障座位）
  const occupiedCount = seatStatus.filter(seat => seat.status === '占用').length
  return occupiedCount === 0
})

// 添加一个新的计算属性来获取今天的北京时间
const todayBeijing = computed(() => {
  // 使用工具类获取当前时间
  const today = getBeijingTime().toDate()
  return today
})

// 方法
const loadComputerLabs = async () => {
  try {
    const response = await getComputerLabs()
    // 移除了过滤条件，现在显示所有机房，包括停用的机房
    computerLabs.value = response.data

    // 如果URL中有roomId参数，自动选择对应的机房
    if (route.query.roomId) {
      const room = computerLabs.value.find(r => r.roomId === route.query.roomId)
      if (room) {
        await selectRoomAndProceed(room)
      }
    }
  } catch (error) {
    ElMessage.error('获取机房列表失败: ' + error.message)
  }
}

const loadSeatStatus = async (date, timeSlot) => {
  if (!selectedRoom.value) return

  try {
    const response = await getSeatsStatusByTime({
      roomId: selectedRoom.value.roomId,
      date: date,
      startTime: timeSlot.startTime,
      endTime: timeSlot.endTime
    })

    // 保存特定时间段的座位状态
    timeSlotSeatStatus.value[timeSlot.value] = response.data
  } catch (error) {
    ElMessage.error('获取座位状态失败: ' + error.message)
    timeSlotSeatStatus.value[timeSlot.value] = []
  }
}

// 格式化日期显示

// 计算时间段时长

// 获取时间段状态文本
const getTimeSlotStatusText = (slot) => {
  // 如果是过去的时间段，显示"暂不可预约"
  if (slot.isPastTime) {
    return '暂不可预约'
  }
  // 如果不是过去的时间段但没有空位，显示"已满"
  else if (!slot.available && slot.remaining === 0) {
    return '已满'
  }
  // 其他情况显示"可预约"
  else {
    return '可预约'
  }
}

const selectRoomAndProceed = debounce(async (room) => {
  // 如果机房是停用状态，不允许继续操作
  if (room.status === '停用') {
    ElMessage.warning('该机房已停用，无法进行预约')
    return
  }

  selectedRoom.value = room
  loadingRoomDetail.value = true

  try {
    // 获取机房详细信息
    const response = await getComputerLabById(room.roomId)
    selectedRoom.value = response.data

    // 设置默认日期为今天
    const today = getCurrentDateFormat()
    selectedDate.value = today

    // 加载所有时间段的座位状态
    await loadAllTimeSlotSeatStatus(today)

    // 默认选择第一个可用时间段
    if (timeSlots.value.length > 0) {
      const firstAvailableSlot = timeSlots.value.find(slot => slot.available)
      if (firstAvailableSlot) {
        await selectTimeSlot(firstAvailableSlot)
      }
    }

    nextStep()
  } catch (error) {
    ElMessage.error('获取机房详情失败: ' + error.message)
  } finally {
    loadingRoomDetail.value = false
  }
}, 1000, true)

// 加载所有时间段的座位状态
const loadAllTimeSlotSeatStatus = async (date) => {
  if (!selectedRoom.value) return

  try {
    // 并行加载所有时间段的座位状态
    const promises = timeSlotDefinitions.map(slot =>
      getSeatsStatusByTime({
        roomId: selectedRoom.value.roomId,
        date: date,
        startTime: slot.startTime,
        endTime: slot.endTime
      }).then(response => {
        // 保存每个时间段的座位状态
        timeSlotSeatStatus.value[slot.value] = response.data
      }).catch(error => {
        console.error(`获取 ${slot.label} 时间段座位状态失败:`, error)
        // 失败时设置为空数组
        timeSlotSeatStatus.value[slot.value] = []
      })
    )

    await Promise.all(promises)
  } catch (error) {
    ElMessage.error('获取座位状态失败: ' + error.message)
  }
}

const disabledDate = (date) => {
  // 禁用过去的日期
  const today = getStartOfDay()
  return date.getTime() < today.getTime()
}

// 在组件挂载时设置默认日期为今天
const setDefaultDate = () => {
  selectedDate.value = getCurrentDateFormat()
}

const onDateChange = async (date) => {
  selectedDate.value = date
  selectedTimeSlot.value = null

  // 重新加载所有时间段的座位状态
  await loadAllTimeSlotSeatStatus(date)

  // 如果有可用时间段，选择第一个
  if (timeSlots.value.length > 0) {
    const firstAvailableSlot = timeSlots.value.find(slot => slot.available)
    if (firstAvailableSlot) {
      await selectTimeSlot(firstAvailableSlot)
    }
  }
}

const selectTimeSlot = debounce(async (slot) => {
  // 如果是过去的时间段或者没有空位，则不能选择
  if (slot.isPastTime || !slot.available) return

  selectedTimeSlot.value = slot
}, 1000, true)

const onSelectionModeChange = () => {
  // 切换模式时清空已选座位
  selectedSeats.value = []

  // 如果切换到整机房预约模式，给出提示
  if (selectionMode.value === 'whole') {
    ElMessage.info('已切换到整机房预约模式，将预约整个机房')
  }
  // 如果切换到单座位选择模式，给出提示
  else if (selectionMode.value === 'single') {
    ElMessage.info('已切换到单座位选择模式，请选择需要预约的座位')
  }
}

const prevStep = debounce(() => {
  if (currentStep.value > 0) {
    currentStep.value--

    // 在步骤3(确认预约)返回到步骤2(选择座位)时
    if (currentStep.value === 2) {
      // 保持selectionMode和selectedSeats的状态，因为用户可能只是想检查确认信息
    }

    // 在步骤2(选择座位)返回到步骤1(选择日期)时
    if (currentStep.value === 1) {
      // 保持selectionMode，但清空已选择的座位
      selectedSeats.value = []
      selectedTimeSlot.value = null
    }

    // 在步骤1(选择日期)返回到步骤0(选择机房)时
    if (currentStep.value === 0) {
      selectedDate.value = ''
      selectedTimeSlot.value = null
      selectedSeats.value = []
      selectionMode.value = 'single'
    }
  }
}, 1000, true)

const nextStep = debounce(() => {
  if (currentStep.value < 3) {
    currentStep.value++

    // 在进入步骤2(选择座位)时，确保有初始状态
    if (currentStep.value === 2) {
      // 不重置已有的selectedSeats和selectionMode，保持用户的选择
    }

    // 在进入步骤3(确认预约)时，保持所有选择状态
  }
}, 1000, true)

const confirmBooking = debounce(async () => {
  confirming.value = true

  try {
    // 准备预约数据
    const bookingData = {
      roomId: selectedRoom.value.roomId,
      userId: authStore.userInfo.userId,
      reservationDate: selectedDate.value,
      startTime: selectedTimeSlot.value.startTime,
      endTime: selectedTimeSlot.value.endTime,
      mode: selectionMode.value,
      seatIds: selectionMode.value === 'single' ? [...selectedSeats.value] : [-1]
    }

    // 调用预约API
    const response = await createBooking(bookingData)

    if (response.status === 200 || response.statusText === 'OK' || response.success === true) {
      ElMessage.success('预约成功！')
      // 预约成功后重新加载座位状态，确保其他用户看到最新的状态
      await loadAllTimeSlotSeatStatus(selectedDate.value)
      await router.push('/my-bookings')
    } else {
      ElMessage.error('预约失败: ' + response.message)
    }
  } catch (error) {
    console.error('预约请求失败:', error)
    ElMessage.error('预约失败: ' + error.message)
  } finally {
    confirming.value = false
  }
}, 1000, true)

// 初始化
setDefaultDate()
loadComputerLabs()

// 页面可见性变化时更新日期
document.addEventListener('visibilitychange', () => {
  if (!document.hidden) {
    // 页面变为可见时，更新日期和座位状态
    setDefaultDate()
    if (selectedRoom.value && selectedDate.value) {
      loadAllTimeSlotSeatStatus(selectedDate.value)
    }
  }
})

</script>

<style scoped>
.booking-container {
  padding: 20px;
}

/* 添加过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.loading-room-detail {
  padding: 30px 0;
  text-align: center;
}

.loading-text {
  margin-top: 15px;
  color: #999;
}

.mb-30 {
  margin-bottom: 30px;
}

.step-content {
  min-height: 400px;
}

.room-col {
  margin-bottom: 20px;
}

.room-card {
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.room-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.room-card.room-selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}

.room-card.room-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.room-card.room-disabled:hover {
  transform: none;
  box-shadow: none;
}

.room-info .room-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.room-capacity,
.room-nice,
.room-available {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.time-slots {
  margin: 30px 0;
}

.time-slots h3 {
  margin-bottom: 20px;
}

.time-slot {
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  margin-bottom: 20px;
}

.time-slot:not(.slot-available) {
  opacity: 0.5;
  cursor: not-allowed;
}

.time-slot:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.time-slot.slot-selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}

.slot-time {
  font-weight: bold;
  margin-bottom: 10px;
  font-size: 16px;
}

.slot-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.slot-remaining {
  font-size: 12px;
  color: #999;
}

.seat-selection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.seat-map-wrapper {
  margin: 20px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.room-info-summary {
  margin-top: 30px;
}

.confirmation-card h3 {
  margin-top: 0;
  text-align: center;
}

.confirmation-actions {
  margin-top: 30px;
  text-align: center;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

/* 深色主题适配 */
html.dark .step-content .room-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
}

html.dark .room-info .room-name {
  color: #fff;
}

html.dark .room-capacity,
html.dark .room-nice,
html.dark .room-available {
  color: #ccc;
}

html.dark .time-slots .time-slot:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

html.dark .slot-remaining {
  color: #ccc;
}

html.dark .seat-map-wrapper {
  background: #1d1e1f;
}

html.dark .booking-card {
  background-color: #1d1e1f;
  color: #ffffff;
  border: none;
}

html.dark .booking-card :deep(.el-card__header) {
  background-color: #1d1e1f;
  border-color: #333;
  color: #ffffff;
  border: none;
}

html.dark .el-steps {
  background-color: #1d1e1f;
}

html.dark .el-step__title,
html.dark .el-step__description {
  color: #ffffff;
}

html.dark .el-step.is-process .el-step__title,
html.dark .el-step.is-process .el-step__description {
  color: #409eff;
}

html.dark .el-step.is-success .el-step__title,
html.dark .el-step.is-success .el-step__description {
  color: #67c23a;
}

html.dark .el-step.is-wait .el-step__title,
html.dark .el-step.is-wait .el-step__description {
  color: #999;
}

html.dark .el-date-editor {
  background-color: #1d1e1f;
}

html.dark .el-date-editor :deep(.el-input__wrapper) {
  background-color: #1d1e1f;
  border: 1px solid #434343;
}

html.dark .el-date-editor :deep(.el-input__inner) {
  background-color: #1d1e1f;
  color: #ffffff;
}

html.dark .el-descriptions {
  background-color: #1d1e1f;
}

html.dark .el-descriptions :deep(.el-descriptions__body) {
  background-color: #1d1e1f;
  color: #ffffff;
}

html.dark .el-descriptions :deep(.el-descriptions__label) {
  color: #ffffff;
  background-color: #2d2e2f;
}

html.dark .el-descriptions :deep(.el-descriptions__content) {
  color: #ffffff;
  background-color: #1d1e1f;
}

html.dark .el-descriptions :deep(.el-descriptions__cell) {
  background-color: #1d1e1f;
}

html.dark .room-card {
  background-color: #1d1e1f;
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

html.dark .time-slot {
  background-color: #1d1e1f;
  border: 1px solid #333;
}

html.dark .time-slot :deep(.el-card__body) {
  background-color: #1d1e1f;
  color: #ffffff;
}

html.dark .time-slot .el-tag {
  background-color: #1d1e1f;
  border-color: #333;
  color: #ffffff;
}

html.dark .time-slot.slot-selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}

html.dark .room-info-summary {
  background-color: #1d1e1f;
  border: 1px solid #333;
  border-radius: 8px;
}

html.dark .room-info-summary :deep(.el-card__body) {
  background-color: #1d1e1f;
  color: #ffffff;
}

html.dark .room-info-summary h3 {
  color: #ffffff;
}

html.dark .slot-time {
  color: #ffffff;
}

html.dark .slot-remaining {
  color: #ccc;
}

html.dark .confirmation-card {
  background-color: #1d1e1f;
  border: 1px solid #333;
}

html.dark .confirmation-card :deep(.el-card__body) {
  background-color: #1d1e1f;
  color: #ffffff;
}

html.dark .confirmation-card h3 {
  color: #ffffff;
}

/* 深色模式下的提示信息适配 */
html.dark .mb-20 :deep(.el-alert) {
  background-color: #1d1e1f !important;
  border: 1px solid #333 !important;
  color: #ffffff !important;
}

html.dark .mb-20 :deep(.el-alert__title) {
  color: #ffffff !important;
}

html.dark .mb-20 :deep(.el-alert__icon) {
  color: #909399 !important;
}

html.dark .mb-20 :deep(.el-alert__content) {
  color: #ffffff !important;
}

html.dark .mb-20 :deep(.el-alert--info) {
  background-color: #1d1e1f !important;
  border-color: #333 !important;
}

html.dark .mb-20 :deep(.el-alert--info .el-alert__icon) {
  color: #909399 !important;
}

/* 深色模式下步骤内容区域适配 */
html.dark .step-content {
  background-color: transparent;
}

html.dark .step-content :deep(.el-alert) {
  background-color: #1d1e1f !important;
  border: 1px solid #333 !important;
}

/* 提示信息下方增加间距 */
.mb-20 {
  margin-bottom: 30px !important;
}

/* 设备信息描述项样式 */
html.dark .el-descriptions__label {
  background-color: #2d2e2f !important;
  color: #ffffff !important;
}

html.dark .el-descriptions__content {
  background-color: #1d1e1f !important;
  color: #ffffff !important;
}
</style>

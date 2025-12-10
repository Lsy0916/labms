<!-- 座位可视化组件 -->
<template>
  <div class="seat-map-container">
    <div class="seat-map-header">
      <div class="legend">
        <span class="legend-item">
          <div class="seat available"></div>
          <span>可选</span>
        </span>
        <span class="legend-item">
          <div class="seat selected"></div>
          <span>已选</span>
        </span>
        <span class="legend-item">
          <div class="seat occupied"></div>
          <span>已占用</span>
        </span>
        <span class="legend-item">
          <div class="seat broken"></div>
          <span>故障</span>
        </span>
      </div>
      
      <div class="controls" v-if="!disabled">
        <el-button 
          type="danger" 
          @click="clearSelection"
          :disabled="selectedSeats.length === 0"
        >
          清空选择
        </el-button>
      </div>
    </div>
    
    <div class="seat-map" ref="seatMapRef">
      <div 
        v-for="seat in seats" 
        :key="seat.id"
        class="seat"
        :class="seat.status"
        :data-id="seat.id"
        @click="handleSeatClick(seat)"
      >
        {{ seat.id }}
      </div>
      <div 
        v-for="i in getPlaceholderCount" 
        :key="'placeholder-' + i"
        class="seat placeholder"
      >
      </div>
    </div>
    
    <div class="seat-map-disabled" v-if="disabled">
      <div class="disabled-message">
        <el-icon><Lock /></el-icon>
        <p>整机房预约模式下无法选择单个座位</p>
      </div>
    </div>
    
    <div class="seat-info" v-if="selectedSeats.length > 0 && !disabled">
      已选择 {{ selectedSeats.length }} 个座位: {{ selectedSeats.join(', ') }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Lock } from '@element-plus/icons-vue'

// 定义组件属性
const props = defineProps({
  rows: {
    type: Number,
    default: 10
  },
  cols: {
    type: Number,
    default: 10
  },
  occupied: {
    type: Array,
    default: () => []
  },
  broken: {
    type: Array,
    default: () => []
  },
  disabled: {
    type: Boolean,
    default: false
  },
  seatTemplate: {
    type: Array,
    default: () => []
  },
  selectedSeats: {
    type: Array,
    default: () => []
  }
})

// 定义事件
const emit = defineEmits(['update:selectedSeats'])

// 引用
const seatMapRef = ref(null)

// 计算属性
const seats = computed(() => {
  // 如果提供了座位模板，则使用模板数据
  if (props.seatTemplate && props.seatTemplate.length > 0) {
    return props.seatTemplate.map(seat => ({
      id: seat.id,
      row: seat.row,
      col: seat.col,
      status: props.occupied.includes(seat.id) ? 'occupied' : 
              props.broken.includes(seat.id) ? 'broken' : 
              props.selectedSeats.includes(seat.id) ? 'selected' : 
              seat.status || 'available'
    }))
  }
  
  // 否则基于行列数生成座位
  const seatList = []
  const actualSeatCount = props.rows * props.cols
  
  for (let i = 0; i < actualSeatCount; i++) {
    const row = Math.floor(i / props.cols)
    const col = i % props.cols
    const seatId = i + 1
    
    let status = 'available'
    
    if (props.occupied.includes(seatId)) {
      status = 'occupied'
    } else if (props.broken.includes(seatId)) {
      status = 'broken'
    } else if (props.selectedSeats.includes(seatId)) {
      status = 'selected'
    }
    
    seatList.push({
      id: seatId,
      row,
      col,
      status
    })
  }
  
  return seatList
})

const getPlaceholderCount = computed(() => {
  const totalGridCells = props.rows * props.cols;
  const actualSeatCount = props.rows * props.cols;
  return totalGridCells - actualSeatCount;
})

// 方法
const handleSeatClick = (seat) => {
  // 如果座位图被禁用，则不能选择任何座位
  if (props.disabled) {
    return
  }
  
  // 不可操作已占用的座位
  if (seat.status === 'occupied') {
    return
  }
  
  const seatId = seat.id
  const index = props.selectedSeats.indexOf(seatId)
  const newSelectedSeats = [...props.selectedSeats]
  
  if (index > -1) {
    // 取消选择
    newSelectedSeats.splice(index, 1)
  } else {
    // 选择座位
    newSelectedSeats.push(seatId)
  }
  
  // 发送更新事件
  emit('update:selectedSeats', newSelectedSeats)
}

const clearSelection = () => {
  emit('update:selectedSeats', [])
}

// 监听器
watch(() => props.occupied, () => {
  // 当已占用座位发生变化时，清除可能冲突的选择
  const newSelectedSeats = props.selectedSeats.filter(
    seatId => !props.occupied.includes(seatId)
  )
  // 只有当选择发生变化时才发出事件
  if (newSelectedSeats.length !== props.selectedSeats.length) {
    emit('update:selectedSeats', newSelectedSeats)
  }
})

// 监听disabled属性变化，如果变为true，清空选择
watch(() => props.disabled, (newVal) => {
  if (newVal && props.selectedSeats.length > 0) {
    emit('update:selectedSeats', [])
  }
})

// 生命周期钩子
onMounted(() => {
  // 可以在这里添加缩放或拖拽功能
})
</script>

<style lang="scss" scoped>
.seat-map-container {
  .seat-map-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 10px;
    
    .legend {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
      
      .legend-item {
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 14px;
      }
    }
  }
  
  .seat-map {
    display: grid;
    grid-template-columns: repeat(v-bind(cols), 1fr);
    gap: 8px;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
    max-height: 500px;
    overflow-y: auto;
  }
  
  .seat-map-disabled {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px 20px;
    background: #f5f7fa;
    border-radius: 8px;
    min-height: 200px;
    
    .disabled-message {
      text-align: center;
      color: #999;
      
      .el-icon {
        font-size: 48px;
        margin-bottom: 10px;
      }
      
      p {
        margin: 0;
        font-size: 16px;
      }
    }
  }
  
  .seat {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.2s ease;
    user-select: none;
    border: none; /* 移除边框 */
    
    &.available {
      background: #67c23a;
      color: white;
      
      &:hover {
        background: #85ce61;
        transform: scale(1.1);
      }
    }
    
    &.selected {
      background: #409eff;
      color: white;
      transform: scale(1.1);
    }
    
    &.occupied {
      background: #f56c6c;
      color: white;
      cursor: not-allowed;
    }
    
    &.broken {
      background: #909399;
      color: white;
      cursor: pointer;
      
      &:hover {
        background: #a0a3a9;
        transform: scale(1.1);
      }
    }
  }
  
  .seat-info {
    margin-top: 15px;
    padding: 10px;
    background: #ecf5ff;
    border-radius: 4px;
    font-size: 14px;
    
    span {
      font-weight: bold;
      color: #409eff;
    }
  }
}

// 深色主题适配
html.dark {
  .seat-map {
    background: #1d1e1f;
  }
  
  .seat-map-disabled {
    background: #1d1e1f;
    
    .disabled-message {
      color: #666;
    }
  }
  
  .seat-info {
    background: #1d1e1f;
  }
}
</style>
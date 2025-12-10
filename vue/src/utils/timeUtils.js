import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

// 扩展 dayjs 功能
dayjs.extend(utc)
dayjs.extend(timezone)

/**
 * 获取当前北京时间
 * @returns {dayjs.Dayjs} 返回 dayjs 对象，时区为 Asia/Shanghai
 */
export function getBeijingTime() {
  return dayjs().tz('Asia/Shanghai')
}

/**
 * 格式化日期为中文格式 YYYY年MM月DD日 (星期X)
 * @param {string|Date} dateString - 日期字符串或 Date 对象
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(dateString) {
  if (!dateString) return ''

  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekday = weekdays[date.getDay()]

  return `${year}年${month}月${day}日 (${weekday})`
}

/**
 * 计算时间段时长
 * @param {Object} timeSlot - 时间段对象，包含 startTime 和 endTime 属性
 * @returns {string} 格式化后的时长字符串
 */
export function getTimeDuration(timeSlot) {
  if (!timeSlot) return ''

  // 解析开始时间和结束时间
  const [startHour, startMinute] = timeSlot.startTime.split(':').map(Number)
  const [endHour, endMinute] = timeSlot.endTime.split(':').map(Number)

  // 计算时长（分钟）
  const startTotalMinutes = startHour * 60 + startMinute
  const endTotalMinutes = endHour * 60 + endMinute
  const durationMinutes = endTotalMinutes - startTotalMinutes

  // 转换为小时和分钟
  const hours = Math.floor(durationMinutes / 60)
  const minutes = durationMinutes % 60

  if (hours > 0 && minutes > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (hours > 0) {
    return `${hours}小时`
  } else {
    return `${minutes}分钟`
  }
}

/**
 * 格式化当前日期为 YYYY-MM-DD 格式
 * @returns {string} 当前日期字符串
 */
export function getCurrentDateFormat() {
  return getBeijingTime().format('YYYY-MM-DD')
}

/**
 * 获取当前是一周的第几天（周日为7）
 * @returns {number} 星期几（1-7，其中7代表周日）
 */
export function getDayOfWeek() {
  return getBeijingTime().day() || 7
}

/**
 * 获取当前日期的开始时间
 * @returns {Date} 当天开始时间的 Date 对象
 */
export function getStartOfDay() {
  return getBeijingTime().startOf('day').toDate()
}

/**
 * 获取格式化的当前日期时间（用于首页显示）
 * @returns {string} 格式化后的当前日期时间字符串
 */
export function getCurrentDateTimeFormatted() {
  const now = getBeijingTime()
  const year = now.year()
  const month = String(now.month() + 1).padStart(2, '0')
  const day = String(now.date()).padStart(2, '0')
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekday = weekdays[now.day()]
  
  return `${year}年${month}月${day}日 ${weekday}`
}

export default {
  getBeijingTime,
  formatDate,
  getTimeDuration,
  getCurrentDateFormat,
  getDayOfWeek,
  getStartOfDay,
  getCurrentDateTimeFormatted
}
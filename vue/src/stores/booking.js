// 预约管理 Store
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useBookingStore = defineStore('booking', () => {
  // 状态
  const bookings = ref([])
  const loading = ref(false)

  // 根据日期获取预约
  const getBookingsByDate = computed(() => (date) => {
    return bookings.value.filter(booking => booking.date === date)
  })

  // 获取预约列表
  const fetchBookings = async (params = {}) => {
    try {
      loading.value = true
      
      // 调用真实 API 请求
      const response = await request({
        url: '/bookings',
        method: 'get',
        params
      })
      
      if (response.success) {
        bookings.value = response.data
      }
      
      return response
    } catch (error) {
      console.error('获取预约列表失败:', error)
      return {
        success: false,
        message: '获取预约列表失败'
      }
    } finally {
      loading.value = false
    }
  }

  // 创建预约
  const createBooking = async (bookingData) => {
    try {
      loading.value = true
      
      // 调用真实 API 请求
      const response = await request({
        url: '/bookings',
        method: 'post',
        data: bookingData
      })
      
      if (response.success) {
        bookings.value.push(response.data)
      }
      
      return response
    } catch (error) {
      console.error('创建预约失败:', error)
      return {
        success: false,
        message: '创建预约失败: ' + error.message
      }
    } finally {
      loading.value = false
    }
  }

  // 取消预约
  const cancelBooking = async (bookingId) => {
    try {
      loading.value = true
      
      // 调用真实 API 请求
      const response = await request({
        url: `/bookings/${bookingId}`,
        method: 'delete'
      })
      
      if (response.success) {
        bookings.value = bookings.value.filter(booking => booking.id !== bookingId)
      }
      
      return response
    } catch (error) {
      console.error('取消预约失败:', error)
      return {
        success: false,
        message: '取消预约失败: ' + error.message
      }
    } finally {
      loading.value = false
    }
  }

  return {
    bookings,
    loading,
    getBookingsByDate,
    fetchBookings,
    createBooking,
    cancelBooking
  }
})
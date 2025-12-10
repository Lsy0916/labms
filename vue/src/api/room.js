// 机房相关 API
import request from '@/utils/request'

/**
 * 获取机房列表
 * @returns {Promise} 机房列表数据
 */
export const getComputerLabs = () => {
  return request({
    url: '/computer-labs',
    method: 'get'
    // 无参数
  })
}

/**
 * 根据ID获取机房详情
 * @param {number} roomId - 机房ID
 * @returns {Promise} 机房详情数据
 */
export const getComputerLabById = (roomId) => {
  return request({
    url: `/computer-labs/${roomId}`,
    method: 'get'
  })
}

/**
 * 创建机房
 * @param {Object} labData - 机房信息
 * @returns {Promise} 创建结果
 */
export const createComputerLab = (labData) => {
  return request({
    url: '/computer-labs',
    method: 'post',
    data: labData
  })
}

/**
 * 更新机房信息
 * @param {Object} labData - 机房信息
 * @returns {Promise} 更新结果
 */
export const updateComputerLab = (labData) => {
  return request({
    url: `/computer-labs/${labData.roomId}`,
    method: 'put',
    params: {
      name: labData.name,
      totalSeats: labData.totalSeats,
      allowedRoles: labData.allowedRoles,
      status: labData.status,
      managerId: labData.managerId,
      equipmentInfo: labData.equipmentInfo
    }
  })
}

/**
 * 删除机房
 * @param {number} labId - 机房ID
 * @returns {Promise} 删除结果
 */
export const deleteComputerLab = (labId) => {
  return request({
    url: `/computer-labs/${labId}`,
    method: 'delete'
  })
}

/**
 * 根据机房ID获取座位信息
 * @param {number} roomId - 机房ID
 * @returns {Promise} 座位信息数据
 */
export const getSeatsByRoomId = (roomId) => {
  return request({
    url: '/seats',
    method: 'get',
    params: {
      roomId
    }
  })
}

/**
 * 根据机房ID和时间段获取座位状态
 * @param {Object} params - 查询参数
 * @param {number} params.roomId - 机房ID
 * @param {string} params.date - 日期 (YYYY-MM-DD)
 * @param {string} params.startTime - 开始时间 (HH:mm:ss)
 * @param {string} params.endTime - 结束时间 (HH:mm:ss)
 * @returns {Promise} 座位状态数据
 */
export const getSeatsStatusByTime = ({ roomId, date, startTime, endTime }) => {
  return request({
    url: '/seats/status',
    method: 'get',
    params: {
      roomId,
      date,
      startTime,
      endTime
    }
  })
}

/**
 * 创建机房预约
 * @param {Object} bookingData - 预约信息
 * @param {number} bookingData.roomId - 机房ID
 * @param {string} bookingData.reservationDate - 预约日期 (YYYY-MM-DD)
 * @param {string} bookingData.startTime - 开始时间 (HH:mm:ss)
 * @param {string} bookingData.endTime - 结束时间 (HH:mm:ss)
 * @param {Array<string>} bookingData.seatIds - 座位ID列表，整机房预约时传空数组或不传
 * @param {string} bookingData.mode - 预约模式 ('single' | 'whole')
 * @returns {Promise} 预约结果
 */
export const createBooking = (bookingData) => {
  console.log('bookingData:', bookingData)
  return request({
    url: '/reservations',
    method: 'post',
    params: bookingData
  })
}

/**
 * 根据用户ID获取预约信息
 * @param {string} userId - 用户ID
 * @returns {Promise} 预约信息列表
 */
export const getUserReservations = (userId) => {
  return request({
    url: '/reservations/user',
    method: 'get',
    params: {
      userId
    }
  })
}

/**
 * 取消预约
 * @param {string} reservationId - 预约ID
 * @param {string} userId - 用户ID
 * @returns {Promise} 取消结果
 */
export const cancelReservation = ({ reservationId, userId }) => {
  return request({
    url: '/reservations',
    method: 'delete',
    params: {
      reservationId,
      userId
    }
  })
}

/**
 * 更新座位状态
 * @param {string} roomId - 机房ID
 * @param {string} seatId - 座位ID（单个）
 * @param {Array<string>} seatIds - 座位ID列表（批量）
 * @param {string} status - 新的状态
 * @returns {Promise} 更新后的座位对象
 */
export const updateSeatStatus = ({ roomId, seatId, seatIds, status }) => {
  return request({
    url: '/seats/status',
    method: 'put',
    params: {
      roomId,
      seatId,
      seatIds: seatIds ? seatIds.join(',') : undefined,
      status
    }
  })
}

export default {
  getComputerLabs,
  getComputerLabById,
  createComputerLab,
  updateComputerLab,
  deleteComputerLab,
  getSeatsByRoomId,
  getSeatsStatusByTime,
  createBooking,
  cancelReservation,
  getUserReservations,
  updateSeatStatus
}

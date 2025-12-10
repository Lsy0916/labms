// 预约相关 API
import request from '@/utils/request'

/**
 * 获取所有预约信息
 * @returns {Promise} 预约数据列表
 */
export const getAllReservations = () => {
  return request({
    url: '/reservations/all',
    method: 'get'
  })
}

/**
 * 更新预约状态
 * @param {string} reservationId - 预约编号
 * @param {string} status - 新状态
 * @returns {Promise} 请求结果
 */
export const updateReservationStatus = (reservationId, status) => {
  return request({
    url: '/reservations/status',
    method: 'put',
    params: {
      reservationId,
      status
    }
  })
}

/**
 * 删除预约
 * @param {string} reservationId - 预约编号
 * @returns {Promise} 请求结果
 */
export const deleteReservation = (reservationId) => {
  return request({
    url: '/deleteReservation',
    method: 'delete',
    params: {
      reservationId
    }
  })
}

export default {
  getAllReservations,
  updateReservationStatus,
  deleteReservation
}

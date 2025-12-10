// 管理员相关 API
import request from '@/utils/request'

// 获取所有教师信息
export const getAllTeachers = () => {
  return request({
    url: '/teachers',
    method: 'get'
  })
}

// 获取所有用户信息
export const getAllUsers = () => {
  return request({
    url: '/users',
    method: 'get'
  })
}

// 获取所有管理员信息
export const getAllAdmins = () => {
  return request({
    url: '/admins',
    method: 'get'
  })
}

// 创建用户（包括学生、教师、管理员）
export const createUser = (data) => {
  return request({
    url: '/admin/create',
    method: 'post',
    params: data
  })
}

// 删除用户
export const deleteUser = (data) => {
  return request({
    url: '/admin/delete',
    method: 'delete',
    params: data
  })
}

// 更新用户信息
export const updateUser = (data) => {
  return request({
    url: '/admin/update_user',
    method: 'post',
    params: data
  })
}

// 重置用户密码
export const resetUserPassword = (roleId, userId) => {
  return request({
    url: '/admin/reset_password',
    method: 'post',
    params: {
      roleId,
      userId
    }
  })
}
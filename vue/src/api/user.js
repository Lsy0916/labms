// 用户相关 API
import request from '@/utils/request'

// 登录接口
export const loginApi = (data) => {
  const roleId = data.roleId

  if (roleId === 'student') {
    return request({
      url: '/student/login',
      method: 'post',
      params: data
    })
  }
  if (roleId === 'teacher') {
    return request({
      url: '/teacher/login',
      method: 'post',
      params: data
    })
  }
  if (roleId === 'admin') {
    return request({
      url: '/admin/login',
      method: 'post',
      params: data
    })
  }
}

// 通过学号获取用户信息接口
export const UserInfoByIdApi = (userId, roleId) => {

  if (roleId === 'student') {
    return request({
      url: '/user/info_id',
      method: 'get',
      params: userId
    })
  }
  if (roleId === 'teacher') {
    return request({
      url: '/teacher/info_id',
      method: 'get',
      params: userId
    })
  }
  if (roleId === 'admin') {
    return request({
      url: '/admin/info_id',
      method: 'get',
      params: userId
    })
  }
}

// 更新个人信息接口
export const updateStudentInfo = (data) => {
  if (data.roleId === 'student' || data.roleId === 'teacher') {
    return request({
      url: '/user/update',
      method: 'post',
      params: data
    })
  }
  if (data.roleId === 'admin') {
    return request({
      url: '/admin/update',
      method: 'post',
      params: data
    })
  }
}

// 修改密码
export const updatePassword = (data) => {
  if (data.roleId === 'student') {
    return request({
      url: '/user/update_password',
      method: 'post',
      params: data
    })
  }
  if (data.roleId === 'teacher') {
    return request({
      url: '/teacher/update_password',
      method: 'post',
      params: data
    })
  }
  if (data.roleId === 'admin') {
    return request({
      url: '/admin/update_password',
      method: 'post',
      params: data
    })
  }
}

// 获取所有学生信息接口
export const getAllStudents = () => {
  return request({
    url: '/usersInfo',
    method: 'get',
    params: {
      roleId: 'student'
    }
  })
}

// 新增学生接口
export const addStudent = (data) => {
  return request({
    url: '/user/add_student',
    method: 'post',
    params: data
  })
}

// 删除学生接口
export const deleteStudent = (userId) => {
  return request({
    url: '/user/delete_student',
    method: 'delete',
    params: {
      userId
    }
  })
}

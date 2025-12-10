import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import MainLayout from '@/layouts/MainLayout.vue'
import { useAuthStore } from '@/stores/auth'

// 根据角色获取对应的仪表板组件
const getDashboardByRole = (roleId) => {
  switch (roleId) {
    case 'student':
      return () => import('@/views/student/DashboardView.vue')
    case 'teacher':
      return () => import('@/views/teacher/DashboardView.vue')
    case 'admin':
      return () => import('@/views/admin/DashboardView.vue')
  }
}

// 根据角色获取对应的个人信息页面组件
const getProfileByRole = (roleId) => {
  switch (roleId) {
    case 'student':
      return () => import('@/views/student/ProfileView.vue')
    case 'teacher':
      return () => import('@/views/teacher/ProfileView.vue')
    case 'admin':
      return () => import('@/views/admin/ProfileView.vue')
  }
}

// 根据角色获取对应的课表页面组件
const getScheduleByRole = (roleId) => {
  switch (roleId) {
    case 'student':
      return () => import('@/views/student/ScheduleView.vue')
    case 'teacher':
      return () => import('@/views/teacher/ScheduleView.vue')
    case 'admin':
      return () => import('@/views/admin/ScheduleView.vue')
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresGuest: true }
    },
    {
      path: '/student',
      name: 'student-login',
      component: () => import('@/views/student/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/teacher',
      name: 'teacher-login',
      component: () => import('@/views/teacher/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/admin',
      name: 'admin-login',
      component: () => import('@/views/admin/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: { name: 'dashboard' }
        },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => {
            // 获取当前用户角色并返回对应的仪表板组件
            const authStore = useAuthStore()
            // 确保在组件加载时重新获取最新的roleId
            authStore.refreshAuthStatus()
            return getDashboardByRole(authStore.roleId)()
          },
          meta: { title: '首页' }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => {
            // 获取当前用户角色并返回对应的个人信息页面组件
            const authStore = useAuthStore()
            // 确保在组件加载时重新获取最新的roleId
            authStore.refreshAuthStatus()
            return getProfileByRole(authStore.roleId)()
          },
          meta: { title: '个人信息' }
        },
        {
          path: 'schedule',
          name: 'schedule',
          component: () => {
            // 获取当前用户角色并返回对应的课表页面组件
            const authStore = useAuthStore()
            // 确保在组件加载时重新获取最新的roleId
            authStore.refreshAuthStatus()
            return getScheduleByRole(authStore.roleId)()
          },
          meta: { title: '我的课表' }
        },
        {
          path: 'booking',
          name: 'Booking',
          component: () => {
            // 获取当前用户角色并返回对应的预约页面组件
            const authStore = useAuthStore()
            // 确保在组件加载时重新获取最新的roleId
            authStore.refreshAuthStatus()
            
            // 根据角色动态导入对应的预约组件
            if (authStore.roleId === 'student') {
              return import('@/views/student/BookingView.vue')
            } else if (authStore.roleId === 'teacher') {
              return import('@/views/teacher/BookingView.vue')
            }
            // 默认返回学生预约页面
            return import('@/views/student/BookingView.vue')
          },
          meta: { title: '机房预约' }
        },
        {
          path: 'my-bookings',
          name: 'MyBookings',
          component: () => import('@/views/student/MyBookingView.vue'),
          meta: { title: '我的预约' }
        },
        {
          path: 'students',
          name: 'Students',
          component: () => import('@/views/teacher/StudentManagementView.vue'),
          meta: { title: '学生管理' }
        },
        {
          path: 'reservations',
          name: 'Reservations',
          component: () => import('@/views/teacher/ReservationManagementView.vue'),
          meta: { title: '预约管理' }
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/admin/UserManagementView.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'rooms',
          name: 'Rooms',
          component: () => import('@/views/admin/RoomManagementView.vue'),
          meta: { title: '机房管理' }
        },
        {
          path: 'seats',
          name: 'Seats',
          component: () => import('@/views/admin/SeatManagementView.vue'),
          meta: { title: '座位管理' }
        },
        {
          path: 'reservation-audit',
          name: 'ReservationAudit',
          component: () => import('@/views/admin/ReservationAuditView.vue'),
          meta: { title: '预约审核' }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/404.vue')
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  // 在路由守卫中也刷新一次状态确保是最新的
  authStore.refreshAuthStatus()

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    // 如果需要登录权限，但当前未登录，则重定向到登录页面
    next({ name: 'login' })
  } else if (to.meta.requiresGuest && authStore.isLoggedIn) {
    // 如果需要游客权限，但当前已登录，则重定向到仪表板页面
    next({ name: 'dashboard' })
  } else {
    // 否则，继续导航
    next()
  }
})

export default router

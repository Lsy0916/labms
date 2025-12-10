<!-- 主布局组件 -->
<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside
      :width="isCollapse ? '64px' : '200px'"
      class="sidebar-container"
      :class="{ 'sidebar-collapse': isCollapse }"
    >
      <div class="sidebar-logo">
        <img v-if="!isCollapse" src="@/assets/logo.svg" alt="Logo" class="logo-img" />
        <span v-if="!isCollapse" class="logo-title">机房管理系统</span>
      </div>

      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          unique-opened
          router
        >
          <template v-for="route in permissionRoutes" :key="route.path">
            <el-menu-item
              v-if="!route.children || route.children.length === 0"
              :index="route.path"
            >
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <template #title>{{ route.meta?.title }}</template>
            </el-menu-item>

            <el-sub-menu
              v-else
              :index="route.path"
            >
              <template #title>
                <el-icon v-if="route.meta?.icon">
                  <component :is="route.meta.icon" />
                </el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>

              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="route.path + '/' + child.path"
              >
                {{ child.meta?.title }}
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 主内容区域 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header-container">
        <div class="navbar-left">
          <el-button
            class="menu-toggle"
            @click="toggleSidebar"
            circle
            :icon="isCollapse ? Expand : Fold"
          />
        </div>

        <div class="navbar-right">
          <!-- 深色模式切换 -->
          <el-switch
            v-model="isDark"
            inline-prompt
            :active-icon="Moon"
            :inactive-icon="Sunny"
            @change="toggleDark"
          />

          <!-- 用户信息下拉菜单 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-dropdown">
              <el-avatar :size="32" :src="userAvatar" />
              <span class="user-name">{{ userInfo.name }}</span>
            </div>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="main-container">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import { Fold, Expand, Sunny, Moon } from '@element-plus/icons-vue'

// 使用 Pinia store
const authStore = useAuthStore()
const { userInfo } = storeToRefs(authStore)
const { roleId } = storeToRefs(authStore)

// 使用用户提供的头像URL，如果没有则构建默认路径
const userAvatar = computed(() => {
  if (userInfo.value && userInfo.value.avatarUrl) {
    return 'src/assets/avatar/' + roleId.value + '/' + userInfo.value.avatarUrl
  }
  return ''
})

// 路由相关
const route = useRoute()
const router = useRouter()

// 响应式状态
const isCollapse = ref(false)
const isDark = ref(false)

// 计算属性
const activeMenu = computed(() => route.path)
const permissionRoutes = computed(() => {
  // 根据角色过滤路由
  const roleId = userInfo.value?.roleId
  return getRoutesByRole(roleId)
})

// 方法
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const toggleDark = (val) => {
  isDark.value = val === true || val === 'true' || val === 1 || val === '1'
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
  localStorage.setItem('darkMode', isDark.value)
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      authStore.logout()
      router.push('/login')
      break
  }
}

// 根据角色获取路由
const getRoutesByRole = (roleId) => {
  // 根据角色返回不同的路由配置

  const routes = {
    student: [
      {
        path: '/dashboard',
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: '/profile',
        meta: { title: '个人信息', icon: 'User' }
      },
      {
        path: '/schedule',
        meta: { title: '我的课表', icon: 'Calendar' }
      },
      {
        path: '/booking',
        meta: { title: '机房预约', icon: 'Monitor' }
      },
      {
        path: '/my-bookings',
        meta: { title: '我的预约', icon: 'Calendar' }
      },
      {
        path: '/notice',
        meta: { title: '机房公告', icon: 'Bell' }
      }
    ],
    teacher: [
      {
        path: '/dashboard',
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: '/profile',
        meta: { title: '个人信息', icon: 'User' }
      },
      {
        path: '/students',
        meta: { title: '学生信息', icon: 'User' }
      },
      {
        path: '/schedule',
        meta: { title: '我的课表', icon: 'Calendar' }
      },
      {
        path: '/booking',
        meta: { title: '机房预约', icon: 'Monitor' }
      },
      {
        path: '/my-bookings',
        meta: { title: '我的预约', icon: 'Calendar' }
      },
      {
        path: '/reservations',
        meta: { title: '预约管理', icon: 'Calendar' }
      },
      {
        path: '/notice',
        meta: { title: '机房公告', icon: 'Bell' }
      }
    ],
    admin: [
      {
        path: '/dashboard',
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: '/profile',
        meta: { title: '个人信息', icon: 'User' }
      },
      {
        path: '/users',
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: '/rooms',
        meta: { title: '机房管理', icon: 'Monitor' }
      },
      {
        path: '/seats',
        meta: { title: '座位管理', icon: 'Tickets' }
      },
      {
        path: '/reservation-audit',
        meta: { title: '预约审核', icon: 'Check' }
      },
      {
        path: '/logs',
        meta: { title: '系统日志', icon: 'Document' }
      }
    ]
  }

  return routes[roleId] || []
}

// 响应式处理
const handleResize = () => {
  const width = document.body.clientWidth
  if (width < 768) {
    isCollapse.value = true
  } else {
    isCollapse.value = false
  }
}

// 生命周期钩子
onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)

  // 恢复深色模式设置
  const darkMode = localStorage.getItem('darkMode') === 'true'
  isDark.value = darkMode
  if (darkMode) {
    document.documentElement.classList.add('dark')
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  border: none;
  margin: 0;
  padding: 0;

  .sidebar-container {
    background-color: #304156;
    transition: width 0.28s;
    box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
    overflow: hidden;
    border: none;
    margin: 0;
    padding: 0;

    .sidebar-logo {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 50px;
      line-height: 50px;
      background: #2b2f3a;
      border: none;
      margin: 0;
      padding: 0;

      .logo-img {
        width: 32px;
        height: 32px;
        margin-right: 12px;
      }

      .logo-title {
        color: #ffffff;
        font-weight: 600;
        font-size: 16px;
        white-space: nowrap;
      }
    }

    :deep(.el-menu) {
      border: none;
      height: calc(100% - 50px);
      margin: 0;
      padding: 0;
    }
  }

  .sidebar-collapse {
    .sidebar-logo {
      .logo-img {
        margin-right: 0;
      }

      .logo-title {
        display: none;
      }
    }
  }

  .header-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 50px;
    padding: 0 20px;
    background: #ffffff;
    border: none;
    margin: 0;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);

    .navbar-left {
      display: flex;
      align-items: center;
    }

    .navbar-right {
      display: flex;
      align-items: center;
      gap: 20px;

      .user-dropdown {
        display: flex;
        align-items: center;
        cursor: pointer;
        border: none;

        .user-name {
          margin-left: 10px;
          font-size: 14px;
        }
      }
    }
  }

  .main-container {
    background-color: #f0f2f5;
    padding: 20px;
    margin: 0;
    overflow: auto;
    border: none;

    :deep(.el-scrollbar) {
      border: none;
    }
  }
}

// 深色主题样式
html.dark {
  .header-container {
    background: #1d1e1f;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  }

  .main-container {
    background-color: #0a0a0a;
    padding: 20px;
  }

  .sidebar-container {
    background-color: #304156;

    .sidebar-logo {
      background: #121212;
    }
  }
}
</style>

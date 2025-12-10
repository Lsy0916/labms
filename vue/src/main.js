import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import 'default-passive-events' // 添加这一行来解决 passive event listener 警告

const app = createApp(App)
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 初始化认证状态
const initAuth = () => {
  const authStore = useAuthStore()
  authStore.refreshAuthStatus()
}

// 在应用挂载前初始化认证状态
initAuth()

// 监听页面关闭事件，在页面关闭时根据需要清除本地存储的敏感信息
window.addEventListener('beforeunload', () => {
  // 获取auth store实例
  const authStore = useAuthStore()
  // 如果存在store且用户选择不记住登录信息，则清除本地存储
  if (authStore && !authStore.rememberMe) {
    sessionStorage.clear()
  }
})

// 监听路由变化，在每次路由变化时刷新认证状态
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  authStore.refreshAuthStatus()
  next()
})

app.mount('#app')
# 学校机房上机管理系统

这是一个基于 Vue 3 + Pinia + Element Plus 的学校机房上机管理系统前端项目。

## 功能特点

- 支持学生、教师、管理员三种角色
- 响应式布局，支持深色模式
- 机房预约、座位可视化选择
- 课表管理、公告系统
- 权限控制和路由守卫

## 推荐 IDE 设置

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (并禁用 Vetur)。

## 推荐浏览器设置

- 基于 Chromium 的浏览器 (Chrome, Edge, Brave, 等):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd) 
  - [在 Chrome DevTools 中开启自定义对象格式化](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [在 Firefox DevTools 中开启自定义对象格式化](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## 项目配置

查看 [Vite 配置参考](https://vite.dev/config/)。

## 项目设置

```sh
npm install
```

### 编译和热重载以进行开发

```sh
npm run dev
```

### 编译和压缩以用于生产

```sh
npm run build
```

## 登录测试账号

- 学生: 2021080101 / 123456
- 教师: T2022001 / 123456
- 管理员: admin / 123456

## 目录结构

```
src/
├── api/          # 按模块拆分的 mock 接口
├── assets/       # 静态资源
├── components/   # 全局通用组件
├── composables/  # 组合式函数
├── layouts/      # 布局组件
├── plugins/      # 插件
├── router/       # 路由配置
├── stores/       # Pinia 状态管理
├── styles/       # 样式文件
├── utils/        # 工具函数
└── views/        # 页面视图
    ├── student/  # 学生页面
    ├── teacher/  # 教师页面
    └── admin/    # 管理员页面
```
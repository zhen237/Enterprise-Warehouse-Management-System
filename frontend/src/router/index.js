/**
 * 前端路由配置
 * 定义页面路由映射和导航守卫
 *
 * 路由结构：
 *   /login                  登录页（无需认证）
 *   /                       主布局（包含侧边栏、顶栏、内容区）
 *     ├── /                 首页（仪表盘）
 *     ├── /products         商品管理
 *     ├── /warehouses       仓库管理
 *     ├── /inventory        库存查询
 *     ├── /inbound          入库管理
 *     ├── /outbound         出库管理
 *     ├── /check            库存盘点
 *     └── /reports          数据统计报表
 *     └── /users            人员管理（仅管理员）
 *
 * 导航守卫：
 *   - 未登录访问受保护页面时，自动跳转至登录页
 *   - 已登录访问登录页时，直接进入主布局
 */
import { createRouter, createWebHistory } from 'vue-router'
import { getCurrentUser } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/Products.vue')
      },
      {
        path: 'warehouses',
        name: 'Warehouses',
        component: () => import('@/views/Warehouses.vue')
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/Inventory.vue')
      },
      {
        path: 'inbound',
        name: 'Inbound',
        component: () => import('@/views/Inbound.vue')
      },
      {
        path: 'outbound',
        name: 'Outbound',
        component: () => import('@/views/Outbound.vue')
      },
      {
        path: 'check',
        name: 'Check',
        component: () => import('@/views/Check.vue')
      },
      {
        path: 'reports',
        name: 'Reports',
        component: () => import('@/views/Reports.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next()
    return
  }
  
  const user = getCurrentUser()
  if (!user) {
    next('/login')
  } else {
    next()
  }
})

export default router
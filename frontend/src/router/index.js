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
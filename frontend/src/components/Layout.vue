<template>
  <div class="layout">
    <el-container>
      <el-aside width="200px" class="aside">
        <div class="logo">
          <h2>仓库管理系统</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="menu"
          mode="vertical"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/">
            <el-icon><component :is="icons.HomeFilled" /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/products">
            <el-icon><component :is="icons.Briefcase" /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/warehouses">
            <el-icon><component :is="icons.DataBoard" /></el-icon>
            <span>仓库管理</span>
          </el-menu-item>
          <el-menu-item index="/inventory">
            <el-icon><component :is="icons.Box" /></el-icon>
            <span>库存查询</span>
          </el-menu-item>
          <el-menu-item index="/inbound">
            <el-icon><component :is="icons.ArrowDown" /></el-icon>
            <span>入库管理</span>
          </el-menu-item>
          <el-menu-item index="/outbound">
            <el-icon><component :is="icons.ArrowUp" /></el-icon>
            <span>出库管理</span>
          </el-menu-item>
          <el-menu-item index="/check">
            <el-icon><component :is="icons.Check" /></el-icon>
            <span>库存盘点</span>
          </el-menu-item>
          <el-menu-item index="/reports">
            <el-icon><component :is="icons.DataAnalysis" /></el-icon>
            <span>报表统计</span>
          </el-menu-item>
        </el-menu>

        <div class="guide-section">
          <div class="guide-header" @click="guideOpen = !guideOpen">
            <span>📋 操作流程指南</span>
            <el-icon :class="{ rotate: guideOpen }"><ArrowDown /></el-icon>
          </div>
          <div v-show="guideOpen" class="guide-steps">
            <div
              v-for="(step, idx) in guideSteps"
              :key="idx"
              class="guide-step"
              :class="{ active: activeMenu === step.path }"
              @click="handleMenuSelect(step.path)"
            >
              <div class="step-num">{{ idx + 1 }}</div>
              <div class="step-content">
                <div class="step-title">{{ step.title }}</div>
                <div class="step-desc">{{ step.desc }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-right">
            <span>欢迎, {{ user?.name }}</span>
            <el-button type="text" @click="logout">退出登录</el-button>
          </div>
        </el-header>

        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  HomeFilled, 
  Briefcase, 
  DataBoard, 
  Box, 
  ArrowDown, 
  ArrowUp, 
  Check, 
  DataAnalysis 
} from '@element-plus/icons-vue'
import { getCurrentUser, removeCurrentUser } from '@/store/user'

const router = useRouter()
const route = useRoute()

const icons = { HomeFilled, Briefcase, DataBoard, Box, ArrowDown, ArrowUp, Check, DataAnalysis }

const user = ref(getCurrentUser())
const guideOpen = ref(true)

const guideSteps = [
  { path: '/', title: '① 登录系统', desc: '用户名密码登录' },
  { path: '/products', title: '② 添加商品', desc: '商品管理→添加' },
  { path: '/warehouses', title: '③ 创建仓库', desc: '仓库管理→添加' },
  { path: '/inbound', title: '④ 商品入库', desc: '入库申请→确认' },
  { path: '/inventory', title: '⑤ 查看库存', desc: '实时库存查询' },
  { path: '/outbound', title: '⑥ 商品出库', desc: '出库申请→确认' },
  { path: '/check', title: '⑦ 库存盘点', desc: '盘点→确认覆盖' },
  { path: '/reports', title: '⑧ 统计报表', desc: '查看统计与分析' }
]

const activeMenu = computed(() => {
  return route.path
})

function handleMenuSelect(key) {
  router.push(key)
}

function logout() {
  removeCurrentUser()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.aside {
  background-color: #2c3e50;
  color: white;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #34495e;
}

.logo h2 {
  margin: 0;
  font-size: 16px;
}

.menu {
  border-right: none;
  background-color: transparent;
}

.el-menu-item {
  color: #ecf0f1;
}

.el-menu-item:hover,
.el-menu-item.is-active {
  background-color: #34495e;
}

.header {
  background-color: #ffffff;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-right: 20px;
  border-bottom: 1px solid #eee;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.guide-section {
  margin: 10px;
  background-color: #1f2d3d;
  border-radius: 8px;
  overflow: hidden;
}

.guide-header {
  padding: 10px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #ecf0f1;
  font-size: 13px;
  font-weight: bold;
  cursor: pointer;
  border-bottom: 1px solid #34495e;
}

.guide-header .el-icon {
  transition: transform 0.3s;
}

.guide-header .rotate {
  transform: rotate(180deg);
}

.guide-steps {
  padding: 8px 0;
}

.guide-step {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 6px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #ecf0f1;
  font-size: 12px;
}

.guide-step:hover {
  background-color: #34495e;
}

.guide-step.active {
  background-color: #3b82f6;
  color: white;
}

.step-num {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #3b82f6;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: bold;
  flex-shrink: 0;
}

.guide-step.active .step-num {
  background-color: white;
  color: #3b82f6;
}

.step-content {
  flex: 1;
  min-width: 0;
}

.step-title {
  font-size: 12px;
  margin-bottom: 2px;
}

.step-desc {
  font-size: 10px;
  color: #95a5a6;
  line-height: 1.3;
}

.guide-step.active .step-desc {
  color: rgba(255, 255, 255, 0.8);
}
</style>
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
</style>
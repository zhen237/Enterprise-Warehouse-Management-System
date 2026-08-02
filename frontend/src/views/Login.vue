<!--
  登录页组件
  功能：用户通过用户名和密码登录系统
  Mock 模式：在 GitHub Pages 等无后端环境下，直接调用 mock 函数登录
-->
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>企业仓库管理系统</h2>
        <p class="subtitle">Enterprise Warehouse Management System</p>
      </div>
      
      <div v-if="isMock" class="mock-banner">
        <span>演示模式 · 可直接体验</span>
      </div>
      
      <el-form :model="form" class="login-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password @keyup.enter="login" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="login" :loading="loading">登 录</el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <p v-if="isMock">演示账号: admin / admin123</p>
        <p v-else>管理员: admin / admin123　　操作员: operator / operator123</p>
      </div>
      
      <div v-if="isMock" class="demo-section">
        <el-button type="success" @click="quickLogin('admin')">管理员演示</el-button>
        <el-button type="warning" @click="quickLogin('operator')">操作员演示</el-button>
      </div>
    </div>
    
    <p class="footer">© 2026 Enterprise Warehouse Management System</p>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import { isMockMode, mockLogin } from '@/mock/data.js'
import { setCurrentUser } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

const isMock = ref(false)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

onMounted(() => {
  isMock.value = isMockMode()
  if (isMock.value) {
    form.username = 'admin'
    form.password = 'admin123'
  }
})

// Mock 模式下直接登录
function mockLoginDirect(username, password) {
  try {
    const user = mockLogin(username, password)
    return user
  } catch (e) {
    throw e
  }
}

async function login() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    let user
    
    if (isMock.value) {
      // Mock 模式：直接调用 mock 函数
      user = mockLoginDirect(form.username, form.password)
    } else {
      // 正常模式：调用 API
      user = await api.post('/auth/login', form)
    }
    
    setCurrentUser(user)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

async function quickLogin(role) {
  const accounts = {
    admin: { username: 'admin', password: 'admin123' },
    operator: { username: 'operator', password: 'operator123' }
  }
  const account = accounts[role]
  if (!account) return
  
  loading.value = true
  try {
    let user
    
    if (isMock.value) {
      user = mockLoginDirect(account.username, account.password)
    } else {
      user = await api.post('/auth/login', account)
    }
    
    setCurrentUser(user)
    ElMessage.success(`${user.name} 登录成功`)
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}

.login-box {
  background: #fff;
  padding: 40px 36px 32px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 28px;
}

.login-header h2 {
  color: #303133;
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 4px;
}

.subtitle {
  color: #909399;
  font-size: 12px;
  margin: 0;
  letter-spacing: 0.5px;
}

.login-form {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__label) {
  color: #606266;
  font-size: 14px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 4px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.login-btn {
  width: 100%;
  height: 40px;
  border-radius: 4px;
  font-size: 15px;
  letter-spacing: 4px;
}

.tips {
  text-align: center;
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}

.tips p {
  margin: 4px 0;
}

.mock-banner {
  background: #ecf5ff;
  color: #409eff;
  padding: 6px 12px;
  border-radius: 4px;
  text-align: center;
  margin-bottom: 20px;
  font-size: 13px;
  border: 1px solid #d9ecff;
}

.demo-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 10px;
}

.demo-section .el-button {
  flex: 1;
}

.footer {
  margin-top: 24px;
  color: #c0c4cc;
  font-size: 12px;
}
</style>

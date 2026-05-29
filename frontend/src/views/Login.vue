<template>
  <div class="login-container">
    <div class="login-box">
      <h2>企业仓库管理系统</h2>
      <el-form :model="form" ref="formRef" label-width="80px" class="login-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="login">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="tips">
        <p>管理员账号: admin / admin123</p>
        <p>操作员账号: operator / operator123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import { setCurrentUser } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

const formRef = ref(null)
const form = reactive({
  username: '',
  password: ''
})

async function login() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  try {
    const response = await api.post('/auth/login', form)
    setCurrentUser(response)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message)
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 20%, rgba(120, 219, 255, 0.2) 0%, transparent 40%);
  animation: gradient-shift 15s ease infinite;
}

@keyframes gradient-shift {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.login-box {
  background: rgba(255, 255, 255, 0.98);
  padding: 48px 40px;
  border-radius: 20px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.3),
    0 0 40px rgba(120, 119, 198, 0.1);
  width: 420px;
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 10;
  transform: translateY(0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 
    0 25px 70px rgba(0, 0, 0, 0.35),
    0 0 50px rgba(120, 119, 198, 0.2);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 36px;
  color: #1e293b;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 0.5px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-form {
  margin-bottom: 28px;
}

.login-form :deep(.el-form-item__label) {
  color: #334155;
  font-weight: 500;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  padding: 8px 15px;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea, 0 4px 15px rgba(102, 126, 234, 0.25);
}

.login-btn {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  margin-top: 10px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.login-btn:active {
  transform: translateY(0);
}

.tips {
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.8;
  padding-top: 10px;
  border-top: 1px dashed #e2e8f0;
}

.tips p {
  margin: 6px 0;
}
</style>
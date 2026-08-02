<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>人员管理</span>
          <el-button type="primary" @click="openDialog()">添加人员</el-button>
        </div>
      </template>

      <!-- 表格 -->
      <el-table :data="users" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="role" label="角色" width="150">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.enabled ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑人员' : '添加人员'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" placeholder="至少6位" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员 (ADMIN)" value="ADMIN" />
            <el-option label="操作员 (OPERATOR)" value="OPERATOR" />
            <el-option label="普通员工 (EMPLOYEE)" value="EMPLOYEE" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../utils/api.js'

const users = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  role: '',
  phone: ''
})

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' },
             { min: 6, message: '密码至少6位', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 角色标签颜色
const getRoleTagType = (role) => {
  const map = { ADMIN: 'danger', OPERATOR: 'warning', EMPLOYEE: 'info' }
  return map[role] || ''
}

// 角色中文标签
const getRoleLabel = (role) => {
  const map = { ADMIN: '管理员', OPERATOR: '操作员', EMPLOYEE: '普通员工' }
  return map[role] || role
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const userList = await api.get('/users')
    users.value = userList
  } catch (err) {
    console.error('加载用户失败:', err)
    ElMessage.error('获取用户列表失败: ' + (err.message || '未知错误'))
  }
}

// 打开弹窗（添加）
const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, { ...row, password: '' })
  } else {
    Object.assign(form, { id: null, username: '', password: '', name: '', role: '', phone: '' })
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (isEdit.value) {
        await api.put(`/users/${form.id}`, form)
        ElMessage.success('更新成功')
      } else {
        await api.post('/users', form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadUsers()
    } catch (err) {
      ElMessage.error(err.message || '操作失败')
    }
  })
}

// 启用/禁用用户
const toggleStatus = async (row) => {
  try {
    await api.patch(`/users/${row.id}/toggle?enabled=${!row.enabled}`)
    ElMessage.success(`已${row.enabled ? '禁用' : '启用'}用户`)
    loadUsers()
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  }
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除用户"${row.name}"吗？`, '提示', { type: 'warning' })
    await api.delete(`/users/${row.id}`)
    ElMessage.success('删除成功')
    loadUsers()
  } catch {}
}

onMounted(() => loadUsers())
</script>

<style scoped>
.users-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>

<template>
  <div class="warehouses-page">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>仓库管理</span>
          <el-button type="primary" @click="showAddModal = true">添加仓库</el-button>
        </div>
      </template>
      
      <el-table :data="warehouses" border show-summary>
        <el-table-column prop="warehouseCode" label="仓库编码" width="120" />
        <el-table-column prop="warehouseName" label="仓库名称" width="150" />
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column prop="manager" label="负责人" width="100" />
        <el-table-column prop="totalItems" label="商品种类数" width="120">
          <template #default="scope">
            {{ scope.row.totalItems || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="总价值" width="150">
          <template #default="scope">
            ¥{{ formatNumber(scope.row.totalValue || 0) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="text" @click="editWarehouse(scope.row)">编辑</el-button>
            <el-button type="text" @click="deleteWarehouse(scope.row)" style="color: #f56c6c">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog :title="editForm.id ? '编辑仓库' : '添加仓库'" v-model="showAddModal" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="仓库编码">
          <el-input v-model="editForm.warehouseCode" :disabled="!!editForm.id" />
        </el-form-item>
        <el-form-item label="仓库名称">
          <el-input v-model="editForm.warehouseName" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="editForm.location" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="editForm.manager" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveWarehouse">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const warehouses = ref([])
const showAddModal = ref(false)

const editForm = reactive({
  id: null,
  warehouseCode: '',
  warehouseName: '',
  location: '',
  manager: ''
})

onMounted(() => {
  loadWarehouses()
})

function formatNumber(value) {
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function loadWarehouses() {
  try {
    warehouses.value = await api.get('/warehouses/statistics')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function editWarehouse(warehouse) {
  editForm.id = warehouse.warehouseId || warehouse.id
  editForm.warehouseCode = warehouse.warehouseCode
  editForm.warehouseName = warehouse.warehouseName
  editForm.location = warehouse.location
  editForm.manager = warehouse.manager
  showAddModal.value = true
}

function deleteWarehouse(warehouse) {
  const id = warehouse.warehouseId || warehouse.id
  ElMessageBox.confirm(`确定删除仓库 ${warehouse.warehouseName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await api.delete(`/warehouses/${id}`)
      ElMessage.success('删除成功')
      loadWarehouses()
    } catch (error) {
      ElMessage.error(error.message)
    }
  })
}

async function saveWarehouse() {
  if (!editForm.warehouseCode || !editForm.warehouseName) {
    ElMessage.warning('请填写仓库编码和名称')
    return
  }
  
  try {
    if (editForm.id) {
      await api.put(`/warehouses/${editForm.id}`, editForm)
      ElMessage.success('更新成功')
    } else {
      await api.post('/warehouses', editForm)
      ElMessage.success('添加成功')
    }
    showAddModal.value = false
    resetForm()
    loadWarehouses()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function resetForm() {
  editForm.id = null
  editForm.warehouseCode = ''
  editForm.warehouseName = ''
  editForm.location = ''
  editForm.manager = ''
}
</script>

<style scoped>
.warehouses-page {
  padding: 0;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
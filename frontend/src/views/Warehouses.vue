<!--
  仓库管理页面组件
  功能：仓库的增删改查、统计视图（品种数、总价值）
  接口：
    GET    /api/warehouses              获取所有仓库
    GET    /api/warehouses/statistics   获取仓库统计信息
    POST   /api/warehouses              新增仓库（仅管理员）
    PUT    /api/warehouses/{id}         修改仓库（仅管理员）
    DELETE /api/warehouses/{id}         删除仓库（仅管理员）
-->
<template>
  <div class="warehouses-page">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>仓库管理</span>
          <div>
            <el-button @click="showStats = !showStats" type="warning" plain>
              {{ showStats ? '返回列表' : '查看统计' }}
            </el-button>
            <el-button v-if="isAdmin()" type="primary" @click="showAddModal = true">添加仓库</el-button>
          </div>
        </div>
      </template>

      <div v-if="!showStats">
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
              <el-button v-if="isAdmin()" type="text" @click="editWarehouse(scope.row)">编辑</el-button>
              <el-button v-if="isAdmin()" type="text" @click="deleteWarehouse(scope.row)" style="color: #f56c6c">删除</el-button>
              <el-text v-if="!isAdmin()" type="info" size="small">仅管理员可操作</el-text>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-else>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">仓库总数</div>
              <div class="stat-value">{{ warehouses.length }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">商品总种类</div>
              <div class="stat-value">{{ totalItemCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">库存总价值</div>
              <div class="stat-value">¥{{ formatNumber(totalValue) }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">平均单仓价值</div>
              <div class="stat-value">¥{{ formatNumber(avgValue) }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-divider content-position="left">各仓库价值对比</el-divider>

        <el-table :data="warehouses" border>
          <el-table-column prop="warehouseName" label="仓库" width="180" />
          <el-table-column prop="location" label="位置" width="150" />
          <el-table-column prop="manager" label="负责人" width="120" />
          <el-table-column prop="totalItems" label="种类数" width="120" />
          <el-table-column label="总价值(元)" width="180">
            <template #default="scope">
              ¥{{ formatNumber(scope.row.totalValue || 0) }}
            </template>
          </el-table-column>
          <el-table-column label="占比" width="200">
            <template #default="scope">
              <el-progress
                :percentage="totalValue > 0 ? Math.round(((scope.row.totalValue || 0) / totalValue) * 100) : 0"
                :stroke-width="18"
                :text-inside="true"
                status="success"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
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
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { isAdmin } from '@/store/user'

const warehouses = ref([])
const showAddModal = ref(false)
const showStats = ref(false)

const totalItemCount = computed(() => {
  return warehouses.value.reduce((sum, w) => sum + (w.totalItems || 0), 0)
})

const totalValue = computed(() => {
  return warehouses.value.reduce((sum, w) => sum + (w.totalValue || 0), 0)
})

const avgValue = computed(() => {
  if (warehouses.value.length === 0) return 0
  return totalValue.value / warehouses.value.length
})

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

.stat-card {
  text-align: center;
  margin-bottom: 20px;
}

.stat-card .stat-label {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-card .stat-value {
  color: #303133;
  font-size: 24px;
  font-weight: bold;
}
</style>

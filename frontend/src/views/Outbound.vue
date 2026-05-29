<template>
  <div class="outbound-page">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>出库管理</span>
          <el-button type="primary" @click="showAddModal = true">新建出库</el-button>
        </div>
      </template>
      
      <el-table :data="outboundRecords" border>
        <el-table-column prop="outboundNo" label="出库单号" width="150" />
        <el-table-column prop="product.productName" label="商品名称" width="150" />
        <el-table-column prop="warehouse.warehouseName" label="仓库" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="outboundTime" label="出库时间" width="180" />
        <el-table-column prop="operator.name" label="操作员" width="100" />
        <el-table-column prop="confirmed" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.confirmed ? 'success' : 'warning'">
              {{ scope.row.confirmed ? '已确认' : '待确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="text" v-if="!scope.row.confirmed" @click="confirmOutbound(scope.row)">确认出库</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog title="新建出库" v-model="showAddModal" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品">
          <el-select v-model="form.productId" placeholder="选择商品">
            <el-option v-for="p in products" :key="p.id" :label="p.productName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库">
          <el-select v-model="form.warehouseId" placeholder="选择仓库">
            <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="form.quantity" type="number" />
        </el-form-item>
        <el-form-item label="备注">
          <el-textarea v-model="form.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="submitOutbound">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCurrentUser } from '@/store/user'

const outboundRecords = ref([])
const products = ref([])
const warehouses = ref([])
const showAddModal = ref(false)

const form = reactive({
  productId: null,
  warehouseId: null,
  quantity: null,
  remark: ''
})

onMounted(async () => {
  await loadData()
})

async function loadData() {
  await Promise.all([
    loadOutboundRecords(),
    loadProducts(),
    loadWarehouses()
  ])
}

async function loadOutboundRecords() {
  try {
    outboundRecords.value = await api.get('/inventory/outbound')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function loadProducts() {
  try {
    products.value = await api.get('/products')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function loadWarehouses() {
  try {
    warehouses.value = await api.get('/warehouses')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function submitOutbound() {
  if (!form.productId || !form.warehouseId || !form.quantity) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const user = getCurrentUser()
  
  try {
    await api.post(`/inventory/outbound?operatorId=${user.id}`, form)
    ElMessage.success('出库申请成功')
    showAddModal.value = false
    resetForm()
    loadOutboundRecords()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function confirmOutbound(record) {
  ElMessageBox.confirm(`确定确认出库单号 ${record.outboundNo} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await api.post(`/inventory/outbound/${record.id}/confirm`)
      ElMessage.success('确认成功')
      loadOutboundRecords()
    } catch (error) {
      ElMessage.error(error.message)
    }
  })
}

function resetForm() {
  form.productId = null
  form.warehouseId = null
  form.quantity = null
  form.remark = ''
}
</script>

<style scoped>
.outbound-page {
  padding: 0;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
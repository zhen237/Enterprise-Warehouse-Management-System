<template>
  <div class="inbound-page">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>入库管理</span>
          <el-button type="primary" @click="showAddModal = true">新建入库</el-button>
        </div>
      </template>
      
      <el-table :data="inboundRecords" border>
        <el-table-column prop="inboundNo" label="入库单号" width="150" />
        <el-table-column prop="product.productName" label="商品名称" width="150" />
        <el-table-column prop="warehouse.warehouseName" label="仓库" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="scope">¥{{ scope.row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="inboundTime" label="入库时间" width="180" />
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
            <el-button type="text" v-if="!scope.row.confirmed" @click="confirmInbound(scope.row)">确认入库</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog title="新建入库" v-model="showAddModal" width="500px">
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
        <el-form-item label="单价">
          <el-input v-model="form.unitPrice" type="number" />
        </el-form-item>
        <el-form-item label="备注">
          <el-textarea v-model="form.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="submitInbound">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCurrentUser } from '@/store/user'

const inboundRecords = ref([])
const products = ref([])
const warehouses = ref([])
const showAddModal = ref(false)

const form = reactive({
  productId: null,
  warehouseId: null,
  quantity: null,
  unitPrice: null,
  remark: ''
})

onMounted(async () => {
  await loadData()
})

async function loadData() {
  await Promise.all([
    loadInboundRecords(),
    loadProducts(),
    loadWarehouses()
  ])
}

async function loadInboundRecords() {
  try {
    inboundRecords.value = await api.get('/inventory/inbound')
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

async function submitInbound() {
  if (!form.productId || !form.warehouseId || !form.quantity) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const user = getCurrentUser()
  
  try {
    await api.post(`/inventory/inbound?operatorId=${user.id}`, form)
    ElMessage.success('入库申请成功')
    showAddModal.value = false
    resetForm()
    loadInboundRecords()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function confirmInbound(record) {
  ElMessageBox.confirm(`确定确认入库单号 ${record.inboundNo} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await api.post(`/inventory/inbound/${record.id}/confirm`)
      ElMessage.success('确认成功')
      loadInboundRecords()
    } catch (error) {
      ElMessage.error(error.message)
    }
  })
}

function resetForm() {
  form.productId = null
  form.warehouseId = null
  form.quantity = null
  form.unitPrice = null
  form.remark = ''
}
</script>

<style scoped>
.inbound-page {
  padding: 0;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
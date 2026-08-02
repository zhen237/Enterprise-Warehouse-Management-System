<!--
  库存查询页面组件
  功能：查看所有仓库的库存明细、按仓库过滤、显示总价
  接口：
    GET /api/inventory                    获取所有库存
    GET /api/inventory?warehouseId=X     按仓库过滤
    GET /api/inventory/{productId}/{warehouseId}  单商品单仓库库存
-->
<template>
  <div class="inventory-page">
    <el-card>
      <template #header>
        <span>库存查询</span>
      </template>
      
      <div class="filter-bar">
        <el-select v-model="selectedWarehouse" placeholder="选择仓库" style="width: 200px">
          <el-option label="全部仓库" :value="null" />
          <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
        <el-button type="primary" @click="loadInventory">查询</el-button>
      </div>
      
      <el-table :data="inventoryList" border show-summary>
        <el-table-column prop="product.productCode" label="商品编码" width="120" />
        <el-table-column prop="product.productName" label="商品名称" width="150" />
        <el-table-column prop="product.category" label="分类" width="100" />
        <el-table-column prop="warehouse.warehouseName" label="仓库" width="120" />
        <el-table-column prop="quantity" label="当前库存" width="100">
          <template #default="scope">
            <span :class="scope.row.quantity < 10 ? 'low-stock' : ''">{{ scope.row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="product.unit" label="单位" width="80" />
        <el-table-column prop="product.price" label="单价" width="100">
          <template #default="scope">
            ¥{{ formatNumber(scope.row.product.price) }}
          </template>
        </el-table-column>
        <el-table-column label="库存价值" width="120">
          <template #default="scope">
            ¥{{ formatNumber(scope.row.quantity * scope.row.product.price) }}
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="最低库存" width="100" />
        <el-table-column prop="maxStock" label="最高库存" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'

const inventoryList = ref([])
const warehouses = ref([])
const selectedWarehouse = ref(null)

onMounted(async () => {
  await loadWarehouses()
  await loadInventory()
})

function formatNumber(value) {
  if (!value && value !== 0) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function loadWarehouses() {
  try {
    warehouses.value = await api.get('/warehouses')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function loadInventory() {
  try {
    const params = selectedWarehouse.value ? `?warehouseId=${selectedWarehouse.value}` : ''
    inventoryList.value = await api.get(`/inventory${params}`)
  } catch (error) {
    ElMessage.error(error.message)
  }
}
</script>

<style scoped>
.inventory-page {
  padding: 0;
}

.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.low-stock {
  color: #f56c6c;
  font-weight: bold;
}
</style>
<template>
  <div class="reports-page">
    <el-card class="summary-card">
      <template #header>
        <span>库存报表</span>
      </template>
      <div class="summary-grid">
        <div class="summary-item">
          <p class="summary-value">{{ summary.totalProducts }}</p>
          <p class="summary-label">商品种类</p>
        </div>
        <div class="summary-item">
          <p class="summary-value">{{ summary.totalQuantity }}</p>
          <p class="summary-label">库存总量</p>
        </div>
        <div class="summary-item">
          <p class="summary-value">{{ summary.totalInbound }}</p>
          <p class="summary-label">本月入库</p>
        </div>
        <div class="summary-item">
          <p class="summary-value">{{ summary.totalOutbound }}</p>
          <p class="summary-label">本月出库</p>
        </div>
      </div>
    </el-card>
    
    <div class="charts-row">
      <el-card class="chart-card">
        <template #header>
          <span>库存分类统计</span>
        </template>
        <div ref="categoryChart" class="chart"></div>
      </el-card>
      <el-card class="chart-card">
        <template #header>
          <span>库存预警</span>
        </template>
        <div ref="warningChart" class="chart"></div>
      </el-card>
    </div>
    
    <el-card>
      <template #header>
        <span>库存明细</span>
      </template>
      <el-table :data="inventoryList" border>
        <el-table-column prop="product.productName" label="商品名称" width="180" />
        <el-table-column prop="product.category" label="分类" width="100" />
        <el-table-column prop="warehouse.warehouseName" label="仓库" width="120" />
        <el-table-column prop="quantity" label="库存数量" width="100" />
        <el-table-column prop="product.unit" label="单位" width="80" />
        <el-table-column prop="product.price" label="单价" width="100">
          <template #default="scope">¥{{ scope.row.product.price }}</template>
        </el-table-column>
        <el-table-column label="库存金额" width="120">
          <template #default="scope">¥{{ (scope.row.quantity * scope.row.product.price).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'

const categoryChart = ref(null)
const warningChart = ref(null)
const inventoryList = ref([])

const summary = reactive({
  totalProducts: 0,
  totalQuantity: 0,
  totalInbound: 0,
  totalOutbound: 0
})

onMounted(async () => {
  await loadData()
  initCharts()
})

async function loadData() {
  try {
    inventoryList.value = await api.get('/inventory')
    
    const products = await api.get('/products')
    summary.totalProducts = products.length
    
    summary.totalQuantity = inventoryList.value.reduce((sum, item) => sum + item.quantity, 0)
    
    const inboundRecords = await api.get('/inventory/inbound')
    summary.totalInbound = inboundRecords.filter(r => {
      const now = new Date()
      const recordDate = new Date(r.inboundTime)
      return recordDate.getMonth() === now.getMonth() && recordDate.getFullYear() === now.getFullYear()
    }).reduce((sum, r) => sum + r.quantity, 0)
    
    const outboundRecords = await api.get('/inventory/outbound')
    summary.totalOutbound = outboundRecords.filter(r => {
      const now = new Date()
      const recordDate = new Date(r.outboundTime)
      return recordDate.getMonth() === now.getMonth() && recordDate.getFullYear() === now.getFullYear()
    }).reduce((sum, r) => sum + r.quantity, 0)
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function initCharts() {
  if (categoryChart.value) {
    const chart = echarts.init(categoryChart.value)
    const categoryMap = {}
    inventoryList.value.forEach(item => {
      const category = item.product.category || '其他'
      categoryMap[category] = (categoryMap[category] || 0) + item.quantity
    })
    
    chart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: Object.keys(categoryMap) },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar',
        data: Object.values(categoryMap)
      }]
    })
  }
  
  if (warningChart.value) {
    const chart = echarts.init(warningChart.value)
    const lowStock = inventoryList.value.filter(item => item.quantity < 10)
    const normalStock = inventoryList.value.filter(item => item.quantity >= 10 && item.quantity <= 100)
    const highStock = inventoryList.value.filter(item => item.quantity > 100)
    
    chart.setOption({
      title: { text: '库存状态分布', left: 'center' },
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        name: '库存状态',
        type: 'pie',
        radius: '50%',
        data: [
          { value: lowStock.length, name: '库存不足(<10)', itemStyle: { color: '#ef4444' } },
          { value: normalStock.length, name: '正常(10-100)', itemStyle: { color: '#10b981' } },
          { value: highStock.length, name: '充足(>100)', itemStyle: { color: '#3b82f6' } }
        ]
      }]
    })
  }
}
</script>

<style scoped>
.reports-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.summary-card {
  margin-bottom: 0;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.summary-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.summary-value {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.summary-label {
  margin: 5px 0 0;
  color: #999;
  font-size: 14px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  height: 350px;
}

.chart {
  width: 100%;
  height: calc(100% - 48px);
}
</style>
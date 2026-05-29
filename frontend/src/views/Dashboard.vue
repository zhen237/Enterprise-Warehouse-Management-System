<template>
  <div class="dashboard">
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-icon product-icon">
          <el-icon><Briefcase /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.productCount }}</p>
          <p class="stat-label">商品数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon warehouse-icon">
          <el-icon><DataBoard /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.warehouseCount }}</p>
          <p class="stat-label">仓库数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon inbound-icon">
          <el-icon><ArrowDown /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.inboundCount }}</p>
          <p class="stat-label">今日入库</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon outbound-icon">
          <el-icon><ArrowUp /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.outboundCount }}</p>
          <p class="stat-label">今日出库</p>
        </div>
      </el-card>
    </div>
    
    <div class="charts-section">
      <el-card class="chart-card">
        <template #header>
          <span>库存概览</span>
        </template>
        <div ref="inventoryChart" class="chart"></div>
      </el-card>
      <el-card class="chart-card">
        <template #header>
          <span>出入库趋势</span>
        </template>
        <div ref="trendChart" class="chart"></div>
      </el-card>
    </div>
    
    <div class="recent-section">
      <el-card>
        <template #header>
          <span>最近操作记录</span>
        </template>
        <el-table :data="recentRecords" border>
          <el-table-column prop="type" label="类型" width="80">
            <template #default="scope">
              <span :class="scope.row.type === 'inbound' ? 'type-inbound' : 'type-outbound'">
                {{ scope.row.type === 'inbound' ? '入库' : '出库' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="商品" width="150" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="warehouse" label="仓库" width="120" />
          <el-table-column prop="time" label="时间" width="150" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Briefcase, DataBoard, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import api from '@/utils/api'

const inventoryChart = ref(null)
const trendChart = ref(null)

const stats = reactive({
  productCount: 0,
  warehouseCount: 0,
  inboundCount: 0,
  outboundCount: 0
})

const recentRecords = ref([])

onMounted(async () => {
  await loadStats()
  await loadRecentRecords()
  initCharts()
})

async function loadStats() {
  try {
    const [products, warehouses, inboundRecords, outboundRecords] = await Promise.all([
      api.get('/products'),
      api.get('/warehouses'),
      api.get('/inventory/inbound'),
      api.get('/inventory/outbound')
    ])
    stats.productCount = products.length
    stats.warehouseCount = warehouses.length
    stats.inboundCount = inboundRecords.filter(r => {
      const today = new Date().toDateString()
      return new Date(r.inboundTime).toDateString() === today
    }).length
    stats.outboundCount = outboundRecords.filter(r => {
      const today = new Date().toDateString()
      return new Date(r.outboundTime).toDateString() === today
    }).length
  } catch (error) {
    console.error(error)
  }
}

async function loadRecentRecords() {
  try {
    const [inboundRecords, outboundRecords] = await Promise.all([
      api.get('/inventory/inbound'),
      api.get('/inventory/outbound')
    ])
    
    const records = [
      ...inboundRecords.map(r => ({
        type: 'inbound',
        productName: r.product?.productName || '',
        quantity: r.quantity,
        warehouse: r.warehouse?.warehouseName || '',
        time: r.inboundTime
      })),
      ...outboundRecords.map(r => ({
        type: 'outbound',
        productName: r.product?.productName || '',
        quantity: r.quantity,
        warehouse: r.warehouse?.warehouseName || '',
        time: r.outboundTime
      }))
    ]
    
    records.sort((a, b) => new Date(b.time) - new Date(a.time))
    recentRecords.value = records.slice(0, 10)
  } catch (error) {
    console.error(error)
  }
}

function initCharts() {
  if (inventoryChart.value) {
    const chart = echarts.init(inventoryChart.value)
    chart.setOption({
      title: { text: '库存分布', left: 'center' },
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        name: '库存',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 335, name: '电子产品' },
          { value: 278, name: '办公用品' },
          { value: 189, name: '原材料' },
          { value: 156, name: '其他' }
        ]
      }]
    })
  }
  
  if (trendChart.value) {
    const chart = echarts.init(trendChart.value)
    chart.setOption({
      title: { text: '近7天出入库趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['入库', '出库'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
      yAxis: { type: 'value' },
      series: [
        { name: '入库', type: 'line', data: [120, 132, 101, 134, 190, 230, 210] },
        { name: '出库', type: 'line', data: [82, 93, 71, 94, 140, 180, 160] }
      ]
    })
  }
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.product-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.warehouse-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.inbound-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.outbound-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.stat-label {
  margin: 5px 0 0;
  color: #999;
  font-size: 14px;
}

.charts-section {
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

.recent-section {
  margin-top: auto;
}

.type-inbound {
  color: #10b981;
  background: #d1fae5;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.type-outbound {
  color: #ef4444;
  background: #fee2e2;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
</style>
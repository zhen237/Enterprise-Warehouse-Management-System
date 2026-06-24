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
          <div class="card-header">
            <span>库存分类分布</span>
          </div>
        </template>
        <div ref="inventoryChart" class="chart"></div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>出入库趋势</span>
            <el-radio-group v-model="trendRange" size="small" @change="renderTrendChart">
              <el-radio-button label="today">今日</el-radio-button>
              <el-radio-button label="7days">近7天</el-radio-button>
              <el-radio-button label="30days">近30天</el-radio-button>
            </el-radio-group>
          </div>
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
          <el-table-column prop="time" label="时间" width="180" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Briefcase, DataBoard, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import api from '@/utils/api'

const inventoryChart = ref(null)
const trendChart = ref(null)
let inventoryInstance = null
let trendInstance = null

const trendRange = ref('today')

const stats = reactive({
  productCount: 0,
  warehouseCount: 0,
  inboundCount: 0,
  outboundCount: 0
})

const recentRecords = ref([])

const inboundRecords = ref([])
const outboundRecords = ref([])

onMounted(async () => {
  await Promise.all([loadStats(), loadAllRecords(), loadRecentRecords()])
  await nextTick()
  initInventoryChart()
  renderTrendChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (inventoryInstance) inventoryInstance.dispose()
  if (trendInstance) trendInstance.dispose()
})

function handleResize() {
  inventoryInstance && inventoryInstance.resize()
  trendInstance && trendInstance.resize()
}

async function loadStats() {
  try {
    const [products, warehouses, inbound, outbound] = await Promise.all([
      api.get('/products'),
      api.get('/warehouses'),
      api.get('/inventory/inbound'),
      api.get('/inventory/outbound')
    ])
    stats.productCount = products.length
    stats.warehouseCount = warehouses.length
    const today = new Date().toDateString()
    stats.inboundCount = inbound.filter(r => new Date(r.inboundTime).toDateString() === today).length
    stats.outboundCount = outbound.filter(r => new Date(r.outboundTime).toDateString() === today).length
  } catch (error) {
    console.error(error)
  }
}

async function loadAllRecords() {
  try {
    const [inbound, outbound] = await Promise.all([
      api.get('/inventory/inbound'),
      api.get('/inventory/outbound')
    ])
    inboundRecords.value = inbound
    outboundRecords.value = outbound
  } catch (error) {
    console.error(error)
  }
}

async function loadRecentRecords() {
  try {
    const [inbound, outbound] = await Promise.all([
      api.get('/inventory/inbound'),
      api.get('/inventory/outbound')
    ])
    const records = [
      ...inbound.map(r => ({
        type: 'inbound',
        productName: r.product?.productName || '',
        quantity: r.quantity,
        warehouse: r.warehouse?.warehouseName || '',
        time: formatTime(r.inboundTime)
      })),
      ...outbound.map(r => ({
        type: 'outbound',
        productName: r.product?.productName || '',
        quantity: r.quantity,
        warehouse: r.warehouse?.warehouseName || '',
        time: formatTime(r.outboundTime)
      }))
    ]
    records.sort((a, b) => new Date(b.time) - new Date(a.time))
    recentRecords.value = records.slice(0, 10)
  } catch (error) {
    console.error(error)
  }
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function initInventoryChart() {
  if (!inventoryChart.value) return
  inventoryInstance = echarts.init(inventoryChart.value)

  const categoryMap = {}
  inboundRecords.value.forEach(r => {
    const cat = r.product?.category || '未分类'
    categoryMap[cat] = (categoryMap[cat] || 0) + (r.quantity || 0)
  })
  outboundRecords.value.forEach(r => {
    const cat = r.product?.category || '未分类'
    categoryMap[cat] = (categoryMap[cat] || 0) + (r.quantity || 0)
  })

  let pieData = Object.entries(categoryMap).map(([name, value]) => ({ name, value }))
  if (pieData.length === 0) {
    pieData = [
      { value: 335, name: '电子产品' },
      { value: 278, name: '办公用品' },
      { value: 189, name: '原材料' },
      { value: 156, name: '其他' }
    ]
  }

  inventoryInstance.setOption({
    title: { text: '库存分类分布', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, left: 'center' },
    color: ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a', '#fee140', '#30cfd0'],
    series: [{
      name: '库存数量',
      type: 'pie',
      radius: ['40%', '65%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data: pieData
    }]
  })
}

function renderTrendChart() {
  if (!trendChart.value) return
  if (!trendInstance) {
    trendInstance = echarts.init(trendChart.value)
  }

  const range = trendRange.value
  let xData = []
  let inboundData = []
  let outboundData = []
  let title = ''
  let preciseLabels = []

  const now = new Date()

  if (range === 'today') {
    title = '今日出入库趋势（按小时）'
    const todayTime = stripTime(now)
    for (let h = 0; h < 24; h++) {
      const label = `${String(h).padStart(2, '0')}:00`
      xData.push(label)
      preciseLabels.push(`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${label}`)
      inboundData.push(0)
      outboundData.push(0)
    }
    inboundRecords.value.forEach(r => {
      const d = new Date(r.inboundTime)
      if (stripTime(d) === todayTime) {
        inboundData[d.getHours()] += (r.quantity || 0)
      }
    })
    outboundRecords.value.forEach(r => {
      const d = new Date(r.outboundTime)
      if (stripTime(d) === todayTime) {
        outboundData[d.getHours()] += (r.quantity || 0)
      }
    })
  } else if (range === '7days') {
    title = '近7天出入库趋势（按天）'
    for (let i = 6; i >= 0; i--) {
      const d = new Date(now)
      d.setDate(d.getDate() - i)
      const monthDay = `${d.getMonth() + 1}/${d.getDate()}`
      xData.push(monthDay)
      preciseLabels.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`)
      inboundData.push(0)
      outboundData.push(0)
    }
    inboundRecords.value.forEach(r => {
      const d = new Date(r.inboundTime)
      const diff = Math.floor((today(now) - stripTime(d)) / 86400000)
      if (diff >= 0 && diff < 7) {
        inboundData[6 - diff] += (r.quantity || 0)
      }
    })
    outboundRecords.value.forEach(r => {
      const d = new Date(r.outboundTime)
      const diff = Math.floor((today(now) - stripTime(d)) / 86400000)
      if (diff >= 0 && diff < 7) {
        outboundData[6 - diff] += (r.quantity || 0)
      }
    })
  } else {
    title = '近30天出入库趋势（按天）'
    for (let i = 29; i >= 0; i--) {
      const d = new Date(now)
      d.setDate(d.getDate() - i)
      const monthDay = `${d.getMonth() + 1}/${d.getDate()}`
      xData.push(monthDay)
      preciseLabels.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`)
      inboundData.push(0)
      outboundData.push(0)
    }
    inboundRecords.value.forEach(r => {
      const d = new Date(r.inboundTime)
      const diff = Math.floor((today(now) - stripTime(d)) / 86400000)
      if (diff >= 0 && diff < 30) {
        inboundData[29 - diff] += (r.quantity || 0)
      }
    })
    outboundRecords.value.forEach(r => {
      const d = new Date(r.outboundTime)
      const diff = Math.floor((today(now) - stripTime(d)) / 86400000)
      if (diff >= 0 && diff < 30) {
        outboundData[29 - diff] += (r.quantity || 0)
      }
    })
  }

  trendInstance.setOption({
    title: { text: title, left: 'center', textStyle: { fontSize: 14 } },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      formatter: params => {
        const idx = params[0].dataIndex
        const precise = preciseLabels[idx]
        let html = `<div style="font-weight:bold;margin-bottom:4px">${precise}</div>`
        params.forEach(p => {
          html += `${p.marker}${p.seriesName}: <b>${p.value}</b><br/>`
        })
        return html
      }
    },
    legend: { data: ['入库', '出库'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '12%', containLabel: true },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: { interval: range === 'today' ? 2 : range === '7days' ? 0 : 2 }
    },
    yAxis: { type: 'value', name: '数量' },
    series: [
      {
        name: '入库',
        type: 'bar',
        data: inboundData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#4facfe' },
            { offset: 1, color: '#00f2fe' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(79,172,254,0.5)' } }
      },
      {
        name: '出库',
        type: 'bar',
        data: outboundData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#43e97b' },
            { offset: 1, color: '#38f9d7' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(67,233,123,0.5)' } }
      }
    ]
  }, true)
}

function stripTime(d) {
  return new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime()
}

function today(now) {
  const base = now || new Date()
  return new Date(base.getFullYear(), base.getMonth(), base.getDate()).getTime()
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  height: 380px;
}

.chart {
  width: 100%;
  height: calc(100% - 80px);
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

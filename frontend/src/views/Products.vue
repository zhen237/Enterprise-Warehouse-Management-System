<template>
  <div class="products-page">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>商品管理</span>
          <el-button type="primary" @click="showAddModal = true">添加商品</el-button>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索商品名称" class="search-input" @keyup.enter="loadProducts" />
        <el-button type="primary" @click="loadProducts">搜索</el-button>
      </div>
      
      <el-table :data="products" border>
        <el-table-column prop="productCode" label="商品编码" width="120" />
        <el-table-column prop="productName" label="商品名称" width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="supplier.supplierName" label="供应商" width="150" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="text" @click="editProduct(scope.row)">编辑</el-button>
            <el-button type="text" @click="deleteProduct(scope.row)" style="color: #f56c6c">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog :title="editForm.id ? '编辑商品' : '添加商品'" v-model="showAddModal" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="商品编码">
          <el-input v-model="editForm.productCode" :disabled="!!editForm.id" />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="editForm.productName" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="editForm.category" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="editForm.unit" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model="editForm.price" type="number" />
        </el-form-item>
        <el-form-item label="描述">
          <el-textarea v-model="editForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const products = ref([])
const searchKeyword = ref('')
const showAddModal = ref(false)

const editForm = reactive({
  id: null,
  productCode: '',
  productName: '',
  category: '',
  unit: '',
  price: null,
  description: ''
})

onMounted(() => {
  loadProducts()
})

async function loadProducts() {
  try {
    if (searchKeyword.value) {
      products.value = await api.get(`/products/search?name=${searchKeyword.value}`)
    } else {
      products.value = await api.get('/products')
    }
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function editProduct(product) {
  editForm.id = product.id
  editForm.productCode = product.productCode
  editForm.productName = product.productName
  editForm.category = product.category
  editForm.unit = product.unit
  editForm.price = product.price
  editForm.description = product.description
  showAddModal.value = true
}

function deleteProduct(product) {
  ElMessageBox.confirm(`确定删除商品 ${product.productName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await api.delete(`/products/${product.id}`)
      ElMessage.success('删除成功')
      loadProducts()
    } catch (error) {
      ElMessage.error(error.message)
    }
  }).catch(() => {})
}

async function saveProduct() {
  if (!editForm.productCode || !editForm.productName) {
    ElMessage.warning('请填写商品编码和名称')
    return
  }
  
  try {
    if (editForm.id) {
      await api.put(`/products/${editForm.id}`, editForm)
      ElMessage.success('更新成功')
    } else {
      await api.post('/products', editForm)
      ElMessage.success('添加成功')
    }
    showAddModal.value = false
    resetForm()
    loadProducts()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function resetForm() {
  editForm.id = null
  editForm.productCode = ''
  editForm.productName = ''
  editForm.category = ''
  editForm.unit = ''
  editForm.price = null
  editForm.description = ''
}
</script>

<style scoped>
.products-page {
  padding: 0;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}
</style>
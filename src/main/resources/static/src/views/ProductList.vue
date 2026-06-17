<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">商品列表</h1>
      <router-link to="/product/add" class="add-btn">
        <span class="btn-icon">➕</span>
        <span>新增商品</span>
      </router-link>
    </div>
    
    <div class="filter-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="商品名称">
          <el-input 
            v-model="searchForm.productName" 
            placeholder="请输入商品名称"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select 
            v-model="searchForm.categoryId" 
            placeholder="请选择分类"
            class="search-select"
            filterable
          >
            <el-option label="全部" :value="''" />
            <el-option 
              v-for="cat in categories" 
              :key="cat.id" 
              :label="cat.categoryName" 
              :value="cat.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            🔍 搜索
          </el-button>
          <el-button @click="handleReset" class="reset-btn">
            🔄 重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="table-card">
      <el-table 
        :data="tableData" 
        border 
        stripe 
        :loading="loading"
        class="product-table"
        @row-click="handleRowClick"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="price" label="价格" width="120" align="center">
          <template #default="scope">
            <span class="price">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center" />
        <el-table-column prop="categoryName" label="分类" width="120" align="center" />
        <el-table-column prop="origin" label="产地" width="120" align="center" />
        <el-table-column prop="creator" label="创建人" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click.stop="handleEdit(scope.row)"
              class="edit-btn"
            >
              ✏️ 编辑
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click.stop="handleDelete(scope.row)"
              class="delete-btn"
            >
              🗑️ 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          class="pagination"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, deleteProduct } from '../api/product'
import { getCategoryList } from '../api/category'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const categories = ref([])

const searchForm = ref({
  productName: '',
  categoryId: ''
})

const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => {
  loadCategories()
  loadProductList()
})

async function loadCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

async function loadProductList() {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      productName: searchForm.value.productName,
      categoryId: searchForm.value.categoryId
    }
    const res = await getProductList(params)
    tableData.value = res.data.list || []
    pagination.value.total = res.data.total || 0
  } catch (error) {
    console.error('加载商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.value.pageNum = 1
  loadProductList()
}

function handleReset() {
  searchForm.value = {
    productName: '',
    categoryId: ''
  }
  pagination.value.pageNum = 1
  loadProductList()
}

function handleSizeChange(val) {
  pagination.value.pageSize = val
  loadProductList()
}

function handleCurrentChange(val) {
  pagination.value.pageNum = val
  loadProductList()
}

function handleRowClick(row) {
  router.push(`/product/edit/${row.id}`)
}

function handleEdit(row) {
  router.push(`/product/edit/${row.id}`)
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品「${row.productName}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadProductList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.add-btn {
  display: inline-flex;
  align-items: center;
  background: #27ae60;
  color: #fff;
  padding: 10px 20px;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.add-btn:hover {
  background: #2ecc71;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
}

.btn-icon {
  margin-right: 6px;
}

.filter-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.search-input {
  width: 200px;
}

.search-select {
  width: 180px;
}

.search-btn, .reset-btn {
  padding: 8px 20px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.reset-btn:hover {
  background: #f5f5f5;
}

.table-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.product-table {
  width: 100%;
}

.product-table :deep(.el-table__row:hover) {
  background: #f8f9fa;
}

.price {
  color: #e74c3c;
  font-weight: 600;
}

.edit-btn, .delete-btn {
  margin-right: 8px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pagination {
  display: flex;
  align-items: center;
}

@media screen and (max-width: 768px) {
  .search-form {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input, .search-select {
    width: 100%;
    max-width: 300px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>
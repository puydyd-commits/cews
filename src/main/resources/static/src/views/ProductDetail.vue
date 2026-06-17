<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">商品详情</h1>
      <div class="header-actions">
        <router-link :to="`/product/edit/${product.id}`" class="edit-btn">
          ✏️ 编辑
        </router-link>
        <router-link to="/product/list" class="back-btn">
          ← 返回列表
        </router-link>
      </div>
    </div>
    
    <div class="detail-card" v-if="product">
      <div class="detail-header">
        <div class="product-image" v-if="product.imageUrl">
          <img :src="product.imageUrl" alt="商品图片" />
        </div>
        <div class="product-info">
          <h2 class="product-name">{{ product.productName }}</h2>
          <div class="product-price">¥{{ product.price }}</div>
          <div class="product-meta">
            <span class="meta-item">分类：{{ product.categoryName }}</span>
            <span class="meta-item">库存：{{ product.stock }}</span>
            <span class="meta-item">产地：{{ product.origin || '未填写' }}</span>
          </div>
        </div>
      </div>
      
      <div class="detail-content">
        <div class="detail-section">
          <h3 class="section-title">📋 基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">商品ID</span>
              <span class="info-value">{{ product.id }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">商品名称</span>
              <span class="info-value">{{ product.productName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">商品分类</span>
              <span class="info-value">{{ product.categoryName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">商品价格</span>
              <span class="info-value">¥{{ product.price }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">商品库存</span>
              <span class="info-value">{{ product.stock }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">生产产地</span>
              <span class="info-value">{{ product.origin || '未填写' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建人</span>
              <span class="info-value">{{ product.creator }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ formatTime(product.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">更新人</span>
              <span class="info-value">{{ product.updater }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">更新时间</span>
              <span class="info-value">{{ formatTime(product.updateTime) }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section" v-if="product.imageUrl">
          <h3 class="section-title">🖼️ 商品图片</h3>
          <div class="image-preview">
            <img :src="product.imageUrl" alt="商品图片" />
          </div>
        </div>
      </div>
      
      <div class="detail-footer">
        <router-link :to="`/product/edit/${product.id}`" class="btn btn-primary">
          ✏️ 编辑商品
        </router-link>
        <router-link to="/product/list" class="btn btn-secondary">
          ← 返回列表
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductById } from '../api/product'
import { useRoute } from 'vue-router'

const route = useRoute()
const product = ref(null)

onMounted(() => {
  loadProduct()
})

async function loadProduct() {
  const id = route.params.id
  if (!id) return
  
  try {
    const res = await getProductById(id)
    product.value = res.data
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  }
}

function formatTime(timeStr) {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
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

.header-actions {
  display: flex;
  gap: 10px;
}

.edit-btn, .back-btn {
  padding: 8px 16px;
  border-radius: 6px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.edit-btn {
  background: #3498db;
  color: #fff;
}

.edit-btn:hover {
  background: #2980b9;
}

.back-btn {
  color: #3498db;
  border: 1px solid #3498db;
}

.back-btn:hover {
  background: rgba(52, 152, 219, 0.1);
}

.detail-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.detail-header {
  display: flex;
  gap: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.product-image {
  width: 200px;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-name {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.product-price {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 15px 0;
}

.product-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  font-size: 14px;
  opacity: 0.9;
}

.detail-content {
  padding: 30px;
}

.detail-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.info-label {
  color: #666;
  font-weight: 500;
}

.info-value {
  color: #333;
  font-weight: 600;
}

.image-preview {
  max-width: 400px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-preview img {
  width: 100%;
  height: auto;
}

.detail-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 30px;
  border-top: 1px solid #f0f0f0;
}

.btn {
  padding: 12px 30px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: #fff;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #eee;
}

@media screen and (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .product-image {
    width: 150px;
    height: 150px;
  }
  
  .product-meta {
    justify-content: center;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-footer {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
    text-align: center;
  }
}
</style>
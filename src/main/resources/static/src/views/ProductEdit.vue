<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">编辑商品</h1>
      <router-link to="/product/list" class="back-btn">
        ← 返回列表
      </router-link>
    </div>
    
    <div class="form-card">
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef"
        class="product-form"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input 
                v-model="form.productName" 
                placeholder="请输入商品名称"
                class="form-input"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select 
                v-model="form.categoryId" 
                placeholder="请选择商品分类"
                class="form-select"
                filterable
              >
                <el-option 
                  v-for="cat in categories" 
                  :key="cat.id" 
                  :label="cat.categoryName" 
                  :value="cat.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品价格" prop="price">
              <el-input 
                v-model="form.price" 
                placeholder="请输入商品价格"
                class="form-input"
                type="number"
                :min="0"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品库存" prop="stock">
              <el-input 
                v-model="form.stock" 
                placeholder="请输入商品库存"
                class="form-input"
                type="number"
                :min="0"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品图片">
              <div class="upload-container">
                <el-upload
                  class="image-upload"
                  action="/api/upload"
                  :show-file-list="false"
                  :on-success="handleImageUpload"
                  :before-upload="beforeImageUpload"
                >
                  <img v-if="form.imageUrl" :src="form.imageUrl" class="uploaded-image" />
                  <div v-else class="upload-placeholder">
                    <span class="upload-icon">📷</span>
                    <span class="upload-text">点击上传图片</span>
                  </div>
                </el-upload>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产产地">
              <el-input 
                v-model="form.origin" 
                placeholder="请输入生产产地（可选）"
                class="form-input"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item class="form-actions">
          <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn">
            ✓ 保存
          </el-button>
          <el-button @click="handleReset" class="reset-btn">
            🔄 重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductById, updateProduct } from '../api/product'
import { getCategoryList } from '../api/category'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const formRef = ref(null)
const categories = ref([])
const originalForm = reactive({})

const form = reactive({
  id: '',
  productName: '',
  price: '',
  stock: '',
  categoryId: '',
  imageUrl: '',
  origin: ''
})

const rules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 100, message: '商品名称长度在1到100个字符之间', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入商品库存', trigger: 'blur' },
    { type: 'integer', min: 0, message: '库存不能为负数', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ]
}

onMounted(() => {
  loadCategories()
  loadProduct()
})

async function loadCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

async function loadProduct() {
  const id = route.params.id
  if (!id) return
  
  try {
    const res = await getProductById(id)
    const data = res.data
    
    form.id = data.id
    form.productName = data.productName
    form.price = data.price.toString()
    form.stock = data.stock.toString()
    form.categoryId = data.categoryId
    form.imageUrl = data.imageUrl
    form.origin = data.origin || ''
    
    Object.assign(originalForm, form)
  } catch (error) {
    console.error('加载商品信息失败:', error)
    ElMessage.error('加载商品信息失败')
  }
}

function handleImageUpload(response) {
  form.imageUrl = response.data.url
  ElMessage.success('图片上传成功')
}

function beforeImageUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请上传图片格式的文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

async function handleSubmit() {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    loading.value = true
    
    const submitData = {
      productName: form.productName,
      price: parseFloat(form.price),
      stock: parseInt(form.stock),
      categoryId: form.categoryId,
      imageUrl: form.imageUrl,
      origin: form.origin
    }
    
    await updateProduct(form.id, submitData)
    ElMessage.success('商品更新成功')
    router.push('/product/list')
  } catch (error) {
    console.error('更新商品失败:', error)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, originalForm)
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

.back-btn {
  color: #3498db;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(52, 152, 219, 0.1);
}

.form-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  max-width: 800px;
}

.product-form {
  width: 100%;
}

.form-input, .form-select {
  width: 100%;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.form-input:focus, .form-select:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.upload-container {
  width: 100%;
}

.image-upload {
  width: 100%;
}

.uploaded-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.uploaded-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 150px;
  height: 150px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-placeholder:hover {
  border-color: #3498db;
  background: rgba(52, 152, 219, 0.05);
}

.upload-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #999;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.submit-btn, .reset-btn {
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.submit-btn {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border: none;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.reset-btn:hover {
  background: #f5f5f5;
}

@media screen and (max-width: 768px) {
  .form-card {
    padding: 20px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .submit-btn, .reset-btn {
    width: 100%;
  }
}
</style>
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

const errorCodeMap = {
  400: '请求参数错误',
  401: '未登录或登录已过期，请重新登录',
  403: '没有权限访问此资源',
  404: '资源未找到',
  500: '服务器内部错误',
  502: '网关错误',
  503: '服务不可用',
  504: '网关超时'
}

let loadingInstance = null

export function showLoading(message = '加载中...') {
  loadingInstance = ElLoading.service({
    lock: true,
    text: message,
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)'
  })
}

export function hideLoading() {
  if (loadingInstance) {
    loadingInstance.close()
    loadingInstance = null
  }
}

export function handleError(error) {
  console.error('全局错误捕获:', error)
  
  hideLoading()
  
  if (error.response) {
    const { status, data } = error.response
    
    if (data && data.message) {
      ElMessage({
        type: 'error',
        message: data.message,
        duration: 3000
      })
    } else if (errorCodeMap[status]) {
      ElMessage({
        type: 'error',
        message: errorCodeMap[status],
        duration: 3000
      })
    } else {
      ElMessage({
        type: 'error',
        message: `请求失败，状态码: ${status}`,
        duration: 3000
      })
    }
    
    if (status === 401) {
      setTimeout(() => {
        window.location.href = '/login'
      }, 1500)
    }
  } else if (error.request) {
    ElMessage({
      type: 'error',
      message: '网络请求超时或失败，请检查网络连接',
      duration: 3000
    })
  } else {
    ElMessage({
      type: 'error',
      message: error.message || '请求失败',
      duration: 3000
    })
  }
}

export function showSuccess(message) {
  ElMessage({
    type: 'success',
    message,
    duration: 2000
  })
}

export function showWarning(message) {
  ElMessage({
    type: 'warning',
    message,
    duration: 2000
  })
}

export function showError(message) {
  ElMessage({
    type: 'error',
    message,
    duration: 3000
  })
}

export function showInfo(message) {
  ElMessage({
    type: 'info',
    message,
    duration: 2000
  })
}

export function showConfirm(message, title = '提示', type = 'warning') {
  return ElMessageBox.confirm(
    message,
    title,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type
    }
  )
}

export function showAlert(message, title = '提示', type = 'info') {
  return ElMessageBox.alert(message, title, {
    confirmButtonText: '确定',
    type
  })
}
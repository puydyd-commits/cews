import { ElMessage, ElMessageBox } from 'element-plus'

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

export function handleError(error) {
  console.error('全局错误捕获:', error)
  
  if (error.response) {
    const { status, data } = error.response
    
    if (data && data.message) {
      ElMessage.error(data.message)
    } else if (errorCodeMap[status]) {
      ElMessage.error(errorCodeMap[status])
    } else {
      ElMessage.error(`请求失败，状态码: ${status}`)
    }
    
    if (status === 401) {
      setTimeout(() => {
        window.location.href = '/login'
      }, 1500)
    }
  } else if (error.request) {
    ElMessage.error('网络请求超时或失败，请检查网络连接')
  } else {
    ElMessage.error(error.message || '请求失败')
  }
}

export function showSuccess(message) {
  ElMessage.success(message)
}

export function showWarning(message) {
  ElMessage.warning(message)
}

export function showError(message) {
  ElMessage.error(message)
}

export function showConfirm(message, title = '提示') {
  return ElMessageBox.confirm(
    message,
    title,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
}
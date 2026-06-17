import axios from 'axios'
import { handleError } from './errorHandler'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      handleError({
        response: {
          status: res.code,
          data: res
        }
      })
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    handleError(error)
    return Promise.reject(error)
  }
)

export default service
// API基础配置和通用方法
const API_BASE_URL = 'http://localhost:8080/api'

class ApiService {
  constructor() {
    this.baseURL = API_BASE_URL
  }

  // 获取认证头
  getAuthHeaders() {
    const token = localStorage.getItem('token')
    return token ? { 'Authorization': `Bearer ${token}` } : {}
  }

  // 通用请求方法
  async request(url, options = {}) {
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...this.getAuthHeaders(),
        ...options.headers
      },
      ...options
    }

    try {
      const response = await fetch(`${this.baseURL}${url}`, config)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || `HTTP error! status: ${response.status}`)
      }

      return data
    } catch (error) {
      console.error('API请求失败:', error)
      throw error
    }
  }

  // GET请求
  async get(url, params = {}) {
    const queryString = new URLSearchParams(params).toString()
    const fullUrl = queryString ? `${url}?${queryString}` : url
    return this.request(fullUrl, { method: 'GET' })
  }

  // POST请求
  async post(url, data = {}) {
    return this.request(url, {
      method: 'POST',
      body: JSON.stringify(data)
    })
  }

  // PUT请求
  async put(url, data = {}, params = {}) {
    const queryString = new URLSearchParams(params).toString()
    const fullUrl = queryString ? `${url}?${queryString}` : url

    return this.request(fullUrl, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined
    })
  }

  // DELETE请求
  async delete(url) {
    return this.request(url, { method: 'DELETE' })
  }
}

export default new ApiService()

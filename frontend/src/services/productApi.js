import apiService from './api.js'

// 产品相关API
class ProductApiService {
  // 获取产品详情
  async getProduct(productId) {
    return apiService.get(`/products/${productId}`)
  }

  // 获取分类列表
  async getCategories() {
    return apiService.get('/categories')
  }

  // 获取产品列表
  async getProducts(params = {}) {
    return apiService.get('/products', params)
  }
}

export default new ProductApiService()
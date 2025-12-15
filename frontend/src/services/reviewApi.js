import apiService from './api.js'

// 产品评价相关API
class ReviewApiService {
  // 获取产品评价列表
  async getProductReviews(productId, params = {}) {
    const defaultParams = {
      page: 0,
      size: 10,
      sortBy: 'dateCreated',
      sortDir: 'desc'
    }
    return apiService.get(`/products/${productId}/reviews`, { ...defaultParams, ...params })
  }

  // 获取产品评价汇总
  async getProductReviewSummary(productId) {
    return apiService.get(`/products/${productId}/reviews/summary`)
  }

  // 创建产品评价
  async createReview(productId, reviewData) {
    return apiService.post(`/products/${productId}/reviews`, {
      productId,
      ...reviewData
    })
  }

  // 获取我的评价
  async getMyReviews(params = {}) {
    const defaultParams = {
      page: 0,
      size: 10
    }
    return apiService.get('/products/reviews/my', { ...defaultParams, ...params })
  }

  // 删除评价
  async deleteReview(reviewId) {
    return apiService.delete(`/products/reviews/${reviewId}`)
  }
}

export default new ReviewApiService()
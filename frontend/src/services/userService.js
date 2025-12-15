import apiService from './api.js'

// 用户相关API服务
class UserService {
  // 获取当前用户信息
  async getCurrentUser() {
    return apiService.get('/user/profile')
  }

  // 获取用户审批状态
  async getApprovalStatus() {
    return apiService.get('/user/approval-status')
  }

  // 更新用户个人信息
  async updateProfile(profileData) {
    return apiService.put('/user/profile', profileData)
  }

  // 更新手机号
  async updatePhone(phone) {
    return apiService.put('/user/phone', null, { phone })
  }

  // 获取用户地址列表
  async getUserAddresses() {
    return apiService.get('/user/addresses')
  }

  // 更新送货地址
  async updateShippingAddress(addressData) {
    return apiService.put('/user/addresses/shipping', addressData)
  }

  // 更新账单地址
  async updateBillingAddress(addressData) {
    return apiService.put('/user/addresses/billing', addressData)
  }

  // 删除地址
  async deleteAddress(addressId) {
    return apiService.delete(`/user/addresses/${addressId}`)
  }

  // 获取用户订单列表
  async getUserOrders(params = {}) {
    const defaultParams = {
      page: 0,
      size: 10,
      sortBy: 'dateCreated',
      sortDir: 'desc'
    }
    return apiService.get('/orders', { ...defaultParams, ...params })
  }

  // 根据订单ID获取订单详情
  async getOrderById(orderId) {
    return apiService.get(`/orders/${orderId}`)
  }

  // 根据状态获取用户订单
  async getUserOrdersByStatus(status, params = {}) {
    const defaultParams = {
      page: 0,
      size: 10,
      sortBy: 'dateCreated',
      sortDir: 'desc'
    }
    return apiService.get(`/orders/status/${status}`, { ...defaultParams, ...params })
  }
}

export default new UserService()
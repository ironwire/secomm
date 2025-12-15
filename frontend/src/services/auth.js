// 认证相关服务
class AuthService {
  constructor() {
    this.tokenKey = 'token'
    this.userKey = 'user'
  }

  // 获取token
  getToken() {
    return localStorage.getItem(this.tokenKey)
  }

  // 设置token
  setToken(token) {
    localStorage.setItem(this.tokenKey, token)
  }

  // 移除token
  removeToken() {
    localStorage.removeItem(this.tokenKey)
  }

  // 获取用户信息
  getUser() {
    const userStr = localStorage.getItem(this.userKey)
    return userStr ? JSON.parse(userStr) : null
  }

  // 设置用户信息
  setUser(user) {
    localStorage.setItem(this.userKey, JSON.stringify(user))
  }

  // 移除用户信息
  removeUser() {
    localStorage.removeItem(this.userKey)
  }

  // 检查是否已登录
  isAuthenticated() {
    return !!this.getToken()
  }

  // 登出
  logout() {
    this.removeToken()
    this.removeUser()
  }

  // 检查token是否过期（简单实现）
  isTokenExpired() {
    const token = this.getToken()
    if (!token) return true

    try {
      // 解析JWT token的payload部分
      const payload = JSON.parse(atob(token.split('.')[1]))
      const currentTime = Date.now() / 1000
      return payload.exp < currentTime
    } catch (error) {
      console.error('Token解析失败:', error)
      return true
    }
  }

  // 获取当前用户ID
  getCurrentUserId() {
    const user = this.getUser()
    return user ? user.id : null
  }
}

export default new AuthService()
import { ref, computed } from 'vue'

// Global cart state
const cartItems = ref([])
const cartTotal = ref(0)
const cartItemCount = ref(0)

export function useCart() {
  // Fetch cart data from API
  const fetchCart = async () => {
    const token = localStorage.getItem('token')
    if (!token) return

    try {
      const response = await fetch('http://localhost:8080/api/cart/items', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      const data = await response.json()
      if (data.status === 200) {
        cartItems.value = data.data
        updateCartTotals()
      }
    } catch (error) {
      console.error('获取购物车失败:', error)
    }
  }

  // Add item to cart
  const addToCart = async (productId, quantity = 1) => {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('请先登录')
      return false
    }

    try {
      const response = await fetch('http://localhost:8080/api/cart/items', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          productId,
          quantity
        })
      })

      const data = await response.json()
      if (data.status === 200) {
        // Refresh cart data
        await fetchCart()
        return true
      } else {
        alert(data.message || '添加失败')
        return false
      }
    } catch (error) {
      console.error('添加到购物车失败:', error)
      alert('添加失败，请稍后重试')
      return false
    }
  }

  // Update cart item quantity
  const updateCartItem = async (cartItemId, quantity) => {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('请先登录')
      return false
    }

    console.log('Updating cart item:', cartItemId, 'to quantity:', quantity) // Debug log

    try {
      const response = await fetch(`http://localhost:8080/api/cart/items/${cartItemId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ quantity: parseInt(quantity) })
      })

      const data = await response.json()
      console.log('Update response:', data) // Debug log

      if (data.status === 200) {
        await fetchCart()
        return true
      } else {
        console.error('Update failed:', data.message)
        alert(data.message || '更新失败')
        return false
      }
    } catch (error) {
      console.error('更新购物车失败:', error)
      alert('更新失败，请稍后重试')
      return false
    }
  }

  // Remove item from cart
  const removeFromCart = async (cartItemId) => {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('请先登录')
      return false
    }

    console.log('Removing cart item:', cartItemId) // Debug log

    try {
      const response = await fetch(`http://localhost:8080/api/cart/items/${cartItemId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      const data = await response.json()
      console.log('Remove response:', data) // Debug log

      if (data.status === 200) {
        await fetchCart()
        return true
      } else {
        console.error('Remove failed:', data.message)
        alert(data.message || '移除失败')
        return false
      }
    } catch (error) {
      console.error('移除购物车商品失败:', error)
      alert('移除失败，请稍后重试')
      return false
    }
  }

  // Update cart totals
  const updateCartTotals = () => {
    cartItemCount.value = cartItems.value.reduce((total, item) => total + item.quantity, 0)
    cartTotal.value = cartItems.value.reduce((total, item) => {
      return total + (item.unitPrice * item.quantity)
    }, 0)
  }

  // Initialize cart on first load
  const initCart = () => {
    fetchCart()
  }

  return {
    cartItems,
    cartTotal: computed(() => cartTotal.value.toFixed(2)),
    cartItemCount,
    addToCart,
    updateCartItem,
    removeFromCart,
    fetchCart,
    initCart
  }
}

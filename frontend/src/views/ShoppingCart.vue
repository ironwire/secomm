<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <AppHeader :showSearch="false" />

    <div class="flex flex-1 relative">
      <AppSidebar
        :categories="categories"
        :selectedCategoryId="null"
        @selectCategory="handleSelectCategory"
        @showAllProducts="handleShowAllProducts"
      />

      <main class="flex-1 p-8">
        <div class="max-w-3xl">
          <h2 class="text-2xl font-bold mb-6">购物车详细</h2>

          <div v-if="loading" class="text-center py-8">
            <p>加载中...</p>
          </div>

          <div v-else-if="cartItems.length === 0" class="text-center py-8">
            <p class="text-gray-600 mb-4">购物车为空</p>
            <router-link
              to="/"
              class="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
              继续购物
            </router-link>
          </div>

          <div v-else>
            <!-- Cart Items -->
            <div class="space-y-4 mb-8">
              <div
                v-for="item in cartItems"
                :key="item.id"
                class="bg-white border border-gray-200 rounded p-4"
              >
                <div class="flex gap-4">
                  <div class="w-20 h-20 bg-gray-100 flex-shrink-0 flex items-center justify-center overflow-hidden">
                    <img
                      v-if="item.productImageUrl"
                      :src="item.productImageUrl"
                      :alt="item.productName"
                      class="w-full h-full object-cover"
                      @error="handleImageError"
                    />
                    <svg
                      v-else
                      class="w-10 h-10 text-gray-300"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 002 2z"
                      />
                    </svg>
                  </div>
                  <div class="flex-1">
                    <h3 class="font-semibold">{{ item.productName || '产品名称' }}</h3>
                    <p class="text-gray-600 text-sm">{{ item.productDescription || '产品描述' }}</p>
                    <p class="font-bold mt-1">￥{{ item.unitPrice }}</p>
                  </div>
                  <div class="flex flex-col items-end gap-2">
                    <div class="flex items-center gap-2">
                      <span class="text-sm">数量</span>
                      <select
                        :value="item.quantity"
                        @change="updateQuantity(item.id, $event.target.value)"
                        class="border border-gray-300 rounded px-2 py-1"
                      >
                        <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
                      </select>
                    </div>
                    <p class="text-sm">
                      小计 <span class="font-semibold">￥{{ (item.unitPrice * item.quantity).toFixed(2) }}</span>
                    </p>
                    <button
                      @click="removeItem(item.id)"
                      class="px-4 py-1 border border-gray-900 rounded hover:bg-gray-100 text-sm"
                    >
                      移除
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单总结 -->
            <div class="bg-white border border-gray-200 rounded p-6">
              <div class="border-t border-gray-300 pt-4 space-y-3">
                <div class="flex justify-between">
                  <span class="font-semibold">总数量:</span>
                  <span class="font-semibold">{{ totalQuantity }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="font-semibold">运费:</span>
                  <span class="font-semibold">免费</span>
                </div>
                <div class="flex justify-between text-xl">
                  <span class="font-bold">总价格:</span>
                  <span class="font-bold">￥{{ totalPrice }}</span>
                </div>
                <button
                  @click="handleCheckout"
                  class="w-full mt-4 px-6 py-3 bg-gray-900 text-white rounded hover:bg-gray-800 font-semibold"
                >
                  结算
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCart } from '../composables/useCart.js'
import AppHeader from '../components/AppHeader.vue'
import AppSidebar from '../components/AppSidebar.vue'
import AppFooter from '../components/AppFooter.vue'

const router = useRouter()
const { cartItems, fetchCart, updateCartItem, removeFromCart } = useCart()

const categories = ref([])
const loading = ref(false)

// 计算总数量和总价格
const totalQuantity = computed(() => {
  return cartItems.value.reduce((total, item) => total + item.quantity, 0)
})

const totalPrice = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return total + (item.unitPrice * item.quantity)
  }, 0).toFixed(2)
})

// 获取分类数据
const fetchCategories = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/categories')
    const data = await response.json()
    if (data.status === 200) {
      categories.value = data.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 更新商品数量 - 使用 composable 函数
const updateQuantity = async (cartItemId, newQuantity) => {
  console.log('Updating quantity:', cartItemId, newQuantity) // Debug log
  const success = await updateCartItem(cartItemId, parseInt(newQuantity))
  if (success) {
    console.log('Quantity updated successfully')
  } else {
    alert('更新数量失败')
  }
}

// 移除商品 - 使用 composable 函数
const removeItem = async (cartItemId) => {
  console.log('Removing item:', cartItemId) // Debug log

  if (!confirm('确定要移除这个商品吗？')) {
    return
  }

  const success = await removeFromCart(cartItemId)
  if (success) {
    alert('商品已移除')
  } else {
    alert('移除失败')
  }
}

// 处理分类选择
const handleSelectCategory = (categoryId) => {
  router.push({ name: 'home', query: { category: categoryId } })
}

// 处理显示所有产品
const handleShowAllProducts = () => {
  router.push({ name: 'home' })
}

// 处理图片加载错误
const handleImageError = (event) => {
  console.log('图片加载失败:', event.target.src)
  event.target.style.display = 'none'
}

// 处理结算
const handleCheckout = async () => {
  if (cartItems.value.length === 0) {
    alert('购物车为空，无法结算')
    return
  }

  if (!confirm(`确认结算？总金额：￥${totalPrice.value}`)) {
    return
  }

  try {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('请先登录')
      return
    }

    const response = await fetch('http://localhost:8080/api/orders/checkout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        totalAmount: parseFloat(totalPrice.value),
        items: cartItems.value.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice
        }))
      })
    })

    const data = await response.json()

    if (data.status === 200) {
      alert('结算成功！订单已创建')
      // 清空购物车并重新获取
      await fetchCart()
      // 可以跳转到订单页面
      // router.push('/orders')
    } else {
      alert(data.message || '结算失败')
    }
  } catch (error) {
    console.error('结算失败:', error)
    alert('结算失败，请稍后重试')
  }
}

onMounted(async () => {
  loading.value = true
  await fetchCategories()
  await fetchCart()
  console.log('Cart items loaded:', cartItems.value) // Debug log
  loading.value = false
})
</script>

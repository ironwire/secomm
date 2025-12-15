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
        <div v-if="loading" class="text-center py-8">
          <p>加载中...</p>
        </div>

        <div v-else-if="product" class="max-w-4xl">
          <div class="bg-white border border-gray-200 rounded p-6 mb-6">
            <div class="flex gap-8">
              <div class="w-64 h-64 bg-gray-100 flex-shrink-0 flex items-center justify-center overflow-hidden">
                <img
                  v-if="product.imageUrl"
                  :src="getImageUrl(product.imageUrl)"
                  :alt="product.name"
                  class="w-full h-full object-cover"
                  @error="handleImageError"
                />
                <svg
                  v-else
                  class="w-32 h-32 text-gray-300"
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
                <h2 class="text-2xl font-bold mb-2">{{ product.name }}</h2>
                <p class="text-gray-600 mb-4">{{ product.description }}</p>
                <p class="text-3xl font-bold mb-6">￥{{ product.unitPrice }}</p>
                <div class="mb-4">
                  <p class="text-sm text-gray-500">SKU: {{ product.sku }}</p>
                  <p class="text-sm text-gray-500">库存: {{ product.unitsInStock }}</p>
                </div>
                <button
                  @click="addToCart"
                  :disabled="product.unitsInStock === 0"
                  :class="[
                    'px-8 py-3 rounded font-semibold transition-colors',
                    product.unitsInStock > 0
                      ? 'border border-gray-900 hover:bg-gray-100'
                      : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  ]"
                >
                  {{ product.unitsInStock > 0 ? '加入购物车' : '缺货' }}
                </button>
              </div>
            </div>
          </div>

          <div class="bg-white border border-gray-200 rounded p-6">
            <h3 class="text-xl font-semibold mb-4">产品描述</h3>
            <div class="space-y-4 text-gray-700 leading-relaxed">
              <p>{{ product.description }}</p>
            </div>
          </div>

          <!-- 产品评价部分 -->
          <div class="bg-white border border-gray-200 rounded p-6 mt-6">
            <h3 class="text-xl font-semibold mb-4">客户评价</h3>

            <!-- 评价统计 -->
            <div class="mb-6 p-4 bg-gray-50 rounded">
              <div class="flex items-center gap-4">
                <div class="text-center">
                  <div class="text-3xl font-bold">{{ averageRating.toFixed(1) }}</div>
                  <div class="flex justify-center mb-1">
                    <div class="flex">
                      <span v-for="i in 5" :key="i"
                            :class="i <= Math.round(averageRating) ? 'text-yellow-400' : 'text-gray-300'"
                            class="text-lg">★</span>
                    </div>
                  </div>
                  <div class="text-sm text-gray-600">{{ reviews.length }} 条评价</div>
                </div>
                <div class="flex-1">
                  <div v-for="rating in [5,4,3,2,1]" :key="rating" class="flex items-center gap-2 mb-1">
                    <span class="text-sm w-8">{{ rating }}星</span>
                    <div class="flex-1 bg-gray-200 rounded-full h-2">
                      <div class="bg-yellow-400 h-2 rounded-full"
                           :style="{ width: getRatingPercentage(rating) + '%' }"></div>
                    </div>
                    <span class="text-sm text-gray-600 w-8">{{ getRatingCount(rating) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 写评价 -->
            <div class="mb-6 p-4 border border-gray-200 rounded">
              <h4 class="font-semibold mb-3">写评价</h4>
              <div class="space-y-4">
                <div>
                  <label class="block text-sm font-medium mb-2">评分</label>
                  <div class="flex gap-1">
                    <button v-for="i in 5" :key="i"
                            @click="newReview.rating = i"
                            :class="i <= newReview.rating ? 'text-yellow-400' : 'text-gray-300'"
                            class="text-2xl hover:text-yellow-400 transition-colors">
                      ★
                    </button>
                  </div>
                </div>
                <div>
                  <label class="block text-sm font-medium mb-2">评价标题</label>
                  <input v-model="newReview.title"
                         type="text"
                         placeholder="请输入评价标题"
                         class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500">
                </div>
                <div>
                  <label class="block text-sm font-medium mb-2">评价内容</label>
                  <textarea v-model="newReview.content"
                            rows="4"
                            placeholder="请分享您的使用体验..."
                            class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500"></textarea>
                </div>
                <button @click="submitReview"
                        :disabled="!newReview.rating || !newReview.content"
                        :class="[
                          'px-6 py-2 rounded font-semibold transition-colors',
                          newReview.rating && newReview.content
                            ? 'bg-blue-600 text-white hover:bg-blue-700'
                            : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                        ]">
                  提交评价
                </button>
              </div>
            </div>

            <!-- 历史评价列表 -->
            <div class="space-y-4">
              <div v-if="reviews.length === 0" class="text-center py-8 text-gray-500">
                暂无评价，成为第一个评价的用户吧！
              </div>
              <div v-else>
                <div v-for="review in reviews" :key="review.id"
                     class="border-b border-gray-200 pb-4 mb-4 last:border-b-0">
                  <div class="flex justify-between items-start mb-2">
                    <div>
                      <div class="flex items-center gap-2 mb-1">
                        <span class="font-medium">{{ review.customerName || '匿名用户' }}</span>
                        <div class="flex">
                          <span v-for="i in 5" :key="i"
                                :class="i <= review.rating ? 'text-yellow-400' : 'text-gray-300'">★</span>
                        </div>
                      </div>
                      <div class="text-sm text-gray-500">{{ formatDate(review.dateCreated) }}</div>
                    </div>
                    <div v-if="review.verifiedPurchase" class="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
                      已验证购买
                    </div>
                  </div>
                  <h5 v-if="review.title" class="font-medium mb-2">{{ review.title }}</h5>
                  <p class="text-gray-700">{{ review.content }}</p>
                  <div v-if="review.helpfulCount > 0" class="mt-2 text-sm text-gray-500">
                    {{ review.helpfulCount }} 人觉得有帮助
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-8">
          <p class="text-red-600">产品未找到</p>
          <router-link to="/" class="text-blue-600 hover:underline mt-4 inline-block">
            返回首页
          </router-link>
        </div>
      </main>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCart } from '../composables/useCart.js'
import AppHeader from '../components/AppHeader.vue'
import AppSidebar from '../components/AppSidebar.vue'
import AppFooter from '../components/AppFooter.vue'
import productApi from '../services/productApi.js'
import reviewApi from '../services/reviewApi.js'
import authService from '../services/auth.js'

const router = useRouter()
const route = useRoute()
const { addToCart: addToCartFunction } = useCart()

// 响应式数据
const categories = ref([])
const product = ref(null)
const loading = ref(false)
const reviews = ref([])
const reviewSummary = ref(null)
const newReview = ref({
  rating: 0,
  title: '',
  content: ''
})

// 计算属性
const averageRating = computed(() => {
  return reviewSummary.value?.averageRating || 0
})

const totalReviews = computed(() => {
  return reviewSummary.value?.totalReviews || 0
})

const isAuthenticated = computed(() => {
  return authService.isAuthenticated()
})

// 获取指定评分的数量
const getRatingCount = (rating) => {
  return reviewSummary.value?.ratingDistribution?.[rating] || 0
}

// 获取指定评分的百分比
const getRatingPercentage = (rating) => {
  if (totalReviews.value === 0) return 0
  return (getRatingCount(rating) / totalReviews.value) * 100
}

// API调用方法
const fetchCategories = async () => {
  try {
    const response = await productApi.getCategories()
    if (response.status === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchProduct = async (productId) => {
  loading.value = true
  try {
    const response = await productApi.getProduct(productId)
    console.log('产品详情响应:', response)
    if (response.status === 200) {
      product.value = response.data
    } else {
      console.error('获取产品详情失败:', response.message)
      product.value = null
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    product.value = null
  } finally {
    loading.value = false
  }
}

const fetchReviews = async (productId) => {
  try {
    const response = await reviewApi.getProductReviews(productId)
    if (response.status === 200) {
      reviews.value = response.data.content || []
    }
  } catch (error) {
    console.error('获取评价失败:', error)
    reviews.value = []
  }
}

const fetchReviewSummary = async (productId) => {
  try {
    const response = await reviewApi.getProductReviewSummary(productId)
    if (response.status === 200) {
      reviewSummary.value = response.data
    }
  } catch (error) {
    console.error('获取评价汇总失败:', error)
    reviewSummary.value = null
  }
}

// 处理图片URL
const getImageUrl = (imageUrl) => {
  if (!imageUrl) return ''

  // 如果已经是完整URL，直接返回
  if (imageUrl.startsWith('http')) {
    return imageUrl
  }

  // 如果不是以/uploads开头，添加/uploads前缀
  let processedUrl = imageUrl.startsWith('/uploads') ? imageUrl : `/uploads${imageUrl}`

  // 添加后端baseUrl
  return `http://localhost:8080${processedUrl}`
}

// 事件处理方法
const addToCart = async () => {
  console.log('Adding to cart:', product.value.id)
  const success = await addToCartFunction(product.value.id, 1)
  if (success) {
    alert('商品已添加到购物车')
  }
}

const handleSelectCategory = (categoryId) => {
  router.push({ name: 'home', query: { category: categoryId } })
}

const handleShowAllProducts = () => {
  router.push({ name: 'home' })
}

const handleImageError = (event) => {
  console.log('图片加载失败:', event.target.src)
  event.target.style.display = 'none'
}

const submitReview = async () => {
  if (!isAuthenticated.value) {
    alert('请先登录后再评价')
    router.push('/login')
    return
  }

  if (!newReview.value.rating || !newReview.value.content) {
    alert('请填写评分和评价内容')
    return
  }

  try {
    const response = await reviewApi.createReview(product.value.id, {
      rating: newReview.value.rating,
      title: newReview.value.title,
      content: newReview.value.content
    })

    if (response.status === 200) {
      alert('评价提交成功！')
      // 重置表单
      newReview.value = { rating: 0, title: '', content: '' }
      // 重新获取评价数据
      await Promise.all([
        fetchReviews(product.value.id),
        fetchReviewSummary(product.value.id)
      ])
    } else {
      alert('评价提交失败：' + response.message)
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    alert('评价提交失败：' + error.message)
  }
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
  if (newId) {
    fetchProduct(newId)
    fetchReviews(newId)
    fetchReviewSummary(newId)
  }
})

// 组件挂载时初始化数据
onMounted(() => {
  fetchCategories()
  const productId = route.params.id
  if (productId) {
    fetchProduct(productId)
    fetchReviews(productId)
    fetchReviewSummary(productId)
  }
})
</script>

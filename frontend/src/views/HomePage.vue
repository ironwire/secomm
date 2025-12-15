<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <AppHeader @search="handleSearch" />

    <div class="flex flex-1 relative">
      <AppSidebar
        :categories="categories"
        :selectedCategoryId="selectedCategoryId"
        :showDebugInfo="true"
        @selectCategory="selectCategory"
        @showAllProducts="showAllProducts"
      />

      <main class="flex-1 p-8">
        <div v-if="loading" class="text-center py-8">
          <p>加载中...</p>
        </div>

        <div v-else class="grid grid-cols-5 gap-6 mb-8">
          <router-link
            v-for="product in products"
            :key="product.id"
            :to="{ name: 'product-detail', params: { id: product.id } }"
            class="bg-white border border-gray-200 rounded p-4 hover:shadow-lg transition-shadow cursor-pointer"
          >
            <div class="aspect-square bg-gray-100 mb-3 flex items-center justify-center overflow-hidden">
              <img
                v-if="product.imageUrl"
                :src="getImageUrl(product.imageUrl)"
                :alt="product.name"
                class="w-full h-full object-cover"
                @error="handleImageError"
              />
              <svg
                v-else
                class="w-16 h-16 text-gray-300"
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
            <h3 class="font-semibold text-sm mb-1">{{ product.name }}</h3>
            <p class="text-gray-600 text-xs mb-2">{{ product.description }}</p>
            <p class="font-bold mb-3">￥{{ product.unitPrice }}</p>
            <button
              @click.prevent.stop="addToCart(product)"
              class="w-full px-4 py-2 border border-gray-900 rounded hover:bg-gray-100 text-sm"
            >
              加入购物车
            </button>
          </router-link>
        </div>

        <!-- 翻页 -->
        <div class="flex items-center justify-end gap-4">
          <div class="flex items-center gap-2">
            <span class="text-sm">每页多少个:</span>
            <select
              :value="pageSize"
              @change="changePageSize(parseInt($event.target.value))"
              class="border border-gray-300 rounded px-2 py-1"
            >
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
          <span class="text-sm">
            {{ currentPage * pageSize + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalElements) }} 总共 {{ totalElements }}
          </span>
          <div class="flex gap-2">
            <button
              @click="goToPreviousPage"
              :disabled="currentPage === 0"
              :class="[
                'p-2 border border-gray-300 rounded transition-colors',
                currentPage === 0
                  ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                  : 'hover:bg-gray-100'
              ]"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15 19l-7-7 7-7"
                />
              </svg>
            </button>
            <button
              @click="goToNextPage"
              :disabled="currentPage >= totalPages - 1"
              :class="[
                'p-2 border border-gray-300 rounded transition-colors',
                currentPage >= totalPages - 1
                  ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                  : 'hover:bg-gray-100'
              ]"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M9 5l7 7-7 7"
                />
              </svg>
            </button>
          </div>
        </div>
      </main>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useCart } from '../composables/useCart.js'
import AppHeader from '../components/AppHeader.vue'
import AppSidebar from '../components/AppSidebar.vue'
import AppFooter from '../components/AppFooter.vue'

const route = useRoute()
const { addToCart: addToCartFunction } = useCart()

const categories = ref([])
const products = ref([])
const selectedCategoryId = ref(null)
const loading = ref(false)

// 分页相关状态
const currentPage = ref(0)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const fetchCategories = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/categories')
    const data = await response.json()
    console.log('分类数据响应:', data)
    if (data.status === 200) {
      categories.value = data.data
      console.log('分类数据:', categories.value)
    } else {
      console.error('获取分类失败:', data.message)
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchProducts = async (categoryId = null, page = 0, size = pageSize.value) => {
  loading.value = true
  try {
    let url = categoryId
      ? `http://localhost:8080/api/products/category/${categoryId}`
      : 'http://localhost:8080/api/products'

    url += `?page=${page}&size=${size}`

    const response = await fetch(url)
    const data = await response.json()
    console.log('产品数据响应:', data)
    if (data.status === 200) {
      products.value = data.data.content || []
      currentPage.value = data.data.pageNumber || 0
      totalElements.value = data.data.totalElements || 0
      totalPages.value = data.data.totalPages || 0
      console.log('产品数据:', products.value)
    }
  } catch (error) {
    console.error('获取产品失败:', error)
  } finally {
    loading.value = false
  }
}

const searchProducts = async (keyword, page = 0, size = pageSize.value) => {
  if (!keyword.trim()) {
    showAllProducts()
    return
  }

  loading.value = true
  try {
    const url = `http://localhost:8080/api/products/search?keyword=${encodeURIComponent(keyword.trim())}&page=${page}&size=${size}`
    const response = await fetch(url)
    const data = await response.json()
    console.log('搜索结果:', data)
    if (data.status === 200) {
      products.value = data.data.content || []
      currentPage.value = data.data.pageNumber || 0
      totalElements.value = data.data.totalElements || 0
      totalPages.value = data.data.totalPages || 0
      selectedCategoryId.value = null
    }
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理来自Header组件的搜索事件
const handleSearch = (keyword) => {
  searchProducts(keyword, 0, pageSize.value)
}

// 处理来自Sidebar组件的分类选择事件
const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  fetchProducts(categoryId, 0, pageSize.value)
}

// 处理来自Sidebar组件的显示所有产品事件
const showAllProducts = () => {
  selectedCategoryId.value = null
  fetchProducts(null, 0, pageSize.value)
}

// 添加到购物车功能
const addToCart = async (product) => {
  console.log('Adding to cart:', product.id) // Debug log
  const success = await addToCartFunction(product.id, 1)
  if (success) {
    alert('商品已添加到购物车')
  }
}

const goToPage = (page) => {
  if (page < 0 || page >= totalPages.value) return
  fetchProducts(selectedCategoryId.value, page, pageSize.value)
}

const goToPreviousPage = () => {
  if (currentPage.value > 0) {
    goToPage(currentPage.value - 1)
  }
}

const goToNextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    goToPage(currentPage.value + 1)
  }
}

const changePageSize = (newSize) => {
  pageSize.value = newSize
  goToPage(0)
}

const handleImageError = (event) => {
  console.log('图片加载失败:', event.target.src)
  event.target.style.display = 'none'
  const svg = event.target.nextElementSibling
  if (svg) {
    svg.style.display = 'block'
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

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

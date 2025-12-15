<template>
  <aside
    :style="{ width: sidebarWidth + 'px' }"
    class="bg-blue-50 border-r border-gray-300 flex-shrink-0 relative"
  >
    <div class="p-6">
      <h2 class="text-lg font-semibold mb-4">按类别购物</h2>
      <!-- 添加调试信息 -->
      <div v-if="showDebugInfo" class="text-xs text-gray-500 mb-2">
        分类数量: {{ categories.length }}
      </div>
      <nav class="space-y-2">
        <a
          href="#"
          @click.prevent="handleShowAllProducts"
          :class="[
            'block hover:underline',
            selectedCategoryId === null ? 'text-blue-800 font-semibold' : 'text-blue-600'
          ]"
        >
          全部商品
        </a>
        <a
          v-for="category in categories"
          :key="category.id"
          href="#"
          @click.prevent="handleSelectCategory(category.id)"
          :class="[
            'block hover:underline',
            selectedCategoryId === category.id ? 'text-blue-800 font-semibold' : 'text-blue-600'
          ]"
        >
          {{ category.categoryName }}
        </a>
      </nav>
    </div>

    <div
      @mousedown="startResize"
      class="absolute top-0 right-0 w-1 h-full cursor-col-resize hover:bg-blue-400 transition-colors group"
    >
      <div
        class="absolute top-1/2 right-0 w-1 h-12 bg-gray-400 group-hover:bg-blue-500 transition-colors"
      ></div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

// Props
const props = defineProps({
  categories: {
    type: Array,
    default: () => []
  },
  selectedCategoryId: {
    type: Number,
    default: null
  },
  showDebugInfo: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['selectCategory', 'showAllProducts'])

// 状态
const sidebarWidth = ref(200)
const isResizing = ref(false)

// 方法
const handleSelectCategory = (categoryId) => {
  emit('selectCategory', categoryId)
}

const handleShowAllProducts = () => {
  emit('showAllProducts')
}

const startResize = (e) => {
  isResizing.value = true
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
  e.preventDefault()
}

const handleResize = (e) => {
  if (isResizing.value) {
    const newWidth = e.clientX
    if (newWidth >= 150 && newWidth <= 400) {
      sidebarWidth.value = newWidth
    }
  }
}

const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
}

onUnmounted(() => {
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
})
</script>
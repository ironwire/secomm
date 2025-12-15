<template>
  <div class="flex items-center gap-4 flex-1 max-w-3xl mx-8">
    <input
      v-model="searchKeyword"
      type="search"
      placeholder="搜索商品名称或描述..."
      @keydown="handleSearchKeydown"
      class="flex-1 px-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500"
    />
    <button
      @click="handleSearchClick"
      class="px-6 py-2 border border-gray-900 rounded hover:bg-gray-100"
    >
      查询
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['search'])

const searchKeyword = ref('')

const handleSearchKeydown = (event) => {
  if (event.key === 'Enter') {
    handleSearch()
  }
}

const handleSearchClick = () => {
  handleSearch()
}

const handleSearch = () => {
  emit('search', searchKeyword.value)
}
</script>
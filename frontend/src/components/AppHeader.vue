<template>
  <header class="bg-white border-b border-gray-200 px-6 py-4">
    <div class="flex items-center justify-between">
      <router-link to="/" class="text-2xl font-bold hover:text-blue-600">我的书店</router-link>

      <SearchBar v-if="showSearch" @search="handleSearch" />

      <div class="flex items-center gap-4">
        <CartStatus />
        <UserAuth />
      </div>
    </div>
  </header>
</template>

<script setup>
import SearchBar from './SearchBar.vue'
import CartStatus from './CartStatus.vue'
import UserAuth from './UserAuth.vue'

const props = defineProps({
  showSearch: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['search'])

const handleSearch = (keyword) => {
  emit('search', keyword)
}
</script>

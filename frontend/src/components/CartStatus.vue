<template>
  <div class="flex items-center gap-4">
    <div class="flex items-center gap-2 px-3 py-1 border border-gray-900 rounded">
      <span class="font-semibold">￥{{ cartTotal }}</span>
      <span class="bg-gray-200 px-2 py-0.5 rounded text-sm">{{ cartItemCount }}</span>
    </div>
    <router-link
      :to="{ name: 'shopping-cart' }"
      class="text-2xl hover:opacity-75 transition-opacity"
    >
      🛒
    </router-link>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useCart } from '../composables/useCart.js'

const { cartTotal, cartItemCount, initCart } = useCart()

onMounted(() => {
  initCart()
})
</script>

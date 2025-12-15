<template>
  <div v-if="user" class="flex items-center gap-2">
    <span class="text-sm text-gray-600">欢迎，{{ user.realName }}</span>
    <router-link
      to="/individual"
      class="px-3 py-1 text-sm text-blue-600 hover:text-blue-800 border border-blue-300 rounded hover:bg-blue-50"
    >
      个人中心
    </router-link>
    <button
      @click="handleLogout"
      class="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-100"
    >
      退出
    </button>
  </div>
  <div v-else class="flex items-center gap-2">
    <router-link
      to="/login"
      class="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-100"
    >
      登录
    </router-link>
    <router-link
      to="/register"
      class="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
    >
      注册
    </router-link>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref(null)

const checkUserLogin = () => {
  const token = localStorage.getItem('token')
  const userData = localStorage.getItem('user')

  if (token && userData) {
    user.value = JSON.parse(userData)
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  user.value = null
  router.push('/')
}

onMounted(() => {
  checkUserLogin()
})
</script>

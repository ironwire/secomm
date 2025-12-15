<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <header class="bg-white border-b border-gray-200 px-6 py-4">
      <div class="flex items-center justify-between">
        <router-link to="/" class="text-2xl font-bold hover:text-blue-600">我的书店</router-link>
        <div class="flex items-center gap-4">
          <router-link
            to="/login"
            class="px-4 py-2 text-blue-600 hover:text-blue-800"
          >
            已有账号登录
          </router-link>
        </div>
      </div>
    </header>

    <main class="flex-1 flex items-center justify-center p-8">
      <div class="bg-white rounded-lg shadow-lg p-8 w-full max-w-md">
        <h2 class="text-2xl font-bold text-center mb-6">用户注册</h2>

        <form @submit.prevent="handleRegister">
          <div class="mb-4">
            <label for="username" class="block text-sm font-medium text-gray-700 mb-2">
              邮箱地址 *
            </label>
            <input
              id="username"
              v-model="registerForm.username"
              type="email"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入邮箱地址"
            />
          </div>

          <div class="mb-4">
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
              密码 *
            </label>
            <input
              id="password"
              v-model="registerForm.password"
              type="password"
              required
              minlength="6"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入密码（至少6位）"
            />
          </div>

          <div class="mb-4">
            <label for="realName" class="block text-sm font-medium text-gray-700 mb-2">
              真实姓名 *
            </label>
            <input
              id="realName"
              v-model="registerForm.realName"
              type="text"
              required
              minlength="2"
              maxlength="50"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入真实姓名"
            />
          </div>

          <div class="mb-4">
            <label for="phone" class="block text-sm font-medium text-gray-700 mb-2">
              手机号码 *
            </label>
            <input
              id="phone"
              v-model="registerForm.phone"
              type="tel"
              required
              pattern="^1[3-9]\d{9}$"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入手机号码"
            />
          </div>

          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              性别
            </label>
            <div class="flex gap-4">
              <label class="flex items-center">
                <input
                  v-model="registerForm.gender"
                  type="radio"
                  value="M"
                  class="mr-2"
                />
                男
              </label>
              <label class="flex items-center">
                <input
                  v-model="registerForm.gender"
                  type="radio"
                  value="F"
                  class="mr-2"
                />
                女
              </label>
            </div>
          </div>

          <div class="mb-6">
            <label for="roleCode" class="block text-sm font-medium text-gray-700 mb-2">
              注册角色 *
            </label>
            <select
              id="roleCode"
              v-model="registerForm.roleCode"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">请选择角色</option>
              <option
                v-for="role in roles"
                :key="role.id"
                :value="role.roleCode"
              >
                {{ role.roleName }}
              </option>
            </select>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <div v-if="errorMessage" class="mt-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="mt-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
          {{ successMessage }}
        </div>

        <div class="mt-6 text-center">
          <span class="text-gray-600">已有账号？</span>
          <router-link to="/login" class="text-blue-600 hover:text-blue-800 ml-1">
            立即登录
          </router-link>
        </div>
      </div>
    </main>

    <footer class="bg-white border-t border-gray-200 py-4">
      <div class="flex justify-center gap-6 text-sm">
        <a href="#" class="hover:underline">关于我们</a>
        <a href="#" class="hover:underline">联系我们</a>
        <a href="#" class="hover:underline">获得帮助</a>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const registerForm = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  gender: 'M',
  roleCode: ''
})

const roles = ref([])
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

// 获取角色列表
const fetchRoles = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/roles')
    const data = await response.json()

    if (data.status === 200) {
      roles.value = data.data
    } else {
      console.error('获取角色列表失败:', data.message)
    }
  } catch (error) {
    console.error('获取角色列表错误:', error)
  }
}

const handleRegister = async () => {
  loading.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/auth/signup', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(registerForm.value)
    })

    const data = await response.json()

    if (data.status === 200) {
      successMessage.value = data.message + ' 3秒后跳转到登录页面...'

      // 3秒后跳转到登录页面
      setTimeout(() => {
        router.push('/login')
      }, 3000)
    } else {
      errorMessage.value = data.message || '注册失败'
    }
  } catch (error) {
    console.error('注册错误:', error)
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRoles()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <header class="bg-white border-b border-gray-200 px-6 py-4">
      <div class="flex items-center justify-between">
        <router-link to="/" class="text-2xl font-bold hover:text-blue-600">我的书店</router-link>
        <div class="flex items-center gap-4">
          <router-link
            to="/"
            class="px-4 py-2 text-gray-600 hover:text-blue-600"
          >
            返回首页
          </router-link>
          <UserAuth />
        </div>
      </div>
    </header>

    <main class="flex-1 p-8">
      <div class="max-w-4xl mx-auto">
        <!-- Page Header -->
        <div class="mb-8">
          <h1 class="text-3xl font-bold text-gray-900">个人中心</h1>
          <p class="text-gray-600 mt-1">管理您的个人信息和订单</p>
        </div>

        <!-- Tab Navigation -->
        <div class="mb-6">
          <nav class="flex space-x-8">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              @click="activeTab = tab.key; handleTabChange()"
              :class="[
                'py-2 px-1 border-b-2 font-medium text-sm',
                activeTab === tab.key
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              {{ tab.label }}
            </button>
          </nav>
        </div>

        <!-- Tab Content -->
        <div class="bg-white rounded-lg shadow">
          <!-- 审批状态 -->
          <div v-if="activeTab === 'approval'" class="p-6">
            <h2 class="text-xl font-semibold mb-4">审批状态</h2>
            <div v-if="loading.approval" class="text-center py-8">
              <p>加载中...</p>
            </div>
            <div v-else class="space-y-4">
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <h3 class="font-medium text-gray-900">账户审批状态</h3>
                    <p class="text-sm text-gray-600 mt-1">您的账户当前审批状态</p>
                  </div>
                  <div class="text-right">
                    <span
                      :class="[
                        'inline-flex px-3 py-1 rounded-full text-sm font-medium',
                        getApprovalStatusClass(userInfo.approvalStatus)
                      ]"
                    >
                      {{ getApprovalStatusText(userInfo.approvalStatus) }}
                    </span>
                    <p class="text-xs text-gray-500 mt-1">
                      更新时间: {{ formatDate(userInfo.updateTime) }}
                    </p>
                  </div>
                </div>
              </div>

              <div v-if="userInfo.approvalStatus === 'PENDING'" class="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <div class="flex">
                  <div class="flex-shrink-0">
                    <svg class="h-5 w-5 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                    </svg>
                  </div>
                  <div class="ml-3">
                    <h3 class="text-sm font-medium text-yellow-800">等待审批</h3>
                    <p class="text-sm text-yellow-700 mt-1">您的账户正在等待管理员审批，请耐心等待。</p>
                  </div>
                </div>
              </div>

              <div v-else-if="userInfo.approvalStatus === 'APPROVED'" class="bg-green-50 border border-green-200 rounded-lg p-4">
                <div class="flex">
                  <div class="flex-shrink-0">
                    <svg class="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                  </div>
                  <div class="ml-3">
                    <h3 class="text-sm font-medium text-green-800">审批通过</h3>
                    <p class="text-sm text-green-700 mt-1">恭喜！您的账户已通过审批，可以正常使用所有功能。</p>
                  </div>
                </div>
              </div>

              <div v-else-if="userInfo.approvalStatus === 'REJECTED'" class="bg-red-50 border border-red-200 rounded-lg p-4">
                <div class="flex">
                  <div class="flex-shrink-0">
                    <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                    </svg>
                  </div>
                  <div class="ml-3">
                    <h3 class="text-sm font-medium text-red-800">审批被拒</h3>
                    <p class="text-sm text-red-700 mt-1">很抱歉，您的账户审批未通过。如有疑问，请联系客服。</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 个人信息 -->
          <div v-if="activeTab === 'profile'" class="p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-semibold">个人信息</h2>
              <button
                @click="editMode = !editMode"
                :class="[
                  'px-4 py-2 rounded-md text-sm font-medium',
                  editMode
                    ? 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                ]"
              >
                {{ editMode ? '取消编辑' : '编辑信息' }}
              </button>
            </div>

            <div v-if="loading.profile" class="text-center py-8">
              <p>加载中...</p>
            </div>

            <form v-else @submit.prevent="updateProfile" class="space-y-6">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
                  <input
                    type="text"
                    :value="userInfo.username"
                    disabled
                    class="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-500"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">真实姓名</label>
                  <input
                    type="text"
                    :value="userInfo.realName"
                    disabled
                    class="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-500"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">手机号码</label>
                  <input
                    type="tel"
                    v-model="profileForm.phone"
                    :disabled="!editMode"
                    :class="[
                      'w-full px-3 py-2 border rounded-md',
                      editMode
                        ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                        : 'border-gray-300 bg-gray-50 text-gray-500'
                    ]"
                    placeholder="请输入手机号码"
                  />
                </div>
              </div>

              <div v-if="editMode" class="flex justify-end space-x-3">
                <button
                  type="button"
                  @click="editMode = false"
                  class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  取消
                </button>
                <button
                  type="submit"
                  :disabled="updating"
                  class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 disabled:opacity-50"
                >
                  {{ updating ? '保存中...' : '保存更改' }}
                </button>
              </div>
            </form>
          </div>

          <!-- 地址管理 -->
          <div v-if="activeTab === 'addresses'" class="p-6">
            <h2 class="text-xl font-semibold mb-4">地址管理</h2>

            <!-- 送货地址 -->
            <div class="mb-8">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-medium">送货地址</h3>
                <button
                  @click="editShippingAddress = !editShippingAddress"
                  :class="[
                    'px-4 py-2 rounded-md text-sm font-medium',
                    editShippingAddress
                      ? 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                      : 'bg-blue-600 text-white hover:bg-blue-700'
                  ]"
                >
                  {{ editShippingAddress ? '取消编辑' : '编辑地址' }}
                </button>
              </div>

              <form @submit.prevent="updateShippingAddress" class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">国家</label>
                    <select
                      v-model="shippingAddressForm.country"
                      :disabled="!editShippingAddress"
                      :class="[
                        'w-full px-3 py-2 border rounded-md',
                        editShippingAddress
                          ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                          : 'border-gray-300 bg-gray-50 text-gray-500'
                      ]"
                    >
                      <option value="">请选择国家</option>
                      <option value="中国">中国</option>
                      <option value="美国">美国</option>
                      <option value="日本">日本</option>
                      <option value="韩国">韩国</option>
                      <option value="英国">英国</option>
                      <option value="法国">法国</option>
                      <option value="德国">德国</option>
                      <option value="加拿大">加拿大</option>
                      <option value="澳大利亚">澳大利亚</option>
                      <option value="新加坡">新加坡</option>
                    </select>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">省份</label>
                    <select
                      v-model="shippingAddressForm.state"
                      :disabled="!editShippingAddress"
                      :class="[
                        'w-full px-3 py-2 border rounded-md',
                        editShippingAddress
                          ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                          : 'border-gray-300 bg-gray-50 text-gray-500'
                      ]"
                    >
                      <option value="">请选择省份</option>
                      <option value="北京市">北京市</option>
                      <option value="上海市">上海市</option>
                      <option value="广东省">广东省</option>
                      <option value="江苏省">江苏省</option>
                      <option value="浙江省">浙江省</option>
                      <option value="山东省">山东省</option>
                      <option value="河南省">河南省</option>
                      <option value="四川省">四川省</option>
                      <option value="湖北省">湖北省</option>
                      <option value="湖南省">湖南省</option>
                    </select>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">城市</label>
                    <select
                      v-model="shippingAddressForm.city"
                      :disabled="!editShippingAddress"
                      :class="[
                        'w-full px-3 py-2 border rounded-md',
                        editShippingAddress
                          ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                          : 'border-gray-300 bg-gray-50 text-gray-500'
                      ]"
                    >
                      <option value="">请选择城市</option>
                      <option value="北京市">北京市</option>
                      <option value="上海市">上海市</option>
                      <option value="广州市">广州市</option>
                      <option value="深圳市">深圳市</option>
                      <option value="杭州市">杭州市</option>
                      <option value="南京市">南京市</option>
                      <option value="武汉市">武汉市</option>
                      <option value="成都市">成都市</option>
                      <option value="西安市">西安市</option>
                      <option value="天津市">天津市</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">详细地址</label>
                  <input
                    type="text"
                    v-model="shippingAddressForm.street"
                    :disabled="!editShippingAddress"
                    :class="[
                      'w-full px-3 py-2 border rounded-md',
                      editShippingAddress
                        ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                        : 'border-gray-300 bg-gray-50 text-gray-500'
                    ]"
                    placeholder="请输入详细地址"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">邮政编码</label>
                  <input
                    type="text"
                    v-model="shippingAddressForm.zipCode"
                    :disabled="!editShippingAddress"
                    :class="[
                      'w-full px-3 py-2 border rounded-md',
                      editShippingAddress
                        ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                        : 'border-gray-300 bg-gray-50 text-gray-500'
                    ]"
                    placeholder="请输入邮政编码"
                  />
                </div>

                <div v-if="editShippingAddress" class="flex justify-end space-x-3">
                  <button
                    type="button"
                    @click="editShippingAddress = false"
                    class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                  >
                    取消
                  </button>
                  <button
                    type="submit"
                    :disabled="updatingShipping"
                    class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 disabled:opacity-50"
                  >
                    {{ updatingShipping ? '保存中...' : '保存更改' }}
                  </button>
                </div>
              </form>
            </div>

            <!-- 账单地址 -->
            <div class="border-t border-gray-200 pt-8">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-medium">账单地址</h3>
                <button
                  @click="editBillingAddress = !editBillingAddress"
                  :class="[
                    'px-4 py-2 rounded-md text-sm font-medium',
                    editBillingAddress
                      ? 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                      : 'bg-blue-600 text-white hover:bg-blue-700'
                  ]"
                >
                  {{ editBillingAddress ? '取消编辑' : '编辑地址' }}
                </button>
              </div>

              <form @submit.prevent="updateBillingAddress" class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">国家</label>
                    <select
                      v-model="billingAddressForm.country"
                      :disabled="!editBillingAddress"
                      :class="[
                        'w-full px-3 py-2 border rounded-md',
                        editBillingAddress
                          ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                          : 'border-gray-300 bg-gray-50 text-gray-500'
                      ]"
                    >
                      <option value="">请选择国家</option>
                      <option value="中国">中国</option>
                      <option value="美国">美国</option>
                      <option value="日本">日本</option>
                      <option value="韩国">韩国</option>
                    </select>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">省份</label>
                    <select
                      v-model="billingAddressForm.state"
                      :disabled="!editBillingAddress"
                      :class="[
                        'w-full px-3 py-2 border rounded-md',
                        editBillingAddress
                          ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                          : 'border-gray-300 bg-gray-50 text-gray-500'
                      ]"
                    >
                      <option value="">请选择省份</option>
                      <option value="北京市">北京市</option>
                      <option value="上海市">上海市</option>
                      <option value="广东省">广东省</option>
                      <option value="江苏省">江苏省</option>
                      <option value="浙江省">浙江省</option>
                    </select>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">城市</label>
                    <select
                      v-model="billingAddressForm.city"
                      :disabled="!editBillingAddress"
                      :class="[
                        'w-full px-3 py-2 border rounded-md',
                        editBillingAddress
                          ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                          : 'border-gray-300 bg-gray-50 text-gray-500'
                      ]"
                    >
                      <option value="">请选择城市</option>
                      <option value="北京市">北京市</option>
                      <option value="上海市">上海市</option>
                      <option value="广州市">广州市</option>
                      <option value="深圳市">深圳市</option>
                      <option value="杭州市">杭州市</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">详细地址</label>
                  <input
                    type="text"
                    v-model="billingAddressForm.street"
                    :disabled="!editBillingAddress"
                    :class="[
                      'w-full px-3 py-2 border rounded-md',
                      editBillingAddress
                        ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                        : 'border-gray-300 bg-gray-50 text-gray-500'
                    ]"
                    placeholder="请输入详细地址"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">邮政编码</label>
                  <input
                    type="text"
                    v-model="billingAddressForm.zipCode"
                    :disabled="!editBillingAddress"
                    :class="[
                      'w-full px-3 py-2 border rounded-md',
                      editBillingAddress
                        ? 'border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                        : 'border-gray-300 bg-gray-50 text-gray-500'
                    ]"
                    placeholder="请输入邮政编码"
                  />
                </div>

                <div v-if="editBillingAddress" class="flex justify-end space-x-3">
                  <button
                    type="button"
                    @click="editBillingAddress = false"
                    class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                  >
                    取消
                  </button>
                  <button
                    type="submit"
                    :disabled="updatingBilling"
                    class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 disabled:opacity-50"
                  >
                    {{ updatingBilling ? '保存中...' : '保存更改' }}
                  </button>
                </div>
              </form>
            </div>
          </div>

          <!-- 我的订单 -->
          <div v-if="activeTab === 'orders'" class="p-6">
            <h2 class="text-xl font-semibold mb-4">我的订单</h2>

            <div v-if="loading.orders" class="text-center py-8">
              <p>加载中...</p>
            </div>

            <div v-else-if="orders.length === 0" class="text-center py-8">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              <h3 class="mt-2 text-sm font-medium text-gray-900">暂无订单</h3>
              <p class="mt-1 text-sm text-gray-500">您还没有任何订单记录</p>
              <div class="mt-6">
                <router-link
                  to="/"
                  class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
                >
                  开始购物
                </router-link>
              </div>
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="order in orders"
                :key="order.id"
                class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
              >
                <div class="flex items-center justify-between mb-3">
                  <div>
                    <h3 class="font-medium text-gray-900">订单 #{{ order.orderNumber }}</h3>
                    <p class="text-sm text-gray-500">{{ formatDate(order.orderDate) }}</p>
                  </div>
                  <span
                    :class="[
                      'inline-flex px-3 py-1 rounded-full text-sm font-medium',
                      getOrderStatusClass(order.status)
                    ]"
                  >
                    {{ getOrderStatusText(order.status) }}
                  </span>
                </div>

                <div class="border-t border-gray-200 pt-3">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center space-x-4">
                      <span class="text-sm text-gray-600">
                        共 {{ order.totalQuantity }} 件商品
                      </span>
                      <span class="text-lg font-semibold text-gray-900">
                        ¥{{ order.totalAmount }}
                      </span>
                    </div>
                    <button
                      @click="viewOrderDetail(order)"
                      class="text-blue-600 hover:text-blue-800 text-sm font-medium"
                    >
                      查看详情
                    </button>
                  </div>
                </div>
              </div>

              <!-- 分页 -->
              <div v-if="orderPagination.totalPages > 1" class="flex justify-center mt-6">
                <nav class="flex items-center space-x-2">
                  <button
                    @click="loadOrders(orderPagination.currentPage - 1)"
                    :disabled="orderPagination.currentPage === 0"
                    class="px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    上一页
                  </button>
                  <span class="px-3 py-2 text-sm text-gray-700">
                    第 {{ orderPagination.currentPage + 1 }} 页，共 {{ orderPagination.totalPages }} 页
                  </span>
                  <button
                    @click="loadOrders(orderPagination.currentPage + 1)"
                    :disabled="orderPagination.currentPage >= orderPagination.totalPages - 1"
                    class="px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    下一页
                  </button>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <AppFooter />
  </div>

  <!-- 订单详情模态框 -->
  <div v-if="showOrderDetailModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
      <div class="mt-3">
        <!-- 模态框头部 -->
        <div class="flex items-center justify-between pb-4 border-b">
          <h3 class="text-lg font-semibold text-gray-900">订单详情</h3>
          <button
            @click="closeOrderDetailModal"
            class="text-gray-400 hover:text-gray-600 focus:outline-none"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- 订单基本信息 -->
        <div v-if="selectedOrderDetail" class="mt-4">
          <div class="bg-gray-50 rounded-lg p-4 mb-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <h4 class="font-medium text-gray-900 mb-2">订单信息</h4>
                <p class="text-sm text-gray-600">订单号: <span class="font-medium">#{{ selectedOrderDetail.orderNumber }}</span></p>
                <p class="text-sm text-gray-600">下单时间: <span class="font-medium">{{ formatDate(selectedOrderDetail.orderDate) }}</span></p>
                <p class="text-sm text-gray-600">订单状态:
                  <span :class="['inline-flex px-2 py-1 rounded-full text-xs font-medium', getOrderStatusClass(selectedOrderDetail.status)]">
                    {{ getOrderStatusText(selectedOrderDetail.status) }}
                  </span>
                </p>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 mb-2">金额信息</h4>
                <p class="text-sm text-gray-600">商品总数: <span class="font-medium">{{ selectedOrderDetail.totalQuantity || 0 }} 件</span></p>
                <p class="text-sm text-gray-600">订单总额: <span class="font-medium text-green-600">¥{{ selectedOrderDetail.totalAmount }}</span></p>
              </div>
            </div>
          </div>

          <!-- 订单商品列表 -->
          <div class="mb-4">
            <h4 class="font-medium text-gray-900 mb-3">商品清单</h4>
            <div v-if="selectedOrderDetail.orderItems && selectedOrderDetail.orderItems.length > 0" class="space-y-3">
              <div
                v-for="item in selectedOrderDetail.orderItems"
                :key="item.id"
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
              >
                <div class="flex items-center space-x-3">
                  <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center">
                    <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path>
                    </svg>
                  </div>
                  <div>
                    <h5 class="font-medium text-gray-900">{{ item.productName || '商品名称' }}</h5>
                    <p class="text-sm text-gray-600">单价: ¥{{ item.price || item.unitPrice }}</p>
                  </div>
                </div>
                <div class="text-right">
                  <p class="font-medium text-gray-900">数量: {{ item.quantity }}</p>
                  <p class="text-sm text-gray-600">小计: ¥{{ ((item.price || item.unitPrice) * item.quantity).toFixed(2) }}</p>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-4 text-gray-500">
              暂无商品信息
            </div>
          </div>

          <!-- 配送信息 -->
          <div v-if="selectedOrderDetail.shippingAddress" class="mb-4">
            <h4 class="font-medium text-gray-900 mb-3">配送信息</h4>
            <div class="bg-gray-50 rounded-lg p-3">
              <p class="text-sm text-gray-600">
                {{ selectedOrderDetail.shippingAddress.country }}
                {{ selectedOrderDetail.shippingAddress.state }}
                {{ selectedOrderDetail.shippingAddress.city }}
                {{ selectedOrderDetail.shippingAddress.street }}
              </p>
              <p v-if="selectedOrderDetail.shippingAddress.zipCode" class="text-sm text-gray-600">
                邮编: {{ selectedOrderDetail.shippingAddress.zipCode }}
              </p>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-else class="flex items-center justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <span class="ml-2 text-gray-600">加载订单详情中...</span>
        </div>

        <!-- 模态框底部 -->
        <div class="flex justify-end pt-4 border-t">
          <button
            @click="closeOrderDetailModal"
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
          >
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import UserAuth from '../components/UserAuth.vue'
import AppFooter from '../components/AppFooter.vue'
import userService from '../services/userService.js'

const router = useRouter()

// 响应式数据
const activeTab = ref('approval')
const editMode = ref(false)
const updating = ref(false)

const loading = ref({
  approval: false,
  profile: false,
  orders: false
})

const userInfo = ref({
  username: '',
  realName: '',
  phone: '',
  approvalStatus: '',
  updateTime: ''
})

const profileForm = ref({
  phone: ''
})

const orders = ref([])
const orderPagination = ref({
  currentPage: 0,
  totalPages: 0,
  totalElements: 0
})

// 地址编辑状态
const editShippingAddress = ref(false)
const editBillingAddress = ref(false)
const updatingShipping = ref(false)
const updatingBilling = ref(false)

// 地址表单数据
const shippingAddressForm = ref({
  country: '',
  state: '',
  city: '',
  street: '',
  zipCode: ''
})

const billingAddressForm = ref({
  country: '',
  state: '',
  city: '',
  street: '',
  zipCode: ''
})

// Tab配置
const tabs = [
  { key: 'approval', label: '审批状态' },
  { key: 'profile', label: '个人信息' },
  { key: 'addresses', label: '地址管理' },
  { key: 'orders', label: '我的订单' }
]

// 获取用户信息
const loadUserInfo = async () => {
  loading.value.approval = true
  loading.value.profile = true

  try {
    const response = await userService.getCurrentUser()
    if (response.status === 200) {
      userInfo.value = response.data
      profileForm.value = {
        phone: response.data.phone || ''
      }
    } else {
      console.error('获取用户信息失败:', response.message)
      if (response.status === 401) {
        router.push('/login')
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    if (error.message.includes('未授权')) {
      router.push('/login')
    }
  } finally {
    loading.value.approval = false
    loading.value.profile = false
  }
}

// 更新个人信息
const updateProfile = async () => {
  updating.value = true

  try {
    const response = await userService.updateProfile(profileForm.value)
    if (response.status === 200) {
      userInfo.value = { ...userInfo.value, ...profileForm.value }
      editMode.value = false
      alert('个人信息更新成功')
    } else {
      alert('更新失败: ' + response.message)
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    alert('更新失败，请稍后重试')
  } finally {
    updating.value = false
  }
}

// 加载订单列表
const loadOrders = async (page = 0) => {
  console.log('🔍 [loadOrders] 开始加载订单列表, page:', page)
  loading.value.orders = true

  try {
    console.log('🔍 [loadOrders] 调用 userService.getUserOrders, 参数:', { page, size: 10 })
    const response = await userService.getUserOrders({ page, size: 10 })

    console.log('🔍 [loadOrders] API响应:', response)
    console.log('🔍 [loadOrders] 响应状态:', response.status)
    console.log('🔍 [loadOrders] 响应数据:', response.data)

    if (response.status === 200) {
      console.log('🔍 [loadOrders] 响应成功，处理数据')
      console.log('🔍 [loadOrders] response.data.content:', response.data.content)
      console.log('🔍 [loadOrders] response.data类型:', typeof response.data)
      console.log('🔍 [loadOrders] response.data完整内容:', JSON.stringify(response.data, null, 2))

      orders.value = response.data.content || []
      orderPagination.value = {
        currentPage: response.data.pageNumber || 0,
        totalPages: response.data.totalPages || 0,
        totalElements: response.data.totalElements || 0
      }

      console.log('🔍 [loadOrders] 设置后的orders.value:', orders.value)
      console.log('🔍 [loadOrders] 设置后的orderPagination.value:', orderPagination.value)
      console.log('🔍 [loadOrders] orders数组长度:', orders.value.length)
    } else {
      console.error('❌ [loadOrders] 获取订单列表失败:', response.message)
      console.error('❌ [loadOrders] 响应状态码:', response.status)
    }
  } catch (error) {
    console.error('❌ [loadOrders] 获取订单列表异常:', error)
    console.error('❌ [loadOrders] 错误详情:', error.message)
    console.error('❌ [loadOrders] 错误堆栈:', error.stack)
  } finally {
    loading.value.orders = false
    console.log('🔍 [loadOrders] 加载完成，loading.orders设为false')
  }
}

// 订单详情模态框
const showOrderDetailModal = ref(false)
const selectedOrderDetail = ref(null)
const loadingOrderDetail = ref(false)

// 查看订单详情
const viewOrderDetail = async (order) => {
  showOrderDetailModal.value = true
  selectedOrderDetail.value = null
  loadingOrderDetail.value = true

  try {
    console.log('🔍 [viewOrderDetail] 获取订单详情, orderId:', order.id)
    const response = await userService.getOrderById(order.id)

    console.log('🔍 [viewOrderDetail] 订单详情响应:', response)

    if (response.status === 200) {
      selectedOrderDetail.value = response.data
      console.log('🔍 [viewOrderDetail] 订单详情数据:', selectedOrderDetail.value)
    } else {
      console.error('获取订单详情失败:', response.message)
      alert('获取订单详情失败: ' + response.message)
      closeOrderDetailModal()
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    alert('获取订单详情失败，请稍后重试')
    closeOrderDetailModal()
  } finally {
    loadingOrderDetail.value = false
  }
}

// 关闭订单详情模态框
const closeOrderDetailModal = () => {
  showOrderDetailModal.value = false
  selectedOrderDetail.value = null
  loadingOrderDetail.value = false
}

// 工具函数
const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('zh-CN')
  } catch (error) {
    return 'N/A'
  }
}

const getApprovalStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审批',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || '未知'
}

const getApprovalStatusClass = (status) => {
  const classMap = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'APPROVED': 'bg-green-100 text-green-800',
    'REJECTED': 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

const getOrderStatusText = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'CONFIRMED': '已确认',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const getOrderStatusClass = (status) => {
  const classMap = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'CONFIRMED': 'bg-blue-100 text-blue-800',
    'SHIPPED': 'bg-purple-100 text-purple-800',
    'DELIVERED': 'bg-green-100 text-green-800',
    'CANCELLED': 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

// 监听tab切换
const handleTabChange = () => {
  console.log('🔍 [handleTabChange] 切换到tab:', activeTab.value)
  console.log('🔍 [handleTabChange] 当前orders长度:', orders.value.length)

  if (activeTab.value === 'orders' && orders.value.length === 0) {
    console.log('🔍 [handleTabChange] 订单tab且无数据，开始加载订单')
    loadOrders()
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadUserInfo()
  loadUserAddresses()
})

// 更新送货地址
const updateShippingAddress = async () => {
  updatingShipping.value = true

  try {
    const response = await userService.updateShippingAddress(shippingAddressForm.value)
    if (response.status === 200) {
      editShippingAddress.value = false
      alert('送货地址更新成功')
    } else {
      alert('更新失败: ' + response.message)
    }
  } catch (error) {
    console.error('更新送货地址失败:', error)
    alert('更新失败，请稍后重试')
  } finally {
    updatingShipping.value = false
  }
}

// 更新账单地址
const updateBillingAddress = async () => {
  updatingBilling.value = true

  try {
    const response = await userService.updateBillingAddress(billingAddressForm.value)
    if (response.status === 200) {
      editBillingAddress.value = false
      alert('账单地址更新成功')
    } else {
      alert('更新失败: ' + response.message)
    }
  } catch (error) {
    console.error('更新账单地址失败:', error)
    alert('更新失败，请稍后重试')
  } finally {
    updatingBilling.value = false
  }
}

// 加载用户地址
const loadUserAddresses = async () => {
  try {
    const response = await userService.getUserAddresses()
    if (response.status === 200) {
      const addresses = response.data
      const shippingAddr = addresses.find(addr => addr.addressType === 'SHIPPING')
      const billingAddr = addresses.find(addr => addr.addressType === 'BILLING')

      if (shippingAddr) {
        shippingAddressForm.value = {
          country: shippingAddr.country || '',
          state: shippingAddr.state || '',
          city: shippingAddr.city || '',
          street: shippingAddr.street || '',
          zipCode: shippingAddr.zipCode || ''
        }
      }

      if (billingAddr) {
        billingAddressForm.value = {
          country: billingAddr.country || '',
          state: billingAddr.state || '',
          city: billingAddr.city || '',
          street: billingAddr.street || '',
          zipCode: billingAddr.zipCode || ''
        }
      }
    }
  } catch (error) {
    console.error('获取用户地址失败:', error)
  }
}
</script>

<style scoped>
/* 可以添加一些自定义样式 */
</style>

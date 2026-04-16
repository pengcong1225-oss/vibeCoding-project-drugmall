<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { createOrder } from '@/api/modules/order'
import { getAddressList, addAddress as apiAddAddress, deleteAddress as apiDeleteAddress } from '@/api/modules/address'
import type { CartItem } from '@/stores/cart'
import type { UserAddress } from '@/types'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 配送方式
const deliveryType = ref<'delivery' | 'self_pickup' | 'same_day'>('delivery')

// 配送时间选择
const deliveryTime = ref('')
const deliveryTimeOptions = [
  { label: '尽快送达', value: 'asap' },
  { label: '今天 09:00-11:00', value: '09:00-11:00' },
  { label: '今天 11:00-13:00', value: '11:00-13:00' },
  { label: '今天 13:00-15:00', value: '13:00-15:00' },
  { label: '今天 15:00-17:00', value: '15:00-17:00' },
  { label: '今天 17:00-19:00', value: '17:00-19:00' },
  { label: '今天 19:00-21:00', value: '19:00-21:00' },
  { label: '明天 09:00-11:00', value: 'tomorrow_09:00-11:00' }
]

// 优惠券
const selectedCoupon = ref('')
const availableCoupons = ref([
  { id: '', name: '不使用优惠券', value: 0, minAmount: 0 },
  { id: 'coupon1', name: '满50减10元', value: 10, minAmount: 50 },
  { id: 'coupon2', name: '满100减25元', value: 25, minAmount: 100 },
  { id: 'coupon3', name: '满200减60元', value: 60, minAmount: 200 }
])

// 订单备注
const remark = ref('')

// 收货地址
const selectedAddress = ref<UserAddress | null>(null)
const addressList = ref<UserAddress[]>([])
const addressDialogVisible = ref(false)
const isAddingAddress = ref(false)

// 新增地址表单
const newAddressForm = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false,
  tag: ''
})

// 模拟购物车结算商品
const checkoutItems = computed(() => {
  return cartStore.items
})

// 商品总额
const goodsTotal = computed(() => {
  return checkoutItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

// 运费
const deliveryFee = computed(() => {
  if (deliveryType.value === 'self_pickup') return 0
  if (deliveryType.value === 'same_day') return 15
  return goodsTotal.value >= 39 ? 0 : 5
})

// 优惠金额
const discountAmount = computed(() => {
  const coupon = availableCoupons.value.find(c => c.id === selectedCoupon.value)
  if (coupon && goodsTotal.value >= coupon.minAmount) {
    return coupon.value
  }
  return 0
})

// 应付总额
const payableAmount = computed(() => {
  return goodsTotal.value + deliveryFee.value - discountAmount.value
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 打开地址选择弹窗
const openAddressDialog = () => {
  addressDialogVisible.value = true
  loadAddressList()
}

// 加载地址列表
const loadAddressList = async () => {
  try {
    const res = await getAddressList()
    addressList.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取地址列表失败:', error)
    addressList.value = []
  }
  if (!selectedAddress.value) {
    selectedAddress.value = addressList.value.find(a => a.isDefault) || addressList.value[0] || null
  }
}

// 选择地址
const selectAddress = (address: UserAddress) => {
  selectedAddress.value = address
  addressDialogVisible.value = false
}

// 删除地址
const deleteAddress = async (address: UserAddress, event: Event) => {
  event.stopPropagation()
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await apiDeleteAddress(address.id)
    addressList.value = addressList.value.filter(a => a.id !== address.id)
    if (selectedAddress.value?.id === address.id) {
      selectedAddress.value = addressList.value[0] || null
    }
    ElMessage.success('地址已删除')
  } catch {
    // 取消删除
  }
}

// 打开新增地址表单
const openAddAddress = () => {
  isAddingAddress.value = true
  newAddressForm.value = {
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false,
    tag: ''
  }
}

// 保存新地址
const saveNewAddress = async () => {
  if (!newAddressForm.value.name || !newAddressForm.value.phone || !newAddressForm.value.detail) {
    ElMessage.warning('请填写完整的地址信息')
    return
  }
  try {
    const res = await apiAddAddress(newAddressForm.value as any)
    const newAddress: UserAddress = res || {
      id: Date.now().toString(),
      ...newAddressForm.value
    }
    addressList.value.push(newAddress)
    selectedAddress.value = newAddress
    isAddingAddress.value = false
    addressDialogVisible.value = false
    ElMessage.success('地址添加成功')
  } catch (error) {
    ElMessage.error('添加地址失败')
  }
}

// 取消添加地址
const cancelAddAddress = () => {
  isAddingAddress.value = false
}

// 提交订单
const submitOrder = async () => {
  if (checkoutItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  if (deliveryType.value !== 'self_pickup' && !selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  submitting.value = true

  try {
    const orderData = {
      cartItemIds: checkoutItems.value.map(item => item.id),
      addressId: selectedAddress.value?.id || '',
      deliveryType: deliveryType.value,
      deliveryTime: deliveryTime.value,
      remark: remark.value,
      couponId: selectedCoupon.value || undefined
    }

    const res = await createOrder(orderData)
    if (res) {
      ElMessage.success('订单提交成功')
      cartStore.clearCart()
      router.push(`/order/pay?id=${res.id}`)
    }
  } catch (error) {
    ElMessage.error('订单提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (checkoutItems.value.length === 0) {
    ElMessage.warning('购物车为空，请先添加商品')
    router.replace('/home')
  }
  loadAddressList()
})
</script>

<template>
  <div class="order-confirm-page">
    <!-- 头部 -->
    <div class="header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <h1 class="title">确认订单</h1>
      <div class="placeholder" />
    </div>

    <div class="content">
      <!-- 收货地址 -->
      <div class="address-section" @click="openAddressDialog">
        <div class="address-icon">
          <el-icon><Location /></el-icon>
        </div>
        <div v-if="selectedAddress" class="address-info">
          <div class="address-header">
            <span class="name">{{ selectedAddress.name }}</span>
            <span class="phone">{{ selectedAddress.phone }}</span>
            <span v-if="selectedAddress.tag" class="tag">{{ selectedAddress.tag }}</span>
          </div>
          <div class="address-detail">
            {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}
          </div>
        </div>
        <div v-else class="no-address">
          <el-icon><Plus /></el-icon>
          <span>添加收货地址</span>
        </div>
        <div class="arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <!-- 配送方式 -->
      <div class="section">
        <div class="section-title">配送方式</div>
        <div class="delivery-type">
          <div
            class="type-item"
            :class="{ active: deliveryType === 'delivery' }"
            @click="deliveryType = 'delivery'"
          >
            <el-icon><Van /></el-icon>
            <span>普通快递</span>
            <span v-if="goodsTotal >= 39" class="type-tag">免运费</span>
          </div>
          <div
            class="type-item"
            :class="{ active: deliveryType === 'same_day' }"
            @click="deliveryType = 'same_day'"
          >
            <el-icon><Timer /></el-icon>
            <span>次日达</span>
            <span class="type-tag">+15元</span>
          </div>
          <div
            class="type-item"
            :class="{ active: deliveryType === 'self_pickup' }"
            @click="deliveryType = 'self_pickup'"
          >
            <el-icon><Shop /></el-icon>
            <span>门店自提</span>
            <span class="type-tag">免运费</span>
          </div>
        </div>
      </div>

      <!-- 配送时间 -->
      <div v-if="deliveryType !== 'self_pickup'" class="section">
        <div class="section-title">配送时间</div>
        <el-select v-model="deliveryTime" placeholder="请选择配送时间" style="width: 100%">
          <el-option
            v-for="option in deliveryTimeOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </div>

      <!-- 自提门店信息 -->
      <div v-if="deliveryType === 'self_pickup'" class="section pickup-section">
        <div class="section-title">自提门店</div>
        <div class="pickup-store">
          <div class="store-info">
            <div class="store-name">DrugMall医药连锁(朝阳门店)</div>
            <div class="store-address">
              <el-icon><Location /></el-icon>
              <span>北京市朝阳区建国路88号</span>
            </div>
            <div class="store-hours">
              <el-icon><Clock /></el-icon>
              <span>营业时间：09:00-21:00</span>
            </div>
          </div>
          <div class="store-phone">
            <el-icon><Phone /></el-icon>
          </div>
        </div>
        <div class="pickup-notice">
          <el-icon><InfoFilled /></el-icon>
          <span>下单后我们会发送取货码到您的手机，请凭取货码到门店自提</span>
        </div>
      </div>

      <!-- 商品清单 -->
      <div class="section">
        <div class="section-title">
          商品清单
          <span class="subtitle">共{{ checkoutItems.length }}件</span>
        </div>
        <div class="item-list">
          <div v-for="item in checkoutItems" :key="item.id" class="item">
            <img :src="item.image" :alt="item.name" class="item-image" />
            <div class="item-info">
              <h4 class="item-name">
                <span v-if="item.isRx" class="rx-tag">Rx</span>
                {{ item.name }}
              </h4>
              <p class="item-spec">{{ item.specification }}</p>
              <p class="item-manufacturer">{{ item.manufacturer }}</p>
            </div>
            <div class="item-price-qty">
              <span class="price">¥{{ item.price.toFixed(2) }}</span>
              <span class="qty">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 优惠券 -->
      <div class="section">
        <div class="section-title">优惠券</div>
        <el-select v-model="selectedCoupon" placeholder="选择优惠券" style="width: 100%">
          <el-option
            v-for="coupon in availableCoupons"
            :key="coupon.id"
            :label="coupon.id ? `${coupon.name}${goodsTotal >= coupon.minAmount ? ' (可用)' : ` (满${coupon.minAmount}可用)`}` : coupon.name"
            :value="coupon.id"
            :disabled="coupon.id !== '' && goodsTotal < coupon.minAmount"
          />
        </el-select>
      </div>

      <!-- 订单备注 -->
      <div class="section">
        <div class="section-title">订单备注</div>
        <el-input
          v-model="remark"
          type="textarea"
          :rows="2"
          placeholder="请输入订单备注，如特殊配送要求等（选填）"
          maxlength="100"
          show-word-limit
        />
      </div>

      <!-- 金额明细 -->
      <div class="section amount-section">
        <div class="section-title">金额明细</div>
        <div class="amount-list">
          <div class="amount-row">
            <span class="label">商品总额</span>
            <span class="value">¥{{ goodsTotal.toFixed(2) }}</span>
          </div>
          <div class="amount-row">
            <span class="label">运费</span>
            <span class="value" :class="{ free: deliveryFee === 0 }">
              {{ deliveryFee === 0 ? '免运费' : `¥${deliveryFee.toFixed(2)}` }}
            </span>
          </div>
          <div v-if="discountAmount > 0" class="amount-row discount">
            <span class="label">优惠</span>
            <span class="value discount">-¥{{ discountAmount.toFixed(2) }}</span>
          </div>
          <div class="amount-row total">
            <span class="label">应付总额</span>
            <span class="value total-price">¥{{ payableAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部提交栏 -->
    <div class="submit-bar">
      <div class="submit-info">
        <span class="total-label">应付：</span>
        <span class="total-price">¥{{ payableAmount.toFixed(2) }}</span>
      </div>
      <button
        class="submit-btn"
        :disabled="checkoutItems.length === 0 || submitting || (deliveryType.value !== 'self_pickup' && !selectedAddress)"
        @click="submitOrder"
      >
        <el-icon v-if="submitting" class="is-loading"><Loading /></el-icon>
        <span>{{ submitting ? '提交中...' : '提交订单' }}</span>
      </button>
    </div>

    <!-- 地址选择弹窗 -->
    <el-dialog
      v-model="addressDialogVisible"
      title="选择收货地址"
      width="90%"
      :close-on-click-modal="false"
      class="address-dialog"
    >
      <div v-if="!isAddingAddress" class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="address-item"
          :class="{ active: selectedAddress?.id === addr.id }"
          @click="selectAddress(addr)"
        >
          <div class="address-item-content">
            <div class="address-header">
              <span class="name">{{ addr.name }}</span>
              <span class="phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault" size="small" type="success">默认</el-tag>
              <el-tag v-if="addr.tag" size="small" type="info">{{ addr.tag }}</el-tag>
            </div>
            <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
          </div>
          <div class="address-actions">
            <el-radio :model-value="selectedAddress?.id === addr.id" :label="true">
              <span class="sr-only">选择</span>
            </el-radio>
            <el-button type="danger" link size="small" @click="deleteAddress(addr, $event)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 新增地址表单 -->
      <div v-else class="add-address-form">
        <el-form :model="newAddressForm" label-width="80px">
          <el-form-item label="收货人" required>
            <el-input v-model="newAddressForm.name" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号" required>
            <el-input v-model="newAddressForm.phone" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="所在地区" required>
            <el-input v-model="newAddressForm.province" placeholder="省" style="width: 30%; margin-right: 5%" />
            <el-input v-model="newAddressForm.city" placeholder="市" style="width: 30%; margin-right: 5%" />
            <el-input v-model="newAddressForm.district" placeholder="区/县" style="width: 30%" />
          </el-form-item>
          <el-form-item label="详细地址" required>
            <el-input v-model="newAddressForm.detail" type="textarea" :rows="2" placeholder="请输入详细地址，如街道、门牌号等" />
          </el-form-item>
          <el-form-item label="地址标签">
            <el-radio-group v-model="newAddressForm.tag">
              <el-radio label="家">家</el-radio>
              <el-radio label="公司">公司</el-radio>
              <el-radio label="学校">学校</el-radio>
              <el-radio label="">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="newAddressForm.isDefault">设为默认地址</el-checkbox>
          </el-form-item>
        </el-form>
      </div>

      <template v-slot:footer>
        <div v-if="!isAddingAddress" class="dialog-footer">
          <el-button type="primary" @click="openAddAddress">
            <el-icon><Plus /></el-icon>新增地址
          </el-button>
          <div>
            <el-button @click="addressDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="addressDialogVisible = false">确定</el-button>
          </div>
        </div>
        <div v-else class="dialog-footer">
          <el-button @click="cancelAddAddress">取消</el-button>
          <el-button type="primary" @click="saveNewAddress">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.order-confirm-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(100px + $safe-area-bottom);
}

// 头部
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: $bg-white;
  border-bottom: 1px solid $border-light;
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s ease;

    &:hover {
      background: $bg-gray;
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
    color: $text-primary;
  }

  .placeholder {
    width: 36px;
  }
}

// 内容区
.content {
  padding: $spacing-md;
}

// 地址区域
.address-section {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-lg;
  background: $bg-white;
  border-radius: $radius-lg;
  margin-bottom: $spacing-md;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    box-shadow: $shadow-sm;
  }

  .address-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba($primary, 0.1);
    border-radius: 50%;
    color: $primary;
    font-size: 20px;
  }

  .address-info {
    flex: 1;

    .address-header {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-xs;

      .name {
        font-size: $font-md;
        font-weight: 500;
        color: $text-primary;
      }

      .phone {
        font-size: $font-sm;
        color: $text-secondary;
      }

      .tag {
        padding: 2px 6px;
        background: rgba($primary, 0.1);
        color: $primary;
        font-size: 10px;
        border-radius: $radius-sm;
      }
    }

    .address-detail {
      font-size: $font-sm;
      color: $text-secondary;
      line-height: 1.5;
    }
  }

  .no-address {
    flex: 1;
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    font-size: $font-md;
    color: $primary;

    .el-icon {
      font-size: 20px;
    }
  }

  .arrow {
    color: $text-tertiary;
  }
}

// 通用section
.section {
  padding: $spacing-lg;
  background: $bg-white;
  border-radius: $radius-lg;
  margin-bottom: $spacing-md;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: $spacing-md;

    .subtitle {
      font-size: $font-sm;
      color: $text-secondary;
      font-weight: normal;
      margin-left: $spacing-sm;
    }
  }
}

// 配送方式
.delivery-type {
  display: flex;
  gap: $spacing-md;
  flex-wrap: wrap;

  .type-item {
    flex: 1;
    min-width: 80px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-md;
    border: 1px solid $border-light;
    border-radius: $radius-md;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;

    .el-icon {
      font-size: 24px;
      color: $text-tertiary;
    }

    span {
      font-size: $font-sm;
      color: $text-secondary;
    }

    .type-tag {
      position: absolute;
      top: -1px;
      right: -1px;
      background: $error;
      color: $text-white;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 0 $radius-md 0 $radius-md;
    }

    &:hover {
      border-color: $primary;
    }

    &.active {
      border-color: $primary;
      background: rgba($primary, 0.05);

      .el-icon,
      span {
        color: $primary;
      }
    }
  }
}

// 自提门店
.pickup-section {
  .pickup-store {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: $spacing-md;
    background: $bg-gray;
    border-radius: $radius-md;
    margin-bottom: $spacing-md;

    .store-info {
      flex: 1;

      .store-name {
        font-size: $font-md;
        font-weight: 500;
        color: $text-primary;
        margin-bottom: $spacing-xs;
      }

      .store-address,
      .store-hours {
        display: flex;
        align-items: center;
        gap: $spacing-xs;
        font-size: $font-sm;
        color: $text-secondary;
        margin-top: $spacing-xs;

        .el-icon {
          font-size: 14px;
          color: $primary;
        }
      }
    }

    .store-phone {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: $primary;
      border-radius: 50%;
      color: $text-white;
      cursor: pointer;
      transition: opacity 0.2s;

      &:hover {
        opacity: 0.9;
      }
    }
  }

  .pickup-notice {
    display: flex;
    align-items: flex-start;
    gap: $spacing-xs;
    padding: $spacing-sm;
    background: rgba($warning, 0.1);
    border-radius: $radius-sm;
    font-size: $font-sm;
    color: $text-secondary;

    .el-icon {
      margin-top: 2px;
      color: $warning;
    }
  }
}

// 商品列表
.item-list {
  .item {
    display: flex;
    gap: $spacing-md;
    padding: $spacing-md 0;
    border-bottom: 1px solid $border-light;

    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }

    &:first-child {
      padding-top: 0;
    }

    .item-image {
      width: 80px;
      height: 80px;
      border-radius: $radius-md;
      object-fit: cover;
      background: $bg-gray;
    }

    .item-info {
      flex: 1;
      min-width: 0;

      .item-name {
        font-size: $font-md;
        font-weight: 500;
        color: $text-primary;
        margin-bottom: $spacing-xs;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        .rx-tag {
          display: inline-block;
          background: $error;
          color: $text-white;
          font-size: 10px;
          padding: 1px 4px;
          border-radius: $radius-sm;
          margin-right: $spacing-xs;
        }
      }

      .item-spec {
        font-size: $font-sm;
        color: $text-tertiary;
        margin-bottom: $spacing-xs;
      }

      .item-manufacturer {
        font-size: $font-sm;
        color: $text-tertiary;
      }
    }

    .item-price-qty {
      text-align: right;

      .price {
        display: block;
        font-size: $font-md;
        font-weight: 500;
        color: $text-primary;
        margin-bottom: $spacing-xs;
      }

      .qty {
        font-size: $font-sm;
        color: $text-tertiary;
      }
    }
  }
}

// 金额区域
.amount-section {
  .amount-list {
    .amount-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $spacing-sm 0;
      font-size: $font-sm;

      &:not(:last-child) {
        border-bottom: 1px dashed $border-light;
      }

      .label {
        color: $text-secondary;
      }

      .value {
        color: $text-primary;

        &.free {
          color: $success;
        }

        &.discount {
          color: $error;
        }

        &.total-price {
          font-size: $font-xl;
          font-weight: bold;
          color: $error;
        }
      }

      &.total {
        padding-top: $spacing-md;
        margin-top: $spacing-sm;
        border-top: 1px solid $border-light;
        font-size: $font-md;
        font-weight: 500;
      }
    }
  }
}

// 提交栏
.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  padding: $spacing-sm $spacing-md;
  padding-bottom: calc($safe-area-bottom + $spacing-sm);
  background: $bg-white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;

  .submit-info {
    flex: 1;
    text-align: right;
    margin-right: $spacing-md;

    .total-label {
      font-size: $font-sm;
      color: $text-primary;
    }

    .total-price {
      font-size: 20px;
      font-weight: bold;
      color: $error;
    }
  }

  .submit-btn {
    min-width: 130px;
    height: 44px;
    padding: 0 $spacing-xl;
    background: linear-gradient(135deg, $primary 0%, lighten($primary, 10%) 100%);
    color: $text-white;
    font-size: $font-md;
    font-weight: 500;
    border: none;
    border-radius: $radius-xl;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;

    &:hover:not(:disabled) {
      opacity: 0.9;
      transform: translateY(-1px);
    }

    &:disabled {
      background: $text-tertiary;
      cursor: not-allowed;
      opacity: 0.6;
    }
  }
}

// 地址选择弹窗样式
:deep(.address-dialog) {
  .el-dialog__body {
    padding: $spacing-md;
    max-height: 60vh;
    overflow-y: auto;
  }

  .address-list {
    .address-item {
      display: flex;
      align-items: center;
      padding: $spacing-md;
      border: 1px solid $border-light;
      border-radius: $radius-md;
      margin-bottom: $spacing-md;
      cursor: pointer;
      transition: all 0.2s;

      &:hover,
      &.active {
        border-color: $primary;
        background: rgba($primary, 0.02);
      }

      .address-item-content {
        flex: 1;
        min-width: 0;

        .address-header {
          display: flex;
          align-items: center;
          gap: $spacing-sm;
          margin-bottom: $spacing-xs;

          .name {
            font-weight: 500;
            color: $text-primary;
          }

          .phone {
            color: $text-secondary;
            font-size: $font-sm;
          }
        }

        .address-detail {
          color: $text-secondary;
          font-size: $font-sm;
          line-height: 1.5;
        }
      }
    }
  }
}
</style>
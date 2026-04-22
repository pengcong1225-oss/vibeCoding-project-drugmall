<template>
  <div class="banner-subsidy-section">
    <!-- 左侧百亿补贴 -->
    <div class="subsidy-section">
      <div class="subsidy-header">
        <span class="subsidy-title">百亿补贴</span>
        <span class="subsidy-tag">抢大额券</span>
      </div>
      <div class="subsidy-list">
        <div
          v-for="product in subsidyProducts.slice(0, 3)"
          :key="product.id"
          class="subsidy-item"
          @click="handleProductClick(product)"
        >
          <div class="item-img">
            <img v-if="product.imageUrl" :src="product.imageUrl" class="product-thumb" :alt="product.name" />
            <span v-else class="placeholder-text">{{ product.name.slice(0, 2) }}</span>
          </div>
          <div class="item-info">
            <div class="item-name">{{ product.name }}</div>
            <div class="item-prices">
              <span class="item-price">¥{{ product.price }}</span>
              <span class="item-original" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧Banner -->
    <div class="banner-section">
      <div class="banner-swiper">
        <div
          class="banner-track"
          :style="{ transform: `translateX(-${currentBannerIndex * 100}%)` }"
        >
          <div
            v-for="(banner, index) in banners"
            :key="index"
            class="banner-item"
            @click="handleBannerClick(banner)"
          >
            <img 
              v-if="banner.imageUrl" 
              :src="banner.imageUrl" 
              class="banner-bg-image"
              :alt="banner.title"
            />
            <div v-else class="banner-bg" :style="{ background: banner.bgGradient }"></div>
            <div class="banner-content">
              <div class="banner-text">
                <h3 class="banner-title">{{ banner.title }}</h3>
                <p class="banner-subtitle">{{ banner.subtitle }}</p>
                <div class="banner-btn" v-if="banner.btnText">{{ banner.btnText }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="banner-dots">
          <span
            v-for="(_, index) in banners"
            :key="index"
            class="dot"
            :class="{ active: currentBannerIndex === index }"
            @click="goToBanner(index)"
          ></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentBannerIndex = ref(0)

// Banner数据
const banners = ref([
  {
    title: '治养结合 科学护胃',
    subtitle: '限时领8元补贴',
    imageUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01QJ0W3T1J7dR5yT8z1_!!6000000000980-0-tps-400-400.jpg',
    link: '/activity/stomach'
  },
  {
    title: '春季过敏专区',
    subtitle: '抗过敏药5折起',
    imageUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01L8v1tZ1Xl6K3y8d8g_!!6000000002969-0-tps-400-400.jpg',
    link: '/activity/allergy'
  },
  {
    title: '慢病管理',
    subtitle: '处方药专属优惠',
    imageUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-0-tps-400-400.jpg',
    link: '/activity/chronic'
  }
])

// 百亿补贴商品数据
const subsidyProducts = ref([
  { id: '1', name: '布地奈德鼻喷雾剂', price: 49.0, originalPrice: 82.0, imageUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01QJ0W3T1J7dR5yT8z1_!!6000000000980-2-tps-100-100.png' },
  { id: '2', name: '薇诺娜清透防晒乳', price: 59.0, originalPrice: 69.0, imageUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01L8v1tZ1Xl6K3y8d8g_!!6000000002969-2-tps-100-100.png' },
  { id: '3', name: '【超声波洗牙洁...', price: 29.2, originalPrice: 38.0, imageUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-2-tps-100-100.png' }
])

let bannerTimer: ReturnType<typeof setInterval>

const startBannerAutoPlay = () => {
  bannerTimer = setInterval(() => {
    currentBannerIndex.value = (currentBannerIndex.value + 1) % banners.value.length
  }, 4000)
}

const goToBanner = (index: number) => {
  currentBannerIndex.value = index
  clearInterval(bannerTimer)
  startBannerAutoPlay()
}

const handleBannerClick = (banner: any) => {
  if (banner.link) {
    try {
      router.push(banner.link)
    } catch (error) {
      ElMessage.info(`${banner.title} - 活动即将上线`)
    }
  } else {
    ElMessage.info(`${banner.title} - 活动即将上线`)
  }
}

const handleProductClick = (product: any) => {
  if (product.id) {
    try {
      router.push(`/drug/${product.id}`)
    } catch (error) {
      ElMessage.info(`即将查看${product.name}`)
    }
  } else {
    ElMessage.warning('商品信息不完整')
  }
}

onMounted(() => {
  startBannerAutoPlay()
})

onUnmounted(() => {
  clearInterval(bannerTimer)
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.banner-subsidy-section {
  display: flex;
  gap: 8px;
  padding: 0 12px;
  margin-bottom: 10px;

  .subsidy-section {
    flex: 0 0 55%;
    background: #fff;
    border-radius: 12px;
    padding: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .subsidy-header {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 8px;

      .subsidy-title {
        font-size: 16px;
        font-weight: 700;
        color: $text-primary;
      }

      .subsidy-tag {
        background: linear-gradient(90deg, $primary 0%, $primary-light 100%);
        color: #fff;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;
      }
    }

    .subsidy-list {
      display: flex;
      flex-direction: column;
      gap: 6px;

      .subsidy-item {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;

        .item-img {
          width: 48px;
          height: 48px;
          border-radius: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          overflow: hidden;
          background: #f5f5f5;

          .product-thumb {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .placeholder-text {
            font-size: 12px;
            color: $text-tertiary;
          }
        }

        .item-info {
          flex: 1;
          min-width: 0;

          .item-name {
            font-size: 13px;
            color: $text-primary;
            margin-bottom: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .item-prices {
            display: flex;
            align-items: baseline;
            gap: 6px;

            .item-price {
              font-size: 15px;
              font-weight: 700;
              color: $price-red;
            }

            .item-original {
              font-size: 11px;
              color: $text-tertiary;
              text-decoration: line-through;
            }
          }
        }
      }
    }
  }

  .banner-section {
    flex: 0 0 45%;

    .banner-swiper {
      position: relative;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      aspect-ratio: 1 / 1.1;

      .banner-track {
        display: flex;
        height: 100%;
        transition: transform 0.4s ease;
      }

      .banner-item {
        flex-shrink: 0;
        width: 100%;
        height: 100%;
        position: relative;
        cursor: pointer;

        .banner-bg-image {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          object-fit: cover;
          object-position: center;
        }

        .banner-bg {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
        }

        .banner-content {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          padding: 12px;
          background: linear-gradient(0deg, rgba(0,0,0,0.6) 0%, rgba(0,0,0,0) 100%);

          .banner-text {
            color: #fff;

            .banner-title {
              font-size: 14px;
              font-weight: 600;
              margin: 0 0 4px;
              line-height: 1.3;
            }

            .banner-subtitle {
              font-size: 11px;
              opacity: 0.9;
              margin-bottom: 6px;
            }

            .banner-btn {
              display: inline-block;
              background: $primary;
              color: #fff;
              padding: 3px 10px;
              border-radius: 10px;
              font-size: 11px;
              font-weight: 500;
            }
          }
        }
      }

      .banner-dots {
        position: absolute;
        bottom: 6px;
        left: 50%;
        transform: translateX(-50%);
        display: flex;
        gap: 4px;

        .dot {
          width: 5px;
          height: 5px;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.5);
          cursor: pointer;

          &.active {
            width: 12px;
            border-radius: 3px;
            background: #fff;
          }
        }
      }
    }
  }
}
</style>

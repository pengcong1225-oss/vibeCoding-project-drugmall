<template>
  <div class="tcm-banner-section">
    <el-carousel height="180px" :interval="3000" arrow="never" indicator-position="outside">
      <el-carousel-item v-for="(slide, index) in bannerSlides" :key="index" @click="handleSlideClick(slide)">
        <div class="slide-wrapper">
          <img :src="slide.image" :alt="slide.title" class="slide-bg" />
          <div class="slide-overlay"></div>
          <div class="slide-content">
            <div class="slide-tag">{{ slide.tag }}</div>
            <h3 class="slide-title">{{ slide.title }}</h3>
            <p class="slide-subtitle">{{ slide.subtitle }}</p>
            <div class="slide-price-row">
              <span class="original-price" v-if="slide.originalPrice">¥{{ slide.originalPrice }}</span>
              <div class="discount-price">
                <span class="price-symbol">¥</span>
                <span class="price-num">{{ slide.price }}</span>
                <span class="price-tag">{{ slide.priceTag }}</span>
              </div>
            </div>
          </div>
          <div class="delivery-badge" v-if="slide.deliveryTime">
            <span class="time">{{ slide.deliveryTime }}分钟达</span>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const bannerSlides = ref([
  {
    tag: '东阿阿胶',
    title: '东阿阿胶复方阿胶浆无蔗糖48支',
    subtitle: '{ 马上补气血 健康伴团圆 }',
    originalPrice: '333',
    price: '260',
    priceTag: '优惠价',
    deliveryTime: '26',
    image: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&h=300&fit=crop',
    link: '/drug/1'
  },
  {
    tag: '限时特惠',
    title: '汤臣倍健蛋白粉增强免疫力',
    subtitle: '{ 每天一杯 活力满满 }',
    originalPrice: '298',
    price: '198',
    priceTag: '限时价',
    deliveryTime: '30',
    image: 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=600&h=300&fit=crop',
    link: '/drug/2'
  },
  {
    tag: '新品上市',
    title: 'Swisse钙片维生素D柠檬酸钙',
    subtitle: '{ 澳洲进口 品质保证 }',
    originalPrice: '168',
    price: '128',
    priceTag: '新品价',
    deliveryTime: '25',
    image: 'https://images.unsplash.com/photo-1626285861696-9f0bf5a49c6d?w=600&h=300&fit=crop',
    link: '/drug/3'
  }
])

const handleSlideClick = (slide: any) => {
  if (slide.link) {
    window.location.href = slide.link
  }
}
</script>

<style scoped lang="scss">
.tcm-banner-section {
  width: 100%;
  padding: 0;

  :deep(.el-carousel) {
    .el-carousel__indicators {
      bottom: 12px;
      left: 50%;
      transform: translateX(-50%);

      .el-carousel__indicator {
        padding: 0 4px;

        .el-carousel__button {
          width: 6px;
          height: 6px;
          background-color: rgba(255, 255, 255, 0.5);
          border-radius: 50%;
        }

        &.is-active .el-carousel__button {
          background-color: #fff;
        }
      }
    }

    .el-carousel__container {
      height: 180px;
    }
  }

  .slide-wrapper {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: hidden;
    cursor: pointer;
  }

  .slide-bg {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .slide-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(90deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.3) 40%, transparent 100%);
  }

  .slide-content {
    position: absolute;
    top: 50%;
    left: 16px;
    transform: translateY(-50%);
    z-index: 2;
    color: #fff;
    max-width: 65%;

    .slide-tag {
      display: inline-block;
      background: rgba(255, 255, 255, 0.25);
      backdrop-filter: blur(4px);
      color: #fff;
      font-size: 11px;
      padding: 3px 10px;
      border-radius: 12px;
      margin-bottom: 10px;
    }

    .slide-title {
      font-size: 18px;
      font-weight: bold;
      line-height: 1.4;
      margin-bottom: 6px;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
    }

    .slide-subtitle {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 12px;
    }

    .slide-price-row {
      display: flex;
      align-items: center;
      gap: 8px;

      .original-price {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.7);
        text-decoration: line-through;
      }

      .discount-price {
        display: flex;
        align-items: baseline;
        background: linear-gradient(90deg, #FF6B6B 0%, #FF4D4F 100%);
        padding: 4px 10px;
        border-radius: 14px;
        color: #fff;

        .price-symbol {
          font-size: 11px;
        }

        .price-num {
          font-size: 18px;
          font-weight: bold;
        }

        .price-tag {
          font-size: 10px;
          margin-left: 2px;
        }
      }
    }
  }

  .delivery-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: linear-gradient(135deg, #FF9500 0%, #FFB800 100%);
    padding: 4px 10px;
    border-radius: 14px;
    z-index: 3;
    box-shadow: 0 2px 8px rgba(255, 149, 0, 0.4);

    .time {
      color: #fff;
      font-size: 11px;
      font-weight: 600;
    }
  }
}
</style>

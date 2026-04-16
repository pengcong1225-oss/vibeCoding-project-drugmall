<template>
  <div class="tcm-banner-section">
    <el-carousel height="200px" :interval="3000" arrow="never" indicator-position="outside">
      <el-carousel-item v-for="(slide, index) in bannerSlides" :key="index" @click="handleSlideClick(slide)">
        <div class="slide-wrapper">
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
          <div class="slide-image-wrapper">
            <img :src="slide.image" :alt="slide.title" class="slide-image" />
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
    image: '/images/商品1.jpeg',
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
    image: '/images/商品2.jpg',
    link: '/drug/2'
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
      position: absolute;
      z-index: 10;

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
      height: 200px;
    }
  }

  .slide-wrapper {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: hidden;
    cursor: pointer;
    display: flex;
    align-items: center;
    padding: 0 16px;
    background: transparent;
  }

  .slide-content {
    flex: 1;
    z-index: 2;
    color: #333;
    max-width: 60%;

    .slide-tag {
      display: inline-block;
      background: rgba(74, 144, 226, 0.15);
      color: #4A90E2;
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
      color: #333;
    }

    .slide-subtitle {
      font-size: 12px;
      color: #666;
      margin-bottom: 12px;
    }

    .slide-price-row {
      display: flex;
      align-items: center;
      gap: 8px;

      .original-price {
        font-size: 12px;
        color: #999;
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

  .slide-image-wrapper {
    position: absolute;
    right: 60px;
    top: 50%;
    transform: translateY(-50%);
    z-index: 1;
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;

    .slide-image {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
      border-radius: 8px;
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

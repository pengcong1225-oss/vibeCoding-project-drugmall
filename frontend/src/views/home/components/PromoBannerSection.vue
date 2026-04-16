<template>
  <div class="promo-banner-section">
    <!-- 左侧：过敏报告 -->
    <div class="promo-left" @click="handleLeftClick">
      <div class="promo-card allergy-report">
        <div class="promo-content">
          <div class="promo-avatar">
            <el-icon :size="26"><Female /></el-icon>
          </div>
          <div class="promo-tag">过敏报告</div>
        </div>
      </div>
    </div>

    <!-- 中间：主促销横幅 -->
    <div class="promo-center" @click="handleCenterClick">
      <div class="promo-card main-banner">
        <div class="banner-content">
          <div class="brand-tag">
            <span class="brand-text">美团买药</span>
          </div>
          <h3 class="promo-title">先维盈全球首发</h3>
          <div class="promo-subtitle">不瘦必赔</div>
        </div>
        <div class="promo-product">
          <div class="product-image">
            <div class="pill-bottle">
              <div class="bottle-cap"></div>
              <div class="bottle-body">
                <div class="bottle-label">Rx</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧：过敏好物 -->
    <div class="promo-right" @click="handleRightClick">
      <div class="promo-card allergy-product">
        <div class="promo-content">
          <div class="product-showcase">
            <div class="medicine-box">
              <div class="box-front">
                <span class="cross-icon">+</span>
              </div>
            </div>
          </div>
          <div class="promo-tag yellow">过敏好物</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Female } from '@element-plus/icons-vue'

const emit = defineEmits<{
  (e: 'leftClick'): void
  (e: 'centerClick'): void
  (e: 'rightClick'): void
}>()

const handleLeftClick = () => emit('leftClick')
const handleCenterClick = () => emit('centerClick')
const handleRightClick = () => emit('rightClick')
</script>

<style scoped lang="scss">
$primary-yellow: #FFD100;
$text-primary: #333333;
$text-secondary: #666666;

.promo-banner-section {
  display: flex;
  gap: 8px;
  padding: 0 12px;
  margin-bottom: 12px;

  .promo-left,
  .promo-right {
    flex: 0 0 80px;
  }

  .promo-center {
    flex: 1;
  }

  .promo-card {
    height: 80px;
    border-radius: 14px;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.2s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    &:active {
      transform: scale(0.98);
    }
  }

  // 左侧过敏报告
  .allergy-report {
      background: linear-gradient(135deg, #FF9AA2 0%, #FFB7B2 100%);
      position: relative;

      .promo-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
        padding: 8px;
        gap: 4px;
      }

      .promo-avatar {
        width: 42px;
        height: 42px;
        border-radius: 50%;
        overflow: hidden;
        background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        box-shadow: 0 3px 8px rgba(255, 107, 107, 0.4);
      }

      .promo-tag {
        background: #FFF;
        color: #FF6B6B;
        font-size: 10px;
        font-weight: 600;
        padding: 2px 6px;
        border-radius: 8px;
        line-height: 1.2;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      }
    }

  // 中间主横幅
  .main-banner {
    background: linear-gradient(135deg, #0E7490 0%, #0891B2 100%);
    position: relative;
    padding: 12px 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .banner-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 4px;
    }

    .brand-tag {
      display: flex;
      align-items: center;
      gap: 4px;

      .brand-text {
        font-size: 10px;
        color: rgba(255, 255, 255, 0.9);
        font-weight: 500;
      }
    }

    .promo-title {
      font-size: 18px;
      font-weight: 700;
      color: #fff;
      margin: 0;
      line-height: 1.2;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }

    .promo-subtitle {
      display: inline-block;
      background: #22D3EE;
      color: #fff;
      font-size: 10px;
      font-weight: 600;
      padding: 3px 8px;
      border-radius: 8px;
      width: fit-content;
    }

    .promo-product {
      width: auto;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: flex-end;

      .product-image {
        display: flex;
        align-items: flex-end;
        justify-content: center;
        padding-bottom: 0;

        .pill-bottle {
          position: relative;
          width: 60px;
          height: 78px;

          .bottle-cap {
            width: 26px;
            height: 12px;
            background: linear-gradient(180deg, #F5F5F5 0%, #E0E0E0 100%);
            border-radius: 3px 3px 0 0;
            margin: 0 auto;
            border: 1px solid #CCCCCC;
          }

          .bottle-body {
            width: 56px;
            height: 60px;
            background: linear-gradient(180deg, #FFFFFF 0%, #F5F5F5 100%);
            border-radius: 6px;
            border: 1px solid #E0E0E0;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.12);
            margin-top: -1px;

            .bottle-label {
              font-size: 18px;
              font-weight: bold;
              color: #FF5722;
            }
          }
        }
      }
    }
  }

  // 右侧过敏好物
  .allergy-product {
    background: linear-gradient(135deg, #A8E6CF 0%, #88D8A3 100%);
    position: relative;

    .promo-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      padding: 8px;
      gap: 4px;
    }

    .product-showcase {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;

      .medicine-box {
        width: 44px;
        height: 44px;
        position: relative;

        .box-front {
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%);
          border-radius: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 3px 8px rgba(76, 175, 80, 0.4);
          position: relative;

          .cross-icon {
            font-size: 24px;
            color: #fff;
            font-weight: bold;
            line-height: 1;
          }
        }
      }
    }

    .promo-tag {
      background: #fff;
      color: #4CAF50;
      font-size: 10px;
      font-weight: 600;
      padding: 2px 6px;
      border-radius: 8px;
      line-height: 1.2;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

      &.yellow {
        background: #fff;
        color: #4CAF50;
      }
    }
  }
}
</style>

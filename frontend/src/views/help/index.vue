<template>
  <div class="help-page">
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">帮助中心</span>
    </div>

    <div class="help-content">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索常见问题"
          :prefix-icon="Search"
          clearable
        />
      </div>

      <div v-for="(group, index) in filteredFaqGroups" :key="index" class="faq-group">
        <h3 class="group-title">{{ group.title }}</h3>
        <el-collapse v-model="activeNames">
          <el-collapse-item
            v-for="faq in group.items"
            :key="faq.id"
            :title="faq.question"
            :name="faq.id"
          >
            <p class="answer-text">{{ faq.answer }}</p>
          </el-collapse-item>
        </el-collapse>
      </div>

      <div class="contact-section">
        <h3 class="section-title">联系我们</h3>
        <div class="contact-list">
          <div class="contact-item">
            <el-icon><Phone /></el-icon>
            <div class="contact-info">
              <span class="contact-label">客服热线</span>
              <span class="contact-value">400-123-4567</span>
            </div>
          </div>
          <div class="contact-item">
            <el-icon><Clock /></el-icon>
            <div class="contact-info">
              <span class="contact-label">服务时间</span>
              <span class="contact-value">每天 08:00 - 22:00</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Search, Phone, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const activeNames = ref<string[]>([])

const faqGroups = [
  {
    title: '购药相关',
    items: [
      { id: 'buy-1', question: '如何购买处方药？', answer: '购买处方药需要先上传有效处方或通过在线问诊获取电子处方，处方经药师审核通过后方可购买。' },
      { id: 'buy-2', question: '药品支持退换货吗？', answer: '药品属于特殊商品，拆封后不支持无理由退货。如收到的药品存在质量问题或发错药品，请在签收后24小时内联系客服处理。' },
      { id: 'buy-3', question: '如何查看药品有效期？', answer: '药品详情页会标注药品的有效期信息。所有在售药品均保证距有效期至少6个月以上。' }
    ]
  },
  {
    title: '配送相关',
    items: [
      { id: 'ship-1', question: '配送范围和时间？', answer: '目前支持全国大部分地区配送，同城订单最快30分钟送达，异地订单一般1-3个工作日到达。' },
      { id: 'ship-2', question: '运费如何计算？', answer: '订单满49元免运费，未满49元收取6元配送费。急速达服务另加收配送费用。' }
    ]
  },
  {
    title: '账户相关',
    items: [
      { id: 'acc-1', question: '如何进行实名认证？', answer: '进入个人中心 > 设置 > 实名认证，输入真实姓名和身份证号进行认证。购买处方药前需完成实名认证。' },
      { id: 'acc-2', question: '如何修改收货地址？', answer: '进入个人中心 > 收货地址，可以新增、编辑或删除收货地址，也可以设置默认地址。' }
    ]
  }
]

const filteredFaqGroups = computed(() => {
  if (!searchKeyword.value.trim()) return faqGroups
  const keyword = searchKeyword.value.trim().toLowerCase()
  return faqGroups
    .map(group => ({
      ...group,
      items: group.items.filter(
        item =>
          item.question.toLowerCase().includes(keyword) ||
          item.answer.toLowerCase().includes(keyword)
      )
    }))
    .filter(group => group.items.length > 0)
})

const goBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.help-page {
  min-height: 100vh;
  background: $bg-primary;
}

.nav-header {
  display: flex;
  align-items: center;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: $text-white;
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
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
    flex: 1;
    text-align: center;
    margin-right: 36px;
  }
}

.help-content {
  padding: $spacing-md;
}

.search-box {
  margin-bottom: $spacing-lg;
}

.faq-group {
  margin-bottom: $spacing-lg;

  .group-title {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 $spacing-md 0;
    padding-left: $spacing-xs;
    border-left: 3px solid $primary;
  }

  .answer-text {
    font-size: $font-sm;
    color: $text-secondary;
    line-height: 1.6;
    margin: 0;
  }
}

.contact-section {
  margin-top: $spacing-xl;
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  box-shadow: $shadow-sm;

  .section-title {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 $spacing-md 0;
  }

  .contact-list {
    .contact-item {
      display: flex;
      align-items: center;
      gap: $spacing-md;
      padding: $spacing-md 0;
      border-bottom: 1px solid $border-light;

      &:last-child {
        border-bottom: none;
      }

      .el-icon {
        font-size: 20px;
        color: $primary;
      }

      .contact-info {
        display: flex;
        flex-direction: column;
        gap: 2px;

        .contact-label {
          font-size: $font-sm;
          color: $text-tertiary;
        }

        .contact-value {
          font-size: $font-md;
          color: $text-primary;
          font-weight: 500;
        }
      }
    }
  }
}
</style>

<template>
  <div class="consultation-layout" :class="{ 'mobile': isMobile }">
    <!-- Toast通知容器 -->
    <Teleport to="body">
      <div class="toast-container">
        <TransitionGroup name="toast-anim">
          <div v-for="toast in toasts" :key="toast.id" :class="['toast-item', 'toast-' + toast.type]">
            <span class="toast-icon">{{ toastIcons[toast.type] }}</span>
            <span class="toast-msg">{{ toast.message }}</span>
            <button class="toast-close" @click="removeToast(toast.id)" aria-label="关闭">&times;</button>
          </div>
        </TransitionGroup>
      </div>
    </Teleport>

    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <button class="nav-back" @click="goBack" aria-label="返回">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path d="M15 18l-6-6 6-6" stroke="#111" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <div class="nav-title-area">
        <h1 class="nav-title">{{ patientInfo.name }}</h1>
        <span class="nav-status" :class="{ 'online': imConnected, 'offline': !imConnected }">
          {{ imConnected ? '已接通' : '连接中...' }}
        </span>
      </div>
      <button class="nav-end" @click="endConsultation" :disabled="consultationInfo.status === 'completed'">
        {{ consultationInfo.status === 'completed' ? '已结束' : '结束问诊' }}
      </button>
    </header>

    <!-- 三栏主体布局 -->
    <div class="main-container">
      <!-- 左侧：患者信息侧边栏 -->
      <aside class="patient-sidebar" :class="{ 'collapsed': sidebarCollapsed }">
        <button class="sidebar-toggle" @click="sidebarCollapsed = !sidebarCollapsed" v-if="isMobile">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M6 9l6 6 6-6" stroke="#666" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>

        <div class="patient-header">
          <img class="patient-avatar" :src="patientInfo.avatar" alt="患者头像" @error="$event.target.src = defaultAvatar" />
          <div class="patient-basic">
            <div class="patient-name">{{ patientInfo.name }}（{{ patientInfo.gender }} {{ patientInfo.age }}岁）</div>
            <div class="patient-tags">
              <span v-for="tag in patientInfo.tags" :key="tag" class="tag" :class="getTagClass(tag)">{{ tag }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <div class="info-title">过敏史</div>
          <div class="info-content">
            <template v-if="patientInfo.allergyHistory?.length">
              <span v-for="a in patientInfo.allergyHistory" :key="a" class="allergy-tag">{{ a }}</span>
            </template>
            <span v-else class="empty">无已知过敏</span>
          </div>
        </div>

        <div class="info-section">
          <div class="info-title">慢性病</div>
          <div class="info-content">
            <template v-if="patientInfo.chronicDiseases?.length">
              <span v-for="d in patientInfo.chronicDiseases" :key="d" class="disease-tag">{{ d }}</span>
            </template>
            <span v-else class="empty">无</span>
          </div>
        </div>

        <div class="info-section">
          <div class="info-title">本次问诊</div>
          <div class="inquiry-meta">
            <div class="meta-row">
              <span class="meta-label">类型：</span>
              <span class="meta-value">{{ inquiryInfo.type }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">提交：</span>
              <span class="meta-value">{{ inquiryInfo.submitTime }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">主诉：</span>
              <span class="meta-value">{{ inquiryInfo.chiefComplaint }}</span>
            </div>
          </div>
        </div>

        <button class="view-history-btn" @click="activeWorkspace = 'history'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          查看历史记录 ({{ patientInfo.historyCount }}次)
        </button>
      </aside>

      <!-- 中间：聊天/沟通区域 -->
      <main class="chat-area">
        <!-- 预问诊信息卡片 -->
        <div class="pre-inquiry-card" :class="{ collapsed: preInquiryCollapsed }">
          <div class="card-header" @click="preInquiryCollapsed = !preInquiryCollapsed">
            <span class="card-title">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
              </svg>
              预问诊信息
            </span>
            <span class="toggle-icon">{{ preInquiryCollapsed ? '▼' : '▲' }}</span>
          </div>
          <div class="card-body" v-show="!preInquiryCollapsed">
            <div class="info-row">
              <span class="label">主诉：</span>
              <span class="value">{{ preInquiry.chiefComplaint }}</span>
            </div>
            <div class="info-row">
              <span class="label">症状详情：</span>
              <div class="symptom-list">
                <div v-for="(item, idx) in preInquiry.symptoms" :key="idx" class="symptom-item">• {{ item }}</div>
              </div>
            </div>
            <div class="info-row">
              <span class="label">用药史：</span>
              <span class="value">{{ preInquiry.medicationHistory || '无' }}</span>
            </div>
            <div class="info-row">
              <span class="label">过敏史：</span>
              <span class="value allergy">{{ preInquiry.allergyHistory || '无' }}</span>
            </div>
            <div class="attachment-row" v-if="preInquiry.images?.length">
              <button class="view-images-btn" @click="viewImages">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                  <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                查看上传图片 {{ preInquiry.images.length }}张
              </button>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div ref="messageListRef" class="msg-list">
          <!-- 加载状态 -->
          <div v-if="loading" class="state-box state-loading">
            <div class="loading-spinner"></div>
            <p class="state-text">正在连接服务器...</p>
          </div>

          <!-- 错误状态 -->
          <div v-else-if="error" class="state-box state-error">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="#ff4d4f" stroke-width="2"/>
              <path d="M12 7v5M12 17h.01" stroke="#ff4d4f" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <p class="state-text state-text--error">{{ error }}</p>
            <button class="retry-btn" @click="retryInit">重新连接</button>
          </div>

          <!-- 消息内容 -->
          <template v-else>
            <div
              v-for="(msg, index) in messages"
              :key="msg.id || index"
              :class="['msg-row', 'msg-' + msg.role]"
            >
              <!-- 系统消息 -->
              <div v-if="msg.role === 'system'" class="sys-msg-wrap">
                <template v-if="msg.isCard && msg.cardData">
                  <div class="sys-card">
                    <div class="sys-card-head">
                      <span class="sys-card-icon">
                        <!-- 处方图标 -->
                        <svg v-if="msg.cardData.icon === 'prescription'" width="16" height="16" viewBox="0 0 24 24" fill="none">
                          <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2" stroke="currentColor" stroke-width="2"/>
                          <path d="M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="currentColor" stroke-width="2"/>
                        </svg>
                        <!-- 病历图标 -->
                        <svg v-else-if="msg.cardData.icon === 'record'" width="16" height="16" viewBox="0 0 24 24" fill="none">
                          <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="currentColor" stroke-width="2"/>
                        </svg>
                        <!-- 检查图标 -->
                        <svg v-else-if="msg.cardData.icon === 'exam'" width="16" height="16" viewBox="0 0 24 24" fill="none">
                          <path d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2z" stroke="currentColor" stroke-width="2"/>
                        </svg>
                        <!-- 警告图标 -->
                        <svg v-else-if="msg.cardData.icon === 'warning'" width="16" height="16" viewBox="0 0 24 24" fill="none">
                          <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" stroke="currentColor" stroke-width="2"/>
                        </svg>
                        <!-- 默认图标 -->
                        <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none">
                          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                          <path d="M12 8v4m0 4h.01" stroke="currentColor" stroke-width="2"/>
                        </svg>
                      </span>
                      <span class="sys-card-title">{{ msg.cardData.title }}</span>
                    </div>
                    <div class="sys-card-body" v-html="formatContent(msg.content)"></div>
                  </div>
                </template>
                <template v-else>
                  <span class="sys-text">{{ msg.content }}</span>
                </template>
              </div>

              <!-- 用户消息 -->
              <template v-else>
                <div class="avatar-circle">
                  <img :src="getAvatar(msg)" :alt="msg.role === 'doctor' ? '医生头像' : '患者头像'" @error="$event.target.src = defaultAvatar" />
                </div>
                <div class="bubble-col">
                  <div class="bubble" v-html="formatContent(msg.content)" @click="handleMessageClick"></div>
                  <span class="bubble-time">{{ msg.time }}</span>
                </div>
              </template>
            </div>

            <!-- 空状态 -->
            <div v-if="messages.length === 0" class="state-box state-empty">
              <svg width="56" height="56" viewBox="0 0 24 24" fill="none">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" stroke="#b2b2b2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M8 10h8M8 14h4" stroke="#b2b2b2" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              <p class="state-text">暂无消息</p>
              <p class="state-subtext">等待患者发送第一条消息</p>
            </div>
          </template>
        </div>

        <!-- 快捷回复栏 -->
        <div class="quick-reply-bar" v-if="quickReplies.length && !inputFocused">
          <button v-for="(reply, idx) in quickReplies" :key="idx" class="quick-reply-btn" @click="sendQuickReply(reply)">
            {{ reply }}
          </button>
        </div>

        <!-- 底部输入区 -->
        <footer class="input-bar">
          <div class="toolbar-row">
            <!-- 语音按钮 -->
            <button class="tool-btn voice-btn" @click="handleVoiceClick" title="语音消息" aria-label="语音">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" style="display: block;">
                <path d="M12 1a3 3 0 00-3 3v8a3 3 0 006 0V4a3 3 0 00-3-3z" stroke="#2E7D32" stroke-width="2" fill="none"/>
                <path d="M19 10v2a7 7 0 01-14 0v-2" stroke="#2E7D32" stroke-width="2" stroke-linecap="round" fill="none"/>
                <line x1="12" y1="19" x2="12" y2="23" stroke="#2E7D32" stroke-width="2" stroke-linecap="round"/>
                <line x1="8" y1="23" x2="16" y2="23" stroke="#2E7D32" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>

            <!-- 输入框 -->
            <div class="input-wrap" :class="{ 'input-wrap--focus': inputFocused }">
              <textarea
                v-model="inputMessage"
                class="input-field"
                placeholder="请输入消息..."
                rows="1"
                :disabled="sending"
                @keydown.enter.exact.prevent="sendMessage"
                @input="autoResize"
                @focus="inputFocused = true"
                @blur="inputFocused = false"
              ></textarea>
            </div>

            <!-- 发送按钮/更多按钮 -->
            <button
              v-if="canSend"
              class="send-btn"
              :class="{ 'sending': sending }"
              :disabled="sending"
              @click="sendMessage"
              aria-label="发送消息"
            >
              <svg v-if="!sending" width="18" height="18" viewBox="0 0 24 24" fill="#fff">
                <path d="M22 2L11 13M22 2l-7 20-4-9-9-4z" stroke="#fff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <div v-else class="send-spin"></div>
            </button>
            <button v-else class="tool-btn more-btn" @click="showMoreMenu = true" title="更多功能" aria-label="更多功能">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="#2E7D32" stroke-width="2"/>
                <line x1="12" y1="8" x2="12" y2="16" stroke="#2E7D32" stroke-width="2"/>
                <line x1="8" y1="12" x2="16" y2="12" stroke="#2E7D32" stroke-width="2"/>
              </svg>
            </button>

            <!-- 图片上传 -->
            <button class="tool-btn image-btn" @click="handleImageClick" title="发送图片" aria-label="图片">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
                <rect x="3" y="3" width="18" height="18" rx="3" stroke="#2E7D32" stroke-width="2"/>
                <circle cx="8.5" cy="8.5" r="1.5" stroke="#2E7D32" stroke-width="2"/>
                <path d="M21 15l-5-5L12 14M3 21L18 6" stroke="#2E7D32" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <input ref="fileInput" type="file" accept="image/*" class="sr-only" @change="handleFileChange" />
          </div>
        </footer>
      </main>

      <!-- 右侧：工作区面板 -->
      <aside class="workspace-panel" :class="{ 'mobile-open': workspaceMobileOpen }">
        <!-- 移动端遮罩 -->
        <div class="workspace-mask" v-if="isMobile && workspaceMobileOpen" @click="workspaceMobileOpen = false"></div>

        <div class="workspace-content">
          <!-- 标签栏 -->
          <div class="workspace-tabs">
            <button
              v-for="tab in workspaceTabs"
              :key="tab.key"
              :class="['tab-btn', { active: activeWorkspace === tab.key }]"
              @click="activeWorkspace = tab.key"
              :title="tab.label"
            >
              <span class="tab-icon">
                <!-- 处方审核 -->
                <svg v-if="tab.icon === 'prescription'" width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2" stroke="currentColor" stroke-width="2"/>
                  <path d="M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="currentColor" stroke-width="2"/>
                </svg>
                <!-- 病历开具 -->
                <svg v-else-if="tab.icon === 'record'" width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="currentColor" stroke-width="2"/>
                </svg>
                <!-- 历史记录 -->
                <svg v-else-if="tab.icon === 'history'" width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2"/>
                </svg>
                <!-- 检查建议 -->
                <svg v-else-if="tab.icon === 'exam'" width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2z" stroke="currentColor" stroke-width="2"/>
                </svg>
              </span>
              <span class="tab-label">{{ tab.label }}</span>
            </button>
            <!-- 移动端关闭按钮 -->
            <button v-if="isMobile" class="tab-btn tab-btn--close" @click="workspaceMobileOpen = false" title="关闭" aria-label="关闭工作区">
              <span class="tab-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </span>
              <span class="tab-label">关闭</span>
            </button>
          </div>

          <!-- 面板内容 -->
          <div class="workspace-body">
            <!-- 处方审核面板 -->
            <div v-if="activeWorkspace === 'prescription'" class="panel-content">
              <h3 class="panel-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                  <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2" stroke="currentColor" stroke-width="2"/>
                  <path d="M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="currentColor" stroke-width="2"/>
                </svg>
                处方审核
              </h3>

              <!-- 患者提交药品 -->
              <div class="section" v-if="submittedMedicines.length">
                <div class="section-title">
                  患者用药申请
                  <span class="count-hint">({{ approvedSubmittedCount }}/{{ submittedMedicines.length }} 已通过)</span>
                </div>
                <div class="medicine-list">
                  <div v-for="(med, idx) in submittedMedicines" :key="idx" class="medicine-item patient-med" :class="{ approved: med.approved, rejected: med.rejected, replaced: med.replaced, pending: !med.approved && !med.rejected && !med.replaced }">
                    <div class="patient-med-content">
                      <div class="med-info-header">
                        <div class="med-name">{{ med.name }}</div>
                        <div class="med-status-icon">
                          <span v-if="med.approved" class="status-badge success">已通过</span>
                          <span v-else-if="med.rejected" class="status-badge danger">已驳回</span>
                          <span v-else-if="med.replaced" class="status-badge warning">已替换</span>
                          <span v-else class="status-badge pending">待审核</span>
                        </div>
                      </div>
                      <div class="med-spec">{{ med.spec }} x {{ med.quantity }}</div>
                      <div class="med-note" v-if="med.patientNote">患者备注：{{ med.patientNote }}</div>
                      <div class="med-replaced-info" v-if="med.replaced">
                        <span class="status-replaced">已替换为：{{ med.replacedWith }}</span>
                      </div>
                    </div>
                    <!-- 待审核状态显示操作按钮 -->
                    <div v-if="!med.approved && !med.rejected && !med.replaced" class="med-actions-compact">
                      <button class="btn-icon-text btn-approve" @click="approveMedicine(idx)" :disabled="totalMedicineCount >= 5" title="通过">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="20 6 9 17 4 12"/>
                        </svg>
                        <span>通过</span>
                      </button>
                      <button class="btn-icon-text btn-replace" @click="replaceMedicine(idx)" :disabled="totalMedicineCount >= 5" title="替换">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M17 1l4 4-4 4"/>
                          <path d="M3 11V9a4 4 0 014-4h14"/>
                          <path d="M7 23l-4-4 4-4"/>
                          <path d="M21 13v2a4 4 0 01-4 4H3"/>
                        </svg>
                        <span>替换</span>
                      </button>
                      <button class="btn-icon-text btn-reject" @click="rejectMedicine(idx)" title="驳回">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <line x1="18" y1="6" x2="6" y2="18"/>
                          <line x1="6" y1="6" x2="18" y2="18"/>
                        </svg>
                        <span>驳回</span>
                      </button>
                    </div>
                    <!-- 已处理状态显示操作按钮 -->
                    <div v-else class="med-actions-compact single">
                      <button v-if="med.approved" class="btn-text-small" @click="cancelApproveMedicine(idx)">取消通过</button>
                      <button v-if="med.rejected" class="btn-text-small" @click="rejectMedicine(idx)">取消驳回</button>
                      <button v-if="med.replaced" class="btn-text-small" @click="cancelReplaceMedicine(idx)">取消替换</button>
                    </div>
                  </div>
                </div>
                <!-- 批量审核按钮 -->
                <div v-if="pendingMedicines.length > 0" class="batch-actions">
                  <button class="btn btn-primary btn-sm" @click="approveAllMedicines" :disabled="totalMedicineCount + pendingMedicines.length > 5">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="20 6 9 17 4 12"/>
                    </svg>
                    全部通过 ({{ pendingMedicines.length }})
                  </button>
                  <span v-if="totalMedicineCount + pendingMedicines.length > 5" class="limit-tip">药品数量将超过5个上限</span>
                </div>
              </div>

              <div class="section" v-else>
                <div class="empty-state">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
                    <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="#ccc" stroke-width="1.5"/>
                  </svg>
                  <p>暂无患者用药申请</p>
                  <p class="sub">患者可在购药时选择外配处方</p>
                </div>
              </div>

              <!-- 已添加药品列表 -->
              <div class="section" v-if="prescriptionMedicines.length > 0">
                <div class="section-title">
                  医生添加药品 ({{ prescriptionMedicines.length }}/5)
                  <span class="count-hint">总计 {{ totalMedicineCount }}/5</span>
                  <span v-if="totalMedicineCount >= 5" class="limit-hint">已达到上限</span>
                </div>
                <div class="medicine-list">
                  <div v-for="(med, idx) in prescriptionMedicines" :key="idx" class="medicine-card">
                    <div class="med-card-header">
                      <span class="med-index">{{ idx + 1 }}</span>
                      <span class="med-name">{{ med.name }}</span>
                      <button class="btn-remove" @click="removeMedicine(idx)">×</button>
                    </div>
                    <div class="med-card-body">
                      <div class="med-info-row">
                        <span class="med-label">规格：</span>
                        <span class="med-value">{{ med.spec }}</span>
                      </div>
                      <div class="med-info-row">
                        <span class="med-label">数量：</span>
                        <span class="med-value">{{ med.quantity }}</span>
                      </div>
                      <div class="med-info-row">
                        <span class="med-label">用法：</span>
                        <span class="med-value">{{ med.usage }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 添加药品表单 - 可折叠 -->
              <div class="section medicine-form-section" v-if="(totalMedicineCount < 5 || replacingIndex !== null) && (showAddMedicineForm || replacingIndex !== null)">
                <div class="section-title">
                  <template v-if="replacingIndex === null">
                    添加药品 ({{ totalMedicineCount }}/5)
                    <span class="quick-fill-hint">快速录入模式</span>
                  </template>
                  <template v-else>
                    替换药品
                    <span class="quick-fill-hint warning">正在替换：{{ submittedMedicines[replacingIndex]?.name }}</span>
                  </template>
                </div>
                <div class="medicine-form-simple">
                  <!-- 药品名称 - 带自动联想 -->
                  <div class="form-line">
                    <label>药品名称 <span class="required">*</span></label>
                    <div class="med-search-wrap">
                      <input
                        v-model="medicineForm.name"
                        placeholder="输入药品名称搜索..."
                        class="med-name-input"
                        @input="onMedicineInput"
                        @focus="showMedSuggestions = medicineForm.name.length > 0"
                        @blur="hideMedSuggestions"
                      />
                      <!-- 药品联想下拉 -->
                      <div v-if="showMedSuggestions && filteredMedicines.length > 0" class="med-suggestions">
                        <div
                          v-for="med in filteredMedicines"
                          :key="med.name"
                          class="med-suggestion-item"
                          @mousedown="selectMedicine(med)"
                        >
                          <div class="suggestion-name">{{ med.name }}</div>
                          <div class="suggestion-spec">{{ med.spec }} | {{ med.defaultUsage }}</div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 规格和数量合并一行 -->
                  <div class="form-line compact">
                    <div class="form-group-half">
                      <label>规格</label>
                      <input v-model="medicineForm.spec" placeholder="0.5g*24片" />
                    </div>
                    <div class="form-group-half">
                      <label>数量</label>
                      <div class="qty-quick">
                        <button class="qty-mini" @click="medicineForm.quantity > 1 && medicineForm.quantity--">−</button>
                        <input v-model.number="medicineForm.quantity" type="number" min="1" max="99" class="qty-input-mini" />
                        <button class="qty-mini" @click="medicineForm.quantity < 99 && medicineForm.quantity++">+</button>
                        <select v-model="medicineForm.unit" class="unit-select-mini">
                          <option value="盒">盒</option>
                          <option value="瓶">瓶</option>
                          <option value="支">支</option>
                          <option value="片">片</option>
                          <option value="粒">粒</option>
                          <option value="袋">袋</option>
                        </select>
                      </div>
                    </div>
                  </div>

                  <!-- 用法用量 - 快捷选择 -->
                  <div class="form-line">
                    <label>用法用量 <span class="required">*</span></label>
                    <div class="usage-quick-row">
                      <select v-model="medicineForm.usageTimes" class="usage-select-mini">
                        <option :value="1">每日1次</option>
                        <option :value="2">每日2次</option>
                        <option :value="3">每日3次</option>
                        <option :value="4">每日4次</option>
                      </select>
                      <span class="usage-sep">每次</span>
                      <input v-model.number="medicineForm.usageDose" type="number" min="0.5" max="10" step="0.5" class="dose-input-mini" />
                      <select v-model="medicineForm.usageUnit" class="unit-select-mini">
                        <option value="片">片</option>
                        <option value="粒">粒</option>
                        <option value="支">支</option>
                        <option value="ml">ml</option>
                        <option value="袋">袋</option>
                      </select>
                    </div>
                    <!-- 常用时间快捷选择 -->
                    <div class="timing-quick">
                      <button
                        v-for="time in timingOptions"
                        :key="time.value"
                        type="button"
                        class="timing-btn"
                        :class="{ active: medicineForm.timing.includes(time.value) }"
                        @click="toggleTiming(time.value)"
                      >
                        {{ time.label }}
                      </button>
                    </div>
                  </div>

                  <!-- 预览和添加/替换按钮 -->
                  <div class="form-actions">
                    <div class="usage-preview-mini" v-if="generatedUsage">
                      <span class="preview-text">{{ generatedUsage }}</span>
                    </div>
                    <!-- 正常添加模式 -->
                    <button
                      v-if="replacingIndex === null"
                      class="btn btn-primary btn-add-quick"
                      @click="confirmAddMedicine"
                      :disabled="!canAddMedicine"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="5" x2="12" y2="19"/>
                        <line x1="5" y1="12" x2="19" y2="12"/>
                      </svg>
                      添加药品
                    </button>
                    <!-- 替换模式 -->
                    <template v-else>
                      <button
                        class="btn btn-primary btn-add-quick"
                        @click="confirmReplaceMedicine"
                        :disabled="!canAddMedicine"
                      >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="20 6 9 17 4 12"/>
                        </svg>
                        确认替换
                      </button>
                      <button
                        class="btn btn-secondary btn-add-quick"
                        @click="cancelReplaceMedicine"
                      >
                        取消替换
                      </button>
                    </template>
                  </div>
                </div>
              </div>

              <!-- 添加药品按钮（折叠状态显示） -->
              <div class="section add-medicine-trigger" v-if="totalMedicineCount < 5 && !showAddMedicineForm && replacingIndex === null">
                <button class="btn-add-medicine-fold" @click="showAddMedicineForm = true">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                  </svg>
                  <span>添加药品</span>
                  <span class="count-badge">{{ totalMedicineCount }}/5</span>
                </button>
              </div>

              <!-- 达到上限提示 -->
              <div class="section" v-if="totalMedicineCount >= 5 && replacingIndex === null">
                <div class="limit-reached">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="8" x2="12" y2="12"/>
                    <line x1="12" y1="16" x2="12.01" y2="16"/>
                  </svg>
                  <span>已达到最大药品数量（5个）</span>
                </div>
              </div>

              <div class="section">
                <div class="section-title">处方诊断 <span class="required">*</span></div>
                <input v-model="prescriptionForm.diagnosis" class="form-input" placeholder="请输入诊断" />
              </div>

              <div class="panel-actions">
                <button class="btn btn-primary" @click="submitPrescription" :disabled="!canSubmitPrescription">
                  确认开具处方
                </button>
                <button class="btn btn-danger" @click="rejectPrescription">驳回并说明原因</button>
              </div>
            </div>

            <!-- 病历开具面板 -->
            <div v-if="activeWorkspace === 'record'" class="panel-content">
              <h3 class="panel-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                  <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="currentColor" stroke-width="2"/>
                </svg>
                病历开具
              </h3>

              <div class="form-group">
                <label>主诉</label>
                <textarea v-model="medicalRecord.chiefComplaint" rows="2" placeholder="患者主要症状及持续时间"></textarea>
              </div>

              <div class="form-group">
                <label>现病史</label>
                <textarea v-model="medicalRecord.presentIllness" rows="4" placeholder="详细描述病情发展过程"></textarea>
              </div>

              <div class="form-group">
                <label>既往史</label>
                <input v-model="medicalRecord.pastHistory" placeholder="过往疾病史、手术史等" />
              </div>

              <div class="form-group">
                <label>过敏史</label>
                <input v-model="medicalRecord.allergyHistory" placeholder="药物或食物过敏情况" />
              </div>

              <div class="form-group">
                <label>体格检查</label>
                <textarea v-model="medicalRecord.physicalExam" rows="2" placeholder="查体结果"></textarea>
              </div>

              <div class="form-group">
                <label>诊断 <span class="required">*</span></label>
                <input v-model="medicalRecord.diagnosis" placeholder="主要诊断" />
              </div>

              <div class="form-group">
                <label>处理意见</label>
                <textarea v-model="medicalRecord.treatment" rows="3" placeholder="治疗方案、注意事项等"></textarea>
              </div>

              <div class="panel-actions">
                <button class="btn btn-secondary" @click="saveRecordDraft">保存草稿</button>
                <button class="btn btn-primary" @click="submitRecord" :disabled="!medicalRecord.diagnosis">
                  提交病历
                </button>
              </div>
            </div>

            <!-- 历史记录面板 -->
            <div v-if="activeWorkspace === 'history'" class="panel-content">
              <h3 class="panel-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                  <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2"/>
                </svg>
                历史记录
              </h3>

              <div class="history-tabs">
                <button :class="{ active: historyTab === 'prescription' }" @click="historyTab = 'prescription'">处方</button>
                <button :class="{ active: historyTab === 'visit' }" @click="historyTab = 'visit'">就医</button>
                <button :class="{ active: historyTab === 'exam' }" @click="historyTab = 'exam'">检查</button>
              </div>

              <div class="history-list">
                <div v-for="record in filteredHistoryRecords" :key="record.id" class="history-item">
                  <div class="history-date">{{ record.date }}</div>
                  <div class="history-diagnosis">{{ record.diagnosis }}</div>
                  <div class="history-detail" v-if="historyTab === 'prescription'">
                    <div v-for="med in record.medicines" :key="med.name" class="med-line">• {{ med.name }} {{ med.dosage }}</div>
                  </div>
                  <div class="history-detail" v-if="historyTab === 'exam'">
                    <div v-for="exam in record.examinations" :key="exam.item" class="exam-line">• {{ exam.item }} - {{ exam.status }}</div>
                  </div>
                  <div class="history-actions">
                    <button class="btn-text" @click="viewHistoryDetail(record)">查看详情</button>
                    <button v-if="historyTab === 'prescription'" class="btn-text btn-text-primary" @click="importHistoryPrescription(record)">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 5v14M5 12h14"/>
                      </svg>
                      一键引用
                    </button>
                  </div>
                </div>

                <div v-if="filteredHistoryRecords.length === 0" class="empty-state">
                  <p>暂无{{ historyTab === 'prescription' ? '处方' : historyTab === 'visit' ? '就医' : '检查' }}记录</p>
                </div>
              </div>
            </div>

            <!-- 检查建议面板 -->
            <div v-if="activeWorkspace === 'exam'" class="panel-content">
              <h3 class="panel-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                  <path d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2z" stroke="currentColor" stroke-width="2"/>
                </svg>
                检查建议
              </h3>

              <!-- 已选检查项目摘要 -->
              <div v-if="examinationForm.items.length > 0" class="exam-summary">
                <div class="summary-title">已选择 {{ examinationForm.items.length }} 项检查</div>
                <div class="summary-tags">
                  <span v-for="item in examinationForm.items" :key="item" class="summary-tag">
                    {{ getExamLabel(item) }}
                    <button class="tag-remove" @click="removeExamItem(item)">×</button>
                  </span>
                </div>
              </div>

              <!-- 检查项目分类选择 -->
              <div class="form-group">
                <label>检查项目 <span class="required">*</span></label>
                <div class="exam-categories">
                  <div v-for="(category, idx) in examCategories" :key="idx" class="exam-category">
                    <div class="category-title">{{ category.name }}</div>
                    <div class="category-items">
                      <label v-for="exam in category.items" :key="exam.value" class="check-chip" :class="{ checked: examinationForm.items.includes(exam.value) }">
                        <input type="checkbox" :value="exam.value" v-model="examinationForm.items" class="sr-only" />
                        <span>{{ exam.label }}</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 折叠面板：高级选项 -->
              <div class="collapse-panel">
                <div class="collapse-header" @click="showExamAdvanced = !showExamAdvanced">
                  <span>高级选项</span>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" :class="{ rotate: showExamAdvanced }">
                    <polyline points="6 9 12 15 18 9"/>
                  </svg>
                </div>
                <div v-show="showExamAdvanced" class="collapse-content">
                  <div class="form-group">
                    <label>检查部位</label>
                    <input v-model="examinationForm.site" placeholder="如：胸部、腹部、头部" />
                  </div>

                  <div class="form-group">
                    <label>检查目的</label>
                    <textarea v-model="examinationForm.purpose" rows="2" placeholder="说明检查目的和临床意义"></textarea>
                  </div>

                  <div class="form-group">
                    <label>紧急程度</label>
                    <div class="urgency-options">
                      <label v-for="opt in urgencyOptions" :key="opt.value" class="urgency-option" :class="{ active: examinationForm.urgency === opt.value }">
                        <input type="radio" :value="opt.value" v-model="examinationForm.urgency" class="sr-only" />
                        <span :class="opt.class">{{ opt.label }}</span>
                      </label>
                    </div>
                  </div>

                  <div class="form-group">
                    <label>备注</label>
                    <textarea v-model="examinationForm.remark" rows="2" placeholder="其他需要说明的内容"></textarea>
                  </div>
                </div>
              </div>

              <div class="panel-actions">
                <button class="btn btn-secondary" @click="saveExaminationDraft">保存草稿</button>
                <button class="btn btn-primary" @click="submitExamination" :disabled="examinationForm.items.length === 0">
                  提交检查单
                </button>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- 移动端工作区切换按钮 -->
    <button v-if="isMobile" class="workspace-toggle" @click="workspaceMobileOpen = true">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
        <path d="M4 6h16M4 12h16M4 18h16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      工作区
    </button>

    <!-- 更多功能菜单 -->
    <Teleport to="body">
      <Transition name="sheet-fade">
        <div v-if="showMoreMenu" class="sheet-overlay" @click.self="showMoreMenu = false">
          <div class="action-sheet">
            <div class="sheet-handle"></div>
            <div class="sheet-grid">
              <button class="sheet-item" @click="openWorkspace('record')">
                <span class="sheet-icon sheet-icon--record">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
                    <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="sheet-label">写病历</span>
              </button>
              <button class="sheet-item" @click="openWorkspace('prescription')">
                <span class="sheet-icon sheet-icon--prescription">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
                    <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2" stroke="currentColor" stroke-width="2"/>
                    <path d="M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="sheet-label">处方审核</span>
              </button>
              <button class="sheet-item" @click="openWorkspace('exam')">
                <span class="sheet-icon sheet-icon--exam">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
                    <path d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2z" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="sheet-label">检查建议</span>
              </button>
              <button class="sheet-item" @click="openWorkspace('history')">
                <span class="sheet-icon sheet-icon--history">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
                    <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="sheet-label">历史记录</span>
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- 语音录制弹窗 -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="showVoiceModal" class="modal-mask" @click.self="closeVoiceModal">
          <div class="voice-modal">
            <div class="voice-header">
              <h3 class="voice-title">语音录制</h3>
              <p class="voice-subtitle">{{ isRecording ? '正在录音...' : '点击开始录音' }}</p>
            </div>
            <div class="voice-visual">
              <div class="wave-rings">
                <div class="wave-ring wave-ring--1" :class="{ active: isRecording }"></div>
                <div class="wave-ring wave-ring--2" :class="{ active: isRecording }"></div>
                <div class="wave-ring wave-ring--3" :class="{ active: isRecording }"></div>
              </div>
              <div class="wave-bars">
                <div class="wave-bar" v-for="i in 7" :key="i" :style="{ '--delay': i * 0.08 + 's', '--height': Math.random() * 28 + 12 + 'px' }" :class="{ animating: isRecording }"></div>
              </div>
            </div>
            <div class="voice-timer">{{ formatRecordTime(recordTime) }}</div>
            <div class="voice-actions">
              <button class="voice-btn voice-btn--cancel" @click="closeVoiceModal">取消</button>
              <button class="voice-btn voice-btn--record" :class="{ 'voice-btn--recording': isRecording }" @click="toggleRecording">
                <span v-if="!isRecording" class="voice-btn-inner">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="#fff">
                    <circle cx="12" cy="12" r="10"/>
                  </svg>
                  开始录音
                </span>
                <span v-else class="voice-btn-inner voice-btn-inner--stop">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="#fff">
                    <rect x="5" y="5" width="14" height="14" rx="3"/>
                  </svg>
                  停止录音
                </span>
              </button>
              <button v-if="!isRecording && recordedBlob" class="voice-btn voice-btn--send" @click="sendVoiceMessage">发送</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- 图片预览弹窗 -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="showImagePreview" class="modal-mask image-preview-mask" @click.self="showImagePreview = false">
          <div class="image-preview">
            <button class="preview-close" @click="showImagePreview = false">&times;</button>
            <div class="preview-images">
              <img v-for="(img, idx) in previewImages" :key="idx" :src="img" class="preview-img" />
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- 驳回原因弹窗 -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="showRejectModal" class="modal-mask" @click.self="showRejectModal = false">
          <div class="reject-modal">
            <h3 class="reject-title">驳回原因</h3>
            <textarea v-model="rejectReason" rows="4" placeholder="请输入驳回原因，将告知患者..."></textarea>
            <div class="reject-actions">
              <button class="btn btn-secondary" @click="showRejectModal = false">取消</button>
              <button class="btn btn-danger" @click="confirmRejectPrescription" :disabled="!rejectReason.trim()">确认驳回</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { imService } from '@/utils/im'
import { getUserSig } from '@/api/im'
import { useDoctorStore } from '@/stores/doctor'
import { completeConsultation, getDoctorConsultationDetail, getRequestedDrugs, sendConsultationMessage, getConsultationMessages } from '@/api/consultation'
import { getPatientDetail } from '@/api/patient'

const route = useRoute()
const router = useRouter()
const doctorStore = useDoctorStore()

const consultationId = route.params.id as string || '1'
const patientId = route.query.patientId as string || '1'

// IM通讯使用的患者用户ID（从问诊详情中获取）
let imPatientUserId: string = patientId

// 加载问诊详情
async function loadConsultationDetail() {
  try {
    const detail = await getDoctorConsultationDetail(consultationId)
    
    if (detail) {
      // 更新问诊信息
      consultationInfo.value = {
        id: detail.id,
        patientId: detail.patientId,
        patientName: detail.patientName,
        status: detail.status,
        type: detail.type,
        symptom: detail.symptom || '' // 症状描述
      }
      
      // 重要：获取患者的user_id用于IM通讯
      if (detail.patientUserId) {
        imPatientUserId = detail.patientUserId
        console.log('[Chat] 使用患者用户ID进行IM通讯:', imPatientUserId)
      } else {
        console.warn('[Chat] 未获取到patientUserId，使用patientId作为fallback:', patientId)
      }
      
      console.log('[Chat] 问诊详情加载成功:', detail)
      
      // 先设置基本信息
      patientInfo.value = {
        id: detail.patientId,
        name: detail.patientName,
        gender: detail.patientGender,
        age: detail.patientAge,
        avatar: detail.patientAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=patient',
        tags: [],
        allergyHistory: [],
        chronicDiseases: [],
        historyCount: 0
      }
      
      // 再获取患者详细信息
      try {
        const patientDetail = await getPatientDetail(detail.patientId)
        
        if (patientDetail) {
          // 更新完整的患者信息
          patientInfo.value = {
            id: patientDetail.id,
            name: patientDetail.name,
            gender: patientDetail.gender,
            age: patientDetail.age,
            avatar: patientDetail.avatar || patientInfo.value.avatar,
            tags: patientDetail.tags || [],
            allergyHistory: patientDetail.allergies ? patientDetail.allergies.split(',') : [],
            chronicDiseases: patientDetail.medicalHistory ? patientDetail.medicalHistory.split(',') : [],
            historyCount: patientDetail.visitCount || 0
          }
          
          console.log('[Chat] 患者详情加载成功:', patientDetail)
        }
      } catch (error: any) {
        console.warn('[Chat] 加载患者详情失败，使用基本信息:', error)
        // 即使患者详情加载失败，也继续使用问诊详情中的基本信息
      }
      
      // 更新预问诊信息（从问诊详情的symptom字段解析，用患者档案补充过敏/慢病史）
      if (detail.symptom) {
        // 解析 symptom 字段（格式: "症状描述 | 时长:xxx | 过敏:xxx | 用药:xxx"）
        const parts = detail.symptom.split(' | ')
        preInquiry.value.chiefComplaint = parts[0]
        inquiryInfo.value.chiefComplaint = parts[0]

        // 提取时长、过敏、用药信息
        const durationPart = parts.find(p => p.startsWith('时长:'))
        const allergyPart = parts.find(p => p.startsWith('过敏:'))
        const medPart = parts.find(p => p.startsWith('用药:'))

        // 从 symptom 字段提取的症状详情
        if (durationPart || allergyPart || medPart) {
          const parsedSymptoms = []
          if (durationPart) parsedSymptoms.push('患病时长：' + durationPart.replace('时长:', ''))
          preInquiry.value.symptoms = parsedSymptoms.length > 0 ? parsedSymptoms : preInquiry.value.symptoms
          preInquiry.value.medicationHistory = medPart ? medPart.replace('用药:', '') : ''
          preInquiry.value.allergyHistory = allergyPart ? allergyPart.replace('过敏:', '') : ''
        }
      }

      // 用患者档案的真实过敏史和慢病史覆盖（如果有的话）
      if (patientInfo.value.allergyHistory?.length > 0) {
        preInquiry.value.allergyHistory = patientInfo.value.allergyHistory.join('、')
      }
      if (patientInfo.value.chronicDiseases?.length > 0) {
        preInquiry.value.pastHistory = patientInfo.value.chronicDiseases.join('、') + '病史'
      }
      
      // 加载患者申请的药品列表
      try {
        console.log('[Chat] 开始加载患者申请的药品, 问诊ID:', detail.id)
        const drugs = await getRequestedDrugs(detail.id)
        console.log('[Chat] API返回的药品数据:', drugs)
        console.log('[Chat] 药品数据类型:', typeof drugs, Array.isArray(drugs))
        
        if (drugs && drugs.length > 0) {
          console.log('[Chat] 找到', drugs.length, '个药品, 第一个药品:', drugs[0])
          
          // 将后端Drug对象转换为前端SubmittedMedicine格式
          submittedMedicines.value = drugs.map(drug => {
            // 后端返回的字段名是 productName，需要兼容处理
            const drugName = (drug as any).productName || drug.name || '未知药品'
            console.log('[Chat] 转换药品:', { productName: (drug as any).productName, name: drug.name, drugName })
            
            return {
              name: drugName,
              spec: drug.specification || '',
              quantity: 1, // 默认数量，后续可以从处方申请表单获取
              patientNote: '', // 患者备注，可能需要额外字段
              approved: false,
              rejected: false,
              replaced: false
            }
          })
          console.log('[Chat] ✅ 患者用药申请已加载:', submittedMedicines.value.length, '个药品')
          console.log('[Chat] 药品详情:', JSON.stringify(submittedMedicines.value, null, 2))
        } else {
          console.log('[Chat] ⚠️ 该问诊没有患者申请的药品, drugs:', drugs)
        }
      } catch (error: any) {
        console.error('[Chat]  加载患者申请药品失败:', error)
        console.error('[Chat] 错误详情:', error.message, error.response)
        // 不显示错误提示，因为可能确实没有申请药品
      }
      
      console.log('[Chat] 问诊详情加载成功:', detail)
    }
  } catch (error: any) {
    console.error('[Chat] 加载问诊详情失败:', error)
    showToast('加载问诊详情失败', 'error')
  }
}

const isMobile = ref(false)
const checkMobile = () => {
  isMobile.value = window.innerWidth < 1024
}

const loading = ref(false)
const sending = ref(false)
const inputMessage = ref('')
const messages = ref<any[]>([])
const messageListRef = ref<HTMLElement>()
const pollingTimer = ref<ReturnType<typeof setInterval> | null>(null)
const error = ref('')
const imConnected = ref(false)

// IM事件处理函数引用（用于正确移除监听）
let handleSdkReady: (() => void) | null = null
let handleSdkNotReady: (() => void) | null = null

const sidebarCollapsed = ref(false)
const preInquiryCollapsed = ref(false)
const activeWorkspace = ref('prescription')
const workspaceMobileOpen = ref(false)
const showMoreMenu = ref(false)
const showVoiceModal = ref(false)
const showImagePreview = ref(false)
const showRejectModal = ref(false)
const inputFocused = ref(false)
const isRecording = ref(false)
const recordTime = ref(0)
let recordTimer: any = null
let mediaRecorder: MediaRecorder | null = null
let recordedChunks: Blob[] = []
const recordedBlob = ref<Blob | null>(null)
const fileInput = ref<HTMLInputElement>()
const rejectReason = ref('')

interface ToastItem { id: number; message: string; type: string }
const toasts = ref<ToastItem[]>([])
let toastIdCounter = 0
const toastIcons: Record<string, string> = { success: '✓', warning: '⚠', error: '✕', info: 'ℹ' }

function showToast(message: string, type: string = 'info') {
  const id = ++toastIdCounter
  toasts.value.push({ id, message, type })
  setTimeout(() => removeToast(id), 3000)
}
function removeToast(id: number) { toasts.value = toasts.value.filter(t => t.id !== id) }

const workspaceTabs = [
  { key: 'prescription', label: '处方审核', icon: 'prescription' },
  { key: 'record', label: '病历开具', icon: 'record' },
  { key: 'history', label: '历史记录', icon: 'history' },
  { key: 'exam', label: '检查建议', icon: 'exam' }
]

const patientInfo = ref({
  id: patientId,
  name: (route.query.patientName as string) || '张三',
  gender: '男',
  age: 35,
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=patient',
  tags: ['复诊患者', '高血压'],
  allergyHistory: ['青霉素', '磺胺类'],
  chronicDiseases: ['高血压', '糖尿病'],
  historyCount: 5
})

const inquiryInfo = ref({
  type: '图文问诊',
  submitTime: '2024-01-15 09:30',
  chiefComplaint: '头痛、发热3天'
})

const preInquiry = ref({
  chiefComplaint: '头痛、发热3天',
  symptoms: [
    '持续性头痛，以前额部为主',
    '体温最高38.5°C',
    '伴有轻微咳嗽',
    '无恶心呕吐'
  ],
  medicationHistory: '服用过布洛芬，效果不明显',
  allergyHistory: '青霉素过敏',
  pastHistory: '高血压病史5年，糖尿病病史3年',
  images: ['https://example.com/img1.jpg', 'https://example.com/img2.jpg']
})

const previewImages = ref<string[]>([])

const quickReplies = [
  '您好，请问有什么不适？',
  '建议您去做个检查',
  '请注意休息多喝水',
  '处方已开具，请按时服药',
  '如有不适请及时复诊'
]

const consultationInfo = ref({
  id: consultationId,
  patientId: patientId,
  patientName: patientInfo.value.name,
  status: 'processing',
  type: '图文问诊',
  symptom: '' // 症状描述
})

// 从doctorStore获取医生信息
const doctorAvatar = computed(() => {
  return doctorStore.doctorInfo?.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor'
})
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'

const canSend = computed(() => {
  return inputMessage.value.trim().length > 0 && !sending.value
})

const canSubmitPrescription = computed(() => {
  return prescriptionForm.value.diagnosis && totalMedicineCount.value > 0
})

const approvedSubmittedCount = computed(() => {
  return submittedMedicines.value.filter(m => m.approved && !m.rejected && !m.replaced).length
})

const pendingMedicines = computed(() => {
  return submittedMedicines.value.filter(m => !m.approved && !m.rejected && !m.replaced)
})

const totalMedicineCount = computed(() => {
  const activeSubmittedCount = submittedMedicines.value.filter(m => !m.rejected && !m.replaced).length
  return prescriptionMedicines.value.length + activeSubmittedCount
})

interface SubmittedMedicine {
  name: string
  spec: string
  quantity: number
  patientNote?: string
  approved: boolean
  rejected: boolean
  replaced: boolean
  replacedWith?: string
}

const submittedMedicines = ref<SubmittedMedicine[]>([])

const prescriptionMedicines = ref<any[]>([])

const medicineForm = ref({
  name: '',
  spec: '',
  quantity: 1,
  unit: '盒',
  usageTimes: 3,
  usageDose: 1,
  usageUnit: '片',
  timing: [] as string[]
})

const timingOptions = [
  { label: '饭前', value: 'before_meal' },
  { label: '饭后', value: 'after_meal' },
  { label: '餐中', value: 'during_meal' },
  { label: '空腹', value: 'empty_stomach' },
  { label: '睡前', value: 'before_bed' },
  { label: '晨起', value: 'morning' }
]

const medicineDatabase = [
  { name: '阿莫西林胶囊', spec: '0.5g*24粒', defaultUsage: '每日3次，每次1粒，饭后', usageTimes: 3, usageDose: 1, usageUnit: '粒', timing: ['after_meal'] },
  { name: '布洛芬缓释胶囊', spec: '0.3g*20粒', defaultUsage: '必要时服用，每次1粒', usageTimes: 1, usageDose: 1, usageUnit: '粒', timing: ['after_meal'] },
  { name: '头孢克肟片', spec: '0.1g*12片', defaultUsage: '每日2次，每次1片，饭后', usageTimes: 2, usageDose: 1, usageUnit: '片', timing: ['after_meal'] },
  { name: '奥美拉唑肠溶胶囊', spec: '20mg*28粒', defaultUsage: '每日1次，每次1粒，晨起空腹', usageTimes: 1, usageDose: 1, usageUnit: '粒', timing: ['morning', 'empty_stomach'] },
  { name: '蒙脱石散', spec: '3g*10袋', defaultUsage: '每日3次，每次1袋，空腹', usageTimes: 3, usageDose: 1, usageUnit: '袋', timing: ['empty_stomach'] },
  { name: '盐酸氨溴索片', spec: '30mg*20片', defaultUsage: '每日3次，每次1片，饭后', usageTimes: 3, usageDose: 1, usageUnit: '片', timing: ['after_meal'] },
  { name: '复方甘草片', spec: '100片', defaultUsage: '每日3次，每次3片，含服', usageTimes: 3, usageDose: 3, usageUnit: '片', timing: [] },
  { name: '硝苯地平缓释片', spec: '20mg*30片', defaultUsage: '每日2次，每次1片', usageTimes: 2, usageDose: 1, usageUnit: '片', timing: [] },
  { name: '二甲双胍片', spec: '0.5g*60片', defaultUsage: '每日3次，每次1片，餐中', usageTimes: 3, usageDose: 1, usageUnit: '片', timing: ['during_meal'] },
  { name: '阿司匹林肠溶片', spec: '100mg*30片', defaultUsage: '每日1次，每次1片，饭后', usageTimes: 1, usageDose: 1, usageUnit: '片', timing: ['after_meal'] },
  { name: '阿托伐他汀钙片', spec: '20mg*7片', defaultUsage: '每晚1次，每次1片', usageTimes: 1, usageDose: 1, usageUnit: '片', timing: ['before_bed'] },
  { name: '氯雷他定片', spec: '10mg*12片', defaultUsage: '每日1次，每次1片', usageTimes: 1, usageDose: 1, usageUnit: '片', timing: [] },
  { name: '维生素C片', spec: '0.1g*100片', defaultUsage: '每日3次，每次1片', usageTimes: 3, usageDose: 1, usageUnit: '片', timing: [] },
  { name: '葡萄糖酸钙片', spec: '0.5g*100片', defaultUsage: '每日3次，每次2片', usageTimes: 3, usageDose: 2, usageUnit: '片', timing: [] }
]

// 药品联想相关
const showMedSuggestions = ref(false)
const filteredMedicines = computed(() => {
  const keyword = medicineForm.value.name.trim()
  if (!keyword) return []
  return medicineDatabase.filter(med => 
    med.name.toLowerCase().includes(keyword.toLowerCase())
  ).slice(0, 5)
})

// 药品输入处理
function onMedicineInput() {
  showMedSuggestions.value = medicineForm.value.name.length > 0
}

function hideMedSuggestions() {
  setTimeout(() => {
    showMedSuggestions.value = false
  }, 200)
}

// 选择药品自动填充
function selectMedicine(med: any) {
  medicineForm.value.name = med.name
  medicineForm.value.spec = med.spec
  medicineForm.value.usageTimes = med.usageTimes
  medicineForm.value.usageDose = med.usageDose
  medicineForm.value.usageUnit = med.usageUnit
  medicineForm.value.timing = [...med.timing]
  showMedSuggestions.value = false
}

// 计算生成的用法用量文本
const generatedUsage = computed(() => {
  const timesText = `每日${medicineForm.value.usageTimes}次`
  const doseText = `每次${medicineForm.value.usageDose}${medicineForm.value.usageUnit}`
  const timingText = medicineForm.value.timing.length > 0
    ? '，' + medicineForm.value.timing.map(t => {
        const opt = timingOptions.find(o => o.value === t)
        return opt ? opt.label : t
      }).join('、')
    : ''
  return `${timesText}，${doseText}${timingText}`
})

// 检查是否可以添加药品
const canAddMedicine = computed(() => {
  return medicineForm.value.name.trim() !== '' && medicineForm.value.quantity > 0
})

const prescriptionForm = ref({
  diagnosis: '',
  usage: '',
  medicines: [] as any[]
})

// 病历表单
const medicalRecord = ref({
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  allergyHistory: '',
  physicalExam: '',
  diagnosis: '',
  treatment: ''
})

// 历史记录
const historyTab = ref('prescription')
const historyRecords = ref([
  {
    id: 1,
    date: '2024-01-15',
    diagnosis: '上呼吸道感染',
    medicines: [
      { name: '阿莫西林胶囊', dosage: '每日3次，每次1粒' },
      { name: '布洛芬缓释胶囊', dosage: '必要时服用' }
    ],
    examinations: [{ item: '血常规', status: '白细胞偏高' }],
    type: 'prescription'
  },
  {
    id: 2,
    date: '2023-08-20',
    diagnosis: '急性胃肠炎',
    medicines: [
      { name: '蒙脱石散', dosage: '每日3次，每次1袋' }
    ],
    examinations: [],
    type: 'visit'
  },
  {
    id: 3,
    date: '2023-05-10',
    diagnosis: '高血压复查',
    medicines: [
      { name: '氨氯地平片', dosage: '每日1次，每次1片' }
    ],
    examinations: [{ item: '血压监测', status: '140/90mmHg' }],
    type: 'exam'
  }
])

const filteredHistoryRecords = computed(() => {
  return historyRecords.value.filter(r => {
    if (historyTab.value === 'prescription') return r.type === 'prescription'
    if (historyTab.value === 'visit') return r.type === 'visit'
    if (historyTab.value === 'exam') return r.type === 'exam'
    return true
  })
})

// 检查单表单
const examinationForm = ref({
  items: [] as string[],
  site: '',
  purpose: '',
  urgency: 'normal',
  remark: ''
})

// 检查项目分类
const examCategories = [
  {
    name: '实验室检查',
    items: [
      { label: '血常规', value: 'blood_count' },
      { label: '尿常规', value: 'urine_count' },
      { label: '大便常规', value: 'stool_count' },
      { label: '生化全项', value: 'biochemistry' },
      { label: '凝血功能', value: 'coagulation' },
      { label: '肝功能', value: 'liver_function' },
      { label: '肾功能', value: 'kidney_function' },
      { label: '血脂', value: 'lipids' },
      { label: '血糖', value: 'glucose' }
    ]
  },
  {
    name: '影像学检查',
    items: [
      { label: 'X光', value: 'xray' },
      { label: 'CT', value: 'ct' },
      { label: 'MRI', value: 'mri' },
      { label: 'B超', value: 'ultrasound' },
      { label: '彩超', value: 'color_doppler' }
    ]
  },
  {
    name: '功能检查',
    items: [
      { label: '心电图', value: 'ecg' },
      { label: '动态心电图', value: 'holter' },
      { label: '肺功能', value: 'pulmonary' },
      { label: '脑电图', value: 'eeg' }
    ]
  }
]

// 紧急程度选项
const urgencyOptions = [
  { label: '普通', value: 'normal', class: 'normal' },
  { label: '急诊', value: 'urgent', class: 'urgent' },
  { label: '加急', value: 'emergency', class: 'emergency' }
]

// 高级选项显示控制
const showExamAdvanced = ref(false)

// 获取检查标签
function getExamLabel(value: string) {
  for (const cat of examCategories) {
    const item = cat.items.find(i => i.value === value)
    if (item) return item.label
  }
  return value
}

// 移除检查项目
function removeExamItem(value: string) {
  const idx = examinationForm.value.items.indexOf(value)
  if (idx > -1) {
    examinationForm.value.items.splice(idx, 1)
  }
}

// 确认添加药品
function confirmAddMedicine() {
  if (!canAddMedicine.value) return
  
  // 检查是否已达到上限（包含患者审核通过的药品）
  if (totalMedicineCount.value >= 5) {
    showToast('最多可添加5个药品', 'warning')
    return
  }
  
  const med = {
    name: medicineForm.value.name,
    spec: medicineForm.value.spec || '未填写规格',
    quantity: `${medicineForm.value.quantity}${medicineForm.value.unit}`,
    usage: generatedUsage.value
  }
  
  prescriptionMedicines.value.push(med)
  
  // 重置表单
  medicineForm.value = {
    name: '',
    spec: '',
    quantity: 1,
    unit: '盒',
    usageTimes: 3,
    usageDose: 1,
    usageUnit: '片',
    timing: []
  }
  
  // 折叠表单
  showAddMedicineForm.value = false
  
  showToast('药品已添加', 'success')
}

// 移除药品
function removeMedicine(idx: number) {
  prescriptionMedicines.value.splice(idx, 1)
  showToast('药品已移除', 'info')
}

// 切换服用时间
function toggleTiming(value: string) {
  const idx = medicineForm.value.timing.indexOf(value)
  if (idx > -1) {
    medicineForm.value.timing.splice(idx, 1)
  } else {
    medicineForm.value.timing.push(value)
  }
}

// ==================== 工具函数 ====================
function getAvatar(msg: any) {
  return msg.role === 'doctor' ? doctorAvatar.value : patientInfo.value.avatar
}

function formatContent(content: string) {
  return content.replace(/\n/g, '<br>')
}

// 处理消息中的图片点击
function handleMessageClick(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (target.tagName === 'IMG' && target.classList.contains('chat-image')) {
    const imgSrc = target.getAttribute('src')
    if (imgSrc) {
      previewImages.value = [imgSrc]
      showImagePreview.value = true
    }
  }
}

function getTagClass(tag: string): string {
  if (tag.includes('复诊')) return 'tag-primary'
  if (tag.includes('高血压')) return 'tag-warning'
  if (tag.includes('糖尿病')) return 'tag-danger'
  return 'tag-default'
}

function autoResize(event: Event) {
  const el = event.target as HTMLTextAreaElement
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}

function addSystemCardMessage(content: string, cardData: any) {
  messages.value.push({
    id: 'SYS_' + Date.now(),
    content: content,
    role: 'system',
    time: new Date().toLocaleTimeString('zh-CN'),
    isCard: true,
    cardData: cardData
  })
  scrollToBottom()
}

function addSystemMessage(content: string) {
  messages.value.push({
    id: 'SYS_' + Date.now(),
    content: content,
    role: 'system',
    time: new Date().toLocaleTimeString('zh-CN')
  })
  scrollToBottom()
}

// ==================== 工作区控制 ====================
function openWorkspace(key: string) {
  activeWorkspace.value = key
  showMoreMenu.value = false
  if (isMobile.value) {
    workspaceMobileOpen.value = true
  }
}

// ==================== 初始化 ====================
async function initIM() {
  try {
    loading.value = true
    error.value = ''

    const doctorId = doctorStore.doctorInfo?.id || route.query.doctorId as string || 'DOC001'
    console.log('[Chat] 开始初始化IM, doctorId:', doctorId)
    
    const success = await imService.init(doctorId, 'doctor')
    console.log('[Chat] IM初始化结果:', success)

    if (!success) {
      console.error('[Chat] IM SDK初始化失败')
      throw new Error('IM SDK初始化失败')
    }

    // 监听SDK Ready状态变化
    handleSdkReady = () => {
      console.log('[Chat] IM SDK已就绪')
      imConnected.value = true
    }
    imService.on('onSdkReady', handleSdkReady)

    // 监听SDK断开连接
    handleSdkNotReady = () => {
      console.warn('[Chat] IM SDK连接断开')
      imConnected.value = false
      showToast('IM连接已断开', 'warning')
    }
    imService.on('onSdkNotReady', handleSdkNotReady)

    // 监听消息接收
    imService.on('onMessageReceived', handleNewMessage)

    messages.value = []
    const now = new Date()
    addSystemMessage(`${consultationInfo.value.type} · ${now.toLocaleDateString('zh-CN')} ${now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })} · 问诊开始`)

    // 优先从后端 API 加载消息（保证两端数据一致）
    let hasMessages = false
    try {
      const apiMessages = await getConsultationMessages(consultationId)
      console.log('[Chat] 从后端API获取到消息数量:', apiMessages?.length || 0)

      if (apiMessages && apiMessages.length > 0) {
        hasMessages = true
        for (const msg of apiMessages) {
          messages.value.push({
            id: msg.id,
            content: msg.content,
            role: msg.sender === 'doctor' ? 'doctor' : 'patient',
            time: msg.time || new Date().toLocaleTimeString('zh-CN'),
            avatar: msg.sender === 'doctor' ? doctorAvatar.value : patientInfo.value.avatar
          })
        }
      }
    } catch (apiError: any) {
      console.error('[Chat] 从后端API加载消息失败:', apiError)
    }

    // 如果后端API没有消息，尝试从 TIM SDK 加载
    if (!hasMessages) {
      const conversationId = `C2C_patient_${imPatientUserId}`
      console.log('[Chat] 尝试从TIM加载消息, conversationId:', conversationId)

      try {
        const msgList = await imService.getMessageList(conversationId, 20)
        console.log('[Chat] 从TIM获取到消息数量:', msgList?.length || 0)

        if (msgList && msgList.length > 0) {
          hasMessages = true
          for (const msg of msgList) {
            const chatMsg = imService.convertToChatMessage(
              imService.convertMessage(msg),
              patientInfo.value.avatar,
              doctorAvatar.value
            )
            messages.value.push({
              id: chatMsg.id,
              content: chatMsg.content,
              role: chatMsg.type,
              time: chatMsg.time,
              avatar: chatMsg.avatar
            })
          }
        }
      } catch (timError: any) {
        console.warn('[Chat] 从TIM加载消息失败:', timError.message)
      }
    }

    // 如果都没有消息，加载默认消息
    if (!hasMessages) {
      console.log('[Chat] 没有历史消息，加载默认消息')
      messages.value.push({
        id: 'm1',
        content: `医生您好，我最近${preInquiry.value.chiefComplaint || consultationInfo.value.symptom || '有些不适'}，请问应该怎么办？`,
        role: 'patient',
        time: new Date().toLocaleTimeString('zh-CN')
      })
    }

    // 标记TIM消息已读
    try {
      const conversationId = `C2C_patient_${imPatientUserId}`
      await imService.setMessageRead(conversationId)
    } catch (e: any) {
      console.warn('[Chat] 标记已读失败:', e.message)
    }

    scrollToBottom()
  } catch (e: any) {
    console.error('[Chat] 初始化失败:', e)
    error.value = e.message || '连接失败'
    imConnected.value = false
    loadFallbackMessages()
  } finally {
    loading.value = false
  }
}

function handleNewMessage(msg: any) {
  if (!msg) return
  const converted = imService.convertMessage(msg)
  // 去重：检查是否已存在相同ID的消息
  if (messages.value.some(m => m.id === converted.id)) {
    console.log('[Chat] 跳过重复消息:', converted.id)
    return
  }
  const chatMsg = imService.convertToChatMessage(converted, patientInfo.value.avatar, doctorAvatar.value)

  messages.value.push({
    id: chatMsg.id,
    content: chatMsg.content,
    role: chatMsg.type,
    time: chatMsg.time,
    avatar: chatMsg.avatar
  })
  scrollToBottom()
}

function loadFallbackMessages() {
  messages.value = []
  const now = new Date()
  addSystemMessage(`${consultationInfo.value.type} · ${now.toLocaleDateString('zh-CN')} ${now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })} · 问诊开始`)

  messages.value.push({
    id: 'm1',
    content: `医生您好，我最近${preInquiry.value.chiefComplaint}，请问应该怎么办？`,
    role: 'patient',
    time: new Date().toLocaleTimeString('zh-CN')
  })

  scrollToBottom()
}

// ==================== 消息收发 ====================
async function sendMessage() {
  const text = inputMessage.value.trim()
  if (!text || sending.value) return

  sending.value = true
  inputMessage.value = ''

  const tempId = 'temp_' + Date.now()
  const tempMsg = {
    id: tempId,
    content: text,
    role: 'doctor',
    time: new Date().toLocaleTimeString('zh-CN'),
    avatar: doctorAvatar.value
  }
  messages.value.push(tempMsg)
  scrollToBottom()

  // 优先通过后端 API 持久化消息（保证两端数据同步）
  try {
    const result = await sendConsultationMessage(consultationId, { type: 'text', content: text })
    console.log('[Chat] 消息已通过后端API保存')
    // 用后端返回的真实ID替换临时ID
    if (result && result.id) {
      const idx = messages.value.findIndex(m => m.id === tempId)
      if (idx !== -1) {
        messages.value[idx].id = result.id
      }
    }
  } catch (apiError: any) {
    console.error('[Chat] 后端API保存消息失败:', apiError)
  }

  // 同时通过 TIM SDK 实时推送（如果已连接）
  if (imConnected.value) {
    try {
      const conversationId = `C2C_patient_${imPatientUserId}`
      await imService.sendTextMessage(conversationId, text)
      console.log('[Chat] TIM实时推送成功')
    } catch (timError: any) {
      console.warn('[Chat] TIM推送失败（消息已通过API保存）:', timError.message)
    }
  } else {
    console.warn('[Chat] IM未连接，消息仅通过API保存')
  }

  sending.value = false
}

function sendQuickReply(reply: string) {
  inputMessage.value = reply
  sendMessage()
}

function scrollToBottom() {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

// 定时轮询后端 API 获取新消息（保证两端消息同步，不依赖 TIM 实时推送）
async function pollNewMessages() {
  try {
    const apiMessages = await getConsultationMessages(consultationId)
    if (!apiMessages || !apiMessages.length) return

    for (const m of apiMessages) {
      // 去重：只追加本地没有的消息
      if (messages.value.some(local => local.id === m.id)) continue

      messages.value.push({
        id: m.id,
        content: m.content,
        role: m.sender === 'doctor' ? 'doctor' : 'patient',
        time: m.time || new Date().toLocaleTimeString('zh-CN'),
        avatar: m.sender === 'doctor' ? doctorAvatar.value : patientInfo.value.avatar
      })
    }
    scrollToBottom()
  } catch (e: any) {
    console.warn('[Chat] 轮询新消息失败:', e.message)
  }
}

function startPolling() {
  stopPolling()
  pollingTimer.value = setInterval(pollNewMessages, 5000)
}

function stopPolling() {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}

function retryInit() { initIM() }
function goBack() { router.push('/consultation') }

async function endConsultation() {
  if (!confirm('确定要结束本次问诊吗？结束后将无法继续发送消息。')) {
    return
  }

  try {
    // 调用后端API结束问诊
    await completeConsultation(consultationId)
    
    // 更新本地状态
    consultationInfo.value.status = 'completed'
    addSystemMessage('问诊已结束')
    showToast('问诊已结束', 'success')
    
    console.log('[Chat] 问诊已结束:', consultationId)
  } catch (error: any) {
    console.error('[Chat] 结束问诊失败:', error)
    showToast('结束问诊失败: ' + (error.message || '未知错误'), 'error')
  }
}

// ==================== 图片上传 ====================
function handleImageClick() { fileInput.value?.click() }

async function handleFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) { showToast('图片不能超过5MB', 'error'); return }

  sending.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('purpose', 'medical')

    const uploadRes = await fetch('/api/v1/im/upload', { method: 'POST', body: formData })
    if (!uploadRes.ok) throw new Error('上传失败')

    messages.value.push({
      id: 'IMG_' + Date.now(),
      content: `[图片] ${file.name}`,
      role: 'doctor',
      time: new Date().toLocaleTimeString('zh-CN'),
      imageUrl: URL.createObjectURL(file)
    })
    scrollToBottom()
  } catch (e: any) {
    showToast('上传失败: ' + e.message, 'error')
  } finally {
    sending.value = false
    if (fileInput.value) fileInput.value.value = ''
  }
}

function viewImages() {
  previewImages.value = preInquiry.value.images || []
  showImagePreview.value = true
}

// ==================== 语音功能 ====================
function handleVoiceClick() { showVoiceModal.value = true }

function closeVoiceModal() {
  stopRecording()
  showVoiceModal.value = false
  recordedBlob.value = null
  recordTime.value = 0
}

async function toggleRecording() {
  if (isRecording.value) {
    stopRecording()
  } else {
    startRecording()
  }
}

async function startRecording() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder = new MediaRecorder(stream)
    recordedChunks = []

    mediaRecorder.ondataavailable = (e) => {
      if (e.data.size > 0) recordedChunks.push(e.data)
    }

    mediaRecorder.onstop = () => {
      recordedBlob.value = new Blob(recordedChunks, { type: 'audio/webm' })
      stream.getTracks().forEach(t => t.stop())
    }

    mediaRecorder.start()
    isRecording.value = true
    recordTime.value = 0

    recordTimer = setInterval(() => { recordTime.value++ }, 1000)
  } catch (e: any) {
    showToast('无法访问麦克风: ' + e.message, 'warning')
  }
}

function stopRecording() {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }
  isRecording.value = false
  if (recordTimer) {
    clearInterval(recordTimer)
    recordTimer = null
  }
}

async function sendVoiceMessage() {
  if (!recordedBlob.value) return

  sending.value = true
  try {
    const formData = new FormData()
    formData.append('file', recordedBlob.value, 'voice.webm')
    formData.append('purpose', 'medical')

    const res = await fetch('/api/v1/im/upload', { method: 'POST', body: formData })
    if (!res.ok) throw new Error('发送失败')

    messages.value.push({
      id: 'VOICE_' + Date.now(),
      content: `[语音] ${formatRecordTime(recordTime.value)}`,
      role: 'doctor',
      time: new Date().toLocaleTimeString('zh-CN')
    })
    scrollToBottom()
    closeVoiceModal()
  } catch (e: any) {
    showToast('发送失败: ' + e.message, 'error')
  } finally {
    sending.value = false
  }
}

function formatRecordTime(sec: number): string {
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

// ==================== 处方审核功能 ====================
// 通过单个药品
function approveMedicine(idx: number) {
  const med = submittedMedicines.value[idx]
  // 通过只是确认使用患者申请的药品，不额外占用额度
  // 因为患者提交的药品已经计入总数
  med.approved = true
  med.rejected = false
  showToast('药品审核通过', 'success')
}

// 取消通过
function cancelApproveMedicine(idx: number) {
  const med = submittedMedicines.value[idx]
  med.approved = false
  showToast('已取消通过', 'info')
}

// 批量通过所有待审核药品
function approveAllMedicines() {
  // 检查是否超过总数限制
  if (totalMedicineCount.value + pendingMedicines.value.length > 5) {
    showToast('药品数量将超过5个上限，无法全部通过', 'warning')
    return
  }
  
  let count = 0
  for (const med of submittedMedicines.value) {
    if (!med.approved && !med.rejected && !med.replaced) {
      med.approved = true
      count++
    }
  }
  
  if (count > 0) {
    showToast(`${count}个药品审核通过`, 'success')
  }
}

// 当前正在替换的药品索引
// 添加药品表单显示控制
const showAddMedicineForm = ref(false)

// 当前正在替换的药品索引
const replacingIndex = ref<number | null>(null)

// 替换患者提交的药品
function replaceMedicine(idx: number) {
  const med = submittedMedicines.value[idx]
  if (med.rejected) {
    showToast('该药品已被驳回，无法替换', 'warning')
    return
  }
  
  // 保存正在替换的索引
  replacingIndex.value = idx
  
  // 在表单中预填充相似药品
  const replaceMed = medicineDatabase.find(m => 
    m.name.toLowerCase().includes(med.name.toLowerCase().replace('胶囊', '').replace('片', '').replace('缓释', ''))
  )
  
  if (replaceMed) {
    medicineForm.value.name = replaceMed.name
    medicineForm.value.spec = replaceMed.spec
    medicineForm.value.usageTimes = replaceMed.usageTimes
    medicineForm.value.usageDose = replaceMed.usageDose
    medicineForm.value.usageUnit = replaceMed.usageUnit
    medicineForm.value.timing = [...replaceMed.timing]
  } else {
    medicineForm.value.name = ''
    medicineForm.value.spec = ''
    medicineForm.value.usageTimes = 3
    medicineForm.value.usageDose = 1
    medicineForm.value.usageUnit = '片'
    medicineForm.value.timing = []
  }
  
  // 标记原药品为已替换（这样它就不会计入总数）
  med.replaced = true
  med.replacedWith = '替换中...'
  med.approved = false
  
  showToast('请在下方表单中填写替换药品，然后点击"确认替换"', 'info')
}

// 确认替换药品
function confirmReplaceMedicine() {
  if (replacingIndex.value === null) return
  
  const med = submittedMedicines.value[replacingIndex.value]
  
  // 验证表单
  if (!medicineForm.value.name.trim()) {
    showToast('请填写替换药品名称', 'warning')
    return
  }
  
  // 更新替换信息
  med.replacedWith = medicineForm.value.name
  
  // 将替换的药品添加到医生药品列表
  const newMed = {
    name: medicineForm.value.name,
    spec: medicineForm.value.spec || '未填写规格',
    quantity: `${medicineForm.value.quantity}${medicineForm.value.unit}`,
    usage: generatedUsage.value
  }
  prescriptionMedicines.value.push(newMed)
  
  // 重置表单
  medicineForm.value = {
    name: '',
    spec: '',
    quantity: 1,
    unit: '盒',
    usageTimes: 3,
    usageDose: 1,
    usageUnit: '片',
    timing: []
  }
  
  // 清除替换索引
  replacingIndex.value = null
  
  showToast('药品替换成功', 'success')
}

// 取消替换
function cancelReplaceMedicine() {
  if (replacingIndex.value === null) return
  
  const med = submittedMedicines.value[replacingIndex.value]
  med.replaced = false
  med.replacedWith = ''
  
  replacingIndex.value = null
  
  showToast('已取消替换', 'info')
}

// 驳回/取消驳回患者提交的药品
function rejectMedicine(idx: number) {
  const med = submittedMedicines.value[idx]
  med.rejected = !med.rejected
  if (med.rejected) {
    med.approved = false
    showToast('药品已驳回，数量已释放', 'info')
  } else {
    showToast('已取消驳回', 'info')
  }
}

function addMedicine() {
  showToast('添加药品功能开发中', 'info')
}

// 当前编辑的处方ID（null表示新建）
const editingPrescriptionId = ref<string | null>(null)

async function submitPrescription() {
  if (!prescriptionForm.value.diagnosis) {
    showToast('请填写诊断信息', 'error')
    return
  }

  if (totalMedicineCount.value === 0) {
    showToast('请至少添加一种药品', 'error')
    return
  }

  const approvedMeds = submittedMedicines.value.filter(m => m.approved && !m.rejected && !m.replaced)
  
  let content = `<strong>诊断：</strong>${prescriptionForm.value.diagnosis}<br/>`
  
  // 添加医生添加的药品
  if (prescriptionMedicines.value.length > 0) {
    content += `<br/><strong>医生开具药品：</strong><br/>`
    content += prescriptionMedicines.value.map((m, idx) => 
      `${idx + 1}. ${m.name} ${m.spec} <br/>   ${m.quantity} - ${m.usage}`
    ).join('<br/>')
  }
  
  // 添加患者提交的已审核药品
  if (approvedMeds.length) {
    content += `<br/><br/><strong>患者申请药品（已审核）：</strong><br/>`
    content += approvedMeds.map(m => `• ${m.name} ${m.spec} x${m.quantity}`).join('<br/>')
  }

  // 生成处方ID（编辑模式使用原ID，新建模式生成新ID）
  const prescriptionId = editingPrescriptionId.value || `RX${Date.now()}`

  addSystemCardMessage(content, {
    icon: 'prescription',
    title: editingPrescriptionId.value ? '处方已更新' : '处方已开具',
    prescriptionId: prescriptionId,
    editable: true
  })

  // 自动生成病历
  generateMedicalRecordFromPrescription()

  // 保存处方到历史记录
  savePrescriptionToHistory(prescriptionId)

  showToast(editingPrescriptionId.value ? '处方更新成功' : '处方开具成功，病历已自动生成', 'success')
  
  // 关闭工作区面板
  closeWorkspace()
  
  // 重置表单和编辑状态
  editingPrescriptionId.value = null
  prescriptionForm.value = { diagnosis: '', usage: '', medicines: [] }
  prescriptionMedicines.value = []
}

// 关闭工作区面板
function closeWorkspace() {
  if (isMobile.value) {
    workspaceMobileOpen.value = false
  }
  // 可选：在桌面端也可以关闭或切换标签
  // activeWorkspace.value = ''
}

// 保存处方到历史记录
function savePrescriptionToHistory(prescriptionId: string) {
  const approvedMeds = submittedMedicines.value.filter(m => m.approved && !m.rejected && !m.replaced)
  const allMedicines = [...prescriptionMedicines.value, ...approvedMeds]
  
  // 查找是否已有该处方
  const existingIndex = historyRecords.value.findIndex(r => r.id === prescriptionId)
  
  const record = {
    id: prescriptionId,
    date: new Date().toISOString().split('T')[0],
    diagnosis: prescriptionForm.value.diagnosis,
    medicines: allMedicines.map(m => ({ 
      name: m.name, 
      dosage: m.usage || '按医嘱服用' 
    })),
    examinations: [],
    type: 'prescription'
  }
  
  if (existingIndex >= 0) {
    // 更新现有记录
    historyRecords.value[existingIndex] = record
  } else {
    // 添加新记录
    historyRecords.value.unshift(record)
  }
}

// 编辑处方
function editPrescription(prescriptionId: string) {
  // 从历史记录中查找处方
  const record = historyRecords.value.find(r => r.id === prescriptionId)
  if (!record) {
    showToast('处方不存在', 'error')
    return
  }
  
  // 设置编辑状态
  editingPrescriptionId.value = prescriptionId
  prescriptionForm.value.diagnosis = record.diagnosis
  
  // 还原药品列表（简化处理，实际应该从完整数据还原）
  prescriptionMedicines.value = record.medicines.map(m => ({
    name: m.name,
    spec: '',
    quantity: '1盒',
    usage: m.dosage
  }))
  
  // 打开工作区面板
  activeWorkspace.value = 'prescription'
  if (isMobile.value) {
    workspaceMobileOpen.value = true
  }
  
  showToast('已进入处方编辑模式', 'info')
}

// 根据处方自动生成病历
function generateMedicalRecordFromPrescription() {
  const approvedMeds = submittedMedicines.value.filter(m => m.approved && !m.rejected && !m.replaced)
  const allMedicines = [...prescriptionMedicines.value, ...approvedMeds]
  
  // 填充病历信息
  medicalRecord.value.diagnosis = prescriptionForm.value.diagnosis
  medicalRecord.value.treatment = allMedicines.map(m => 
    `${m.name} ${m.spec || ''} ${m.quantity || ''} ${m.usage || ''}`
  ).join('；')
  
  // 生成病历摘要
  const recordSummary = {
    id: Date.now(),
    date: new Date().toISOString().split('T')[0],
    diagnosis: prescriptionForm.value.diagnosis,
    medicines: allMedicines.map(m => ({ 
      name: m.name, 
      dosage: m.usage || '按医嘱服用' 
    })),
    examinations: [],
    type: 'prescription'
  }
  
  // 添加到历史记录
  historyRecords.value.unshift(recordSummary)
  
  // 添加到会话消息
  const recordContent = `<strong>诊断：</strong>${prescriptionForm.value.diagnosis}<br/>
<strong>治疗方案：</strong>${medicalRecord.value.treatment}`
  
  addSystemCardMessage(recordContent, {
    icon: 'record',
    title: '病历已生成'
  })
}

function rejectPrescription() {
  showRejectModal.value = true
}

function confirmRejectPrescription() {
  if (!rejectReason.value.trim()) return
  
  addSystemCardMessage(
    `<strong>驳回原因：</strong>${rejectReason.value}`,
    { icon: 'warning', title: '处方已驳回' }
  )
  
  showToast('处方已驳回', 'warning')
  showRejectModal.value = false
  rejectReason.value = ''
}

// ==================== 病历功能 ====================
function saveRecordDraft() {
  localStorage.setItem(`medical_record_draft_${patientId}`, JSON.stringify(medicalRecord.value))
  showToast('病历草稿已保存', 'success')
}

function submitRecord() {
  if (!medicalRecord.value.diagnosis) {
    showToast('请填写诊断', 'error')
    return
  }

  const record = medicalRecord.value
  const content = `
    <strong>主诉：</strong>${record.chiefComplaint || '无'}<br/>
    <strong>诊断：</strong>${record.diagnosis}<br/>
    <strong>处理意见：</strong>${record.treatment || '待补充'}
  `

  addSystemCardMessage(content, { icon: 'record', title: '病历已创建' })
  showToast('病历提交成功', 'success')
  
  localStorage.removeItem(`medical_record_draft_${patientId}`)
  medicalRecord.value = {
    chiefComplaint: '',
    presentIllness: '',
    pastHistory: '',
    allergyHistory: '',
    physicalExam: '',
    diagnosis: '',
    treatment: ''
  }
}

// ==================== 检查单功能 ====================
function saveExaminationDraft() {
  localStorage.setItem(`examination_draft_${patientId}`, JSON.stringify(examinationForm.value))
  showToast('检查单草稿已保存', 'success')
}

function submitExamination() {
  if (examinationForm.value.items.length === 0) {
    showToast('请至少选择一项检查项目', 'error')
    return
  }

  const itemsMap: Record<string, string> = {
    blood_count: '血常规',
    urine_count: '尿常规',
    biochemistry: '生化全项',
    ecg: '心电图',
    ultrasound: 'B超',
    ct: 'CT',
    xray: 'X光',
    mri: 'MRI'
  }

  const itemList = examinationForm.value.items.map(item => `- ${itemsMap[item] || item}`).join('<br/>')
  const content = `<strong>检查项目：</strong><br/>${itemList}`

  addSystemCardMessage(content, { icon: 'exam', title: '检查申请单已生成' })
  showToast('检查单提交成功', 'success')
  
  localStorage.removeItem(`examination_draft_${patientId}`)
  examinationForm.value = {
    items: [],
    site: '',
    purpose: '',
    urgency: 'normal',
    remark: ''
  }
}

// ==================== 历史记录功能 ====================
function viewHistoryDetail(record: any) {
  showToast('查看详情功能开发中', 'info')
}

// 一键引用历史处方
function importHistoryPrescription(record: any) {
  // 检查是否还有空间添加药品
  const availableSlots = 5 - totalMedicineCount.value
  if (availableSlots <= 0) {
    showToast('药品数量已达上限，无法引用', 'warning')
    return
  }

  // 填充诊断
  prescriptionForm.value.diagnosis = record.diagnosis

  // 引用药品（最多填充剩余空间）
  let importedCount = 0
  for (const med of record.medicines) {
    if (totalMedicineCount.value >= 5) break

    // 尝试从数据库匹配药品信息
    const dbMed = medicineDatabase.find(m => med.name.includes(m.name) || m.name.includes(med.name.replace('胶囊', '').replace('片', '')))

    const newMed = {
      name: med.name,
      spec: dbMed?.spec || '未填写规格',
      quantity: dbMed ? `1${dbMed.usageUnit === '粒' || dbMed.usageUnit === '片' ? '盒' : '盒'}` : '1盒',
      usage: med.dosage
    }

    prescriptionMedicines.value.push(newMed)
    importedCount++
  }

  // 切换到处方面板
  activeWorkspace.value = 'prescription'

  if (importedCount > 0) {
    showToast(`成功引用 ${importedCount} 个药品`, 'success')
  } else {
    showToast('引用失败，请检查药品数量限制', 'warning')
  }
}

// ==================== 生命周期 ====================
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  
  // 先加载问诊详情，再初始化IM，然后开始轮询
  loadConsultationDetail().then(() => {
    initIM().then(() => startPolling())
  }).catch(() => {
    // 即使加载失败，也尝试初始化IM
    initIM().then(() => startPolling())
  })

  // 加载草稿
  const recordDraft = localStorage.getItem(`medical_record_draft_${patientId}`)
  if (recordDraft) {
    try { medicalRecord.value = JSON.parse(recordDraft) } catch (e) {}
  } else {
    // 没有草稿时，从患者预问诊信息初始化病历
    // 既往史优先从preInquiry获取，否则从patientInfo的慢性病拼接
    const patientPastHistory = preInquiry.value.pastHistory
      || (patientInfo.value.chronicDiseases?.length
        ? patientInfo.value.chronicDiseases.join('、') + '病史'
        : '')

    medicalRecord.value.pastHistory = patientPastHistory
    medicalRecord.value.allergyHistory = preInquiry.value.allergyHistory
      || (patientInfo.value.allergyHistory?.length
        ? patientInfo.value.allergyHistory.join('、')
        : '')
    medicalRecord.value.chiefComplaint = preInquiry.value.chiefComplaint || ''
  }
})

onUnmounted(() => {
  stopPolling()
  window.removeEventListener('resize', checkMobile)
  stopRecording()
  if (handleSdkReady) {
    imService.off('onSdkReady', handleSdkReady)
  }
  if (handleSdkNotReady) {
    imService.off('onSdkNotReady', handleSdkNotReady)
  }
  imService.off('onMessageReceived', handleNewMessage)
  imService.destroy()
})

watch(activeWorkspace, (newVal) => {
  if (isMobile.value && newVal) {
    workspaceMobileOpen.value = true
  }
})
</script>

<style scoped>
/* ============================================
   DrugMall 医生端问诊界面 - 互联网医院专业风格
   设计规范: 医疗专业感 + 简洁严谨
   ============================================ */

.consultation-layout {
  /* 色彩系统 - 医疗专业配色 */
  --primary: #2E7D32;
  --primary-light: #4CAF50;
  --primary-dark: #1B5E20;
  --primary-bg: #E8F5E9;
  
  --secondary: #1976D2;
  --success: #388E3C;
  --warning: #F57C00;
  --danger: #D32F2F;
  --info: #0288D1;
  
  --bg-page: #F5F7FA;
  --bg-sidebar: #FAFBFC;
  --bg-chat: #F5F7FA;
  --bg-panel: #FFFFFF;
  --bg-input: #FFFFFF;
  --bg-hover: #F0F2F5;
  
  --text-primary: #262626;
  --text-secondary: #595959;
  --text-tertiary: #8C8C8C;
  --text-muted: #BFBFBF;
  
  --border-color: #E8E8E8;
  --border-dark: #D9D9D9;
  
  --shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 4px 12px rgba(0, 0, 0, 0.12);
  --shadow-modal: 0 8px 24px rgba(0, 0, 0, 0.15);
  
  --radius: 8px;
  --radius-sm: 4px;
  --radius-lg: 12px;
  
  /* 字体系统 */
  --font-stack: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: var(--font-stack);
  color: var(--text-primary);
  background: var(--bg-page);
}

/* 屏幕阅读器专用类 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

/* ============================================
   顶部导航栏 - 56px高度，专业医疗风格
   ============================================ */
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  background: var(--bg-panel);
  border-bottom: 1px solid var(--border-color);
  position: relative;
  z-index: 100;
  flex-shrink: 0;
}

.nav-back {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  transition: all 150ms ease;
}

.nav-back:hover {
  background: var(--bg-hover);
}

.nav-back svg path {
  stroke: var(--text-primary);
}

.nav-title-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.3;
  margin: 0;
}

.nav-status {
  font-size: 12px;
  font-weight: 400;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-status::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.nav-status.online {
  color: var(--primary);
}

.nav-status.online::before {
  background: var(--primary);
}

.nav-status.offline {
  color: var(--text-tertiary);
}

.nav-status.offline::before {
  background: var(--text-tertiary);
}

.nav-end {
  padding: 8px 16px;
  border: none;
  background: var(--danger);
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 150ms ease;
}

.nav-end:hover:not(:disabled) {
  background: #C62828;
}

.nav-end:disabled {
  background: var(--text-muted);
  cursor: not-allowed;
}

/* ============================================
   三栏主体布局
   ============================================ */
.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* ============================================
   左侧：患者信息侧边栏 - 240px，浅灰蓝背景
   ============================================ */
.patient-sidebar {
  width: 240px;
  background: var(--bg-sidebar);
  border-right: 1px solid var(--border-color);
  padding: 16px;
  overflow-y: auto;
  flex-shrink: 0;
  transition: all 300ms ease;
}

.sidebar-toggle {
  display: none;
  width: 100%;
  padding: 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  margin-bottom: 8px;
}

.patient-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.patient-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--bg-panel);
  box-shadow: var(--shadow);
}

.patient-basic {
  flex: 1;
  min-width: 0;
}

.patient-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.patient-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.tag-primary {
  background: var(--primary-bg);
  color: var(--primary);
}

.tag-warning {
  background: #FFF3E0;
  color: var(--warning);
}

.tag-danger {
  background: #FFEBEE;
  color: var(--danger);
}

.tag-default {
  background: var(--bg-hover);
  color: var(--text-secondary);
}

.info-section {
  margin-bottom: 20px;
}

.info-section:last-of-type {
  margin-bottom: 0;
}

.info-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-tertiary);
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.allergy-tag {
  display: inline-block;
  padding: 4px 10px;
  background: #FFEBEE;
  color: var(--danger);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.disease-tag {
  display: inline-block;
  padding: 4px 10px;
  background: #FFF3E0;
  color: var(--warning);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.empty {
  color: var(--text-muted);
  font-size: 13px;
}

.inquiry-meta {
  background: var(--bg-panel);
  border-radius: var(--radius);
  padding: 12px;
  box-shadow: var(--shadow);
}

.meta-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 13px;
  line-height: 1.5;
}

.meta-row:last-child {
  margin-bottom: 0;
}

.meta-label {
  color: var(--text-tertiary);
  flex-shrink: 0;
  width: 48px;
}

.meta-value {
  color: var(--text-primary);
  flex: 1;
}

.view-history-btn {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  background: var(--bg-panel);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 150ms ease;
  margin-top: 20px;
  box-shadow: var(--shadow);
}

.view-history-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

/* ============================================
   中间：聊天区域 - 浅灰蓝背景，专业医疗风格
   ============================================ */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: var(--bg-chat);
}

/* 预问诊信息卡片 */
.pre-inquiry-card {
  background: var(--bg-panel);
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;
  box-shadow: var(--shadow);
}

.pre-inquiry-card.collapsed {
  border-bottom-color: transparent;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  cursor: pointer;
  user-select: none;
  transition: background 150ms ease;
}

.card-header:hover {
  background: var(--bg-hover);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-title svg {
  color: var(--primary);
}

.toggle-icon {
  font-size: 12px;
  color: var(--text-tertiary);
}

.card-body {
  padding: 0 16px 16px;
}

.info-row {
  display: flex;
  margin-bottom: 10px;
  font-size: 13px;
  line-height: 1.6;
}

.info-row .label {
  color: var(--text-tertiary);
  flex-shrink: 0;
  width: 70px;
}

.info-row .value {
  color: var(--text-primary);
  flex: 1;
}

.info-row .value.allergy {
  color: var(--danger);
  font-weight: 500;
}

.symptom-list {
  flex: 1;
}

.symptom-item {
  margin-bottom: 4px;
  color: var(--text-primary);
}

.attachment-row {
  margin-top: 12px;
}

.view-images-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid var(--border-color);
  background: var(--bg-sidebar);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: all 150ms ease;
}

.view-images-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

/* 消息列表 */
.msg-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 16px;
  scroll-behavior: smooth;
}

/* 状态容器 */
.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  gap: 12px;
  padding: 24px;
}

.state-text {
  font-size: 14px;
  color: var(--text-secondary);
  max-width: 260px;
  text-align: center;
  line-height: 1.6;
}

.state-subtext {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: -8px;
}

.state-text--error {
  color: var(--danger);
  font-weight: 500;
}

.state-loading .loading-spinner {
  width: 32px;
  height: 32px;
  border: 2.5px solid var(--border-color);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.75s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.retry-btn {
  display: inline-flex;
  align-items: center;
  padding: 10px 24px;
  background: var(--primary);
  color: #ffffff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 150ms ease;
}

.retry-btn:hover {
  background: var(--primary-dark);
}

/* 消息行 */
.msg-row {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  align-items: flex-start;
  animation: msg-in 200ms ease-out both;
}

@keyframes msg-in {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.msg-doctor {
  flex-direction: row-reverse;
}

.avatar-circle {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--bg-panel);
  box-shadow: var(--shadow);
}

.avatar-circle img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.bubble-col {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.bubble {
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}

.msg-patient .bubble {
  background: var(--bg-panel);
  color: var(--text-primary);
  border-radius: var(--radius) var(--radius) var(--radius) var(--radius-sm);
}

.msg-doctor .bubble {
  background: var(--primary-bg);
  color: var(--text-primary);
  border-radius: var(--radius) var(--radius) var(--radius-sm) var(--radius);
}

.bubble-time {
  font-size: 11px;
  color: var(--text-tertiary);
  padding: 0 4px;
}

.msg-doctor .bubble-time {
  text-align: right;
}

/* 系统消息 */
.sys-msg-wrap {
  display: flex;
  justify-content: center;
  margin: 12px 0;
  width: 100%;
}

.sys-text {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-panel);
  padding: 6px 14px;
  border-radius: 12px;
  max-width: 85%;
  text-align: center;
  box-shadow: var(--shadow);
}

.sys-card {
  background: var(--bg-panel);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  max-width: 420px;
  width: 100%;
  border: 1px solid var(--border-color);
}

.sys-card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  background: var(--primary);
  color: #fff;
}

.sys-card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.sys-card-title {
  font-size: 14px;
  font-weight: 600;
}

.sys-card-body {
  padding: 14px;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-secondary);
}

/* 快捷回复栏 */
.quick-reply-bar {
  display: flex;
  gap: 8px;
  padding: 10px 16px;
  overflow-x: auto;
  border-top: 1px solid var(--border-color);
  background: var(--bg-panel);
  flex-shrink: 0;
}

.quick-reply-btn {
  flex-shrink: 0;
  padding: 6px 14px;
  border: 1px solid var(--border-color);
  background: var(--bg-sidebar);
  border-radius: 12px;
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  white-space: nowrap;
  transition: all 150ms ease;
}

.quick-reply-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

/* 输入栏 */
.input-bar {
  background: var(--bg-panel);
  padding: 12px 16px;
  padding-bottom: max(12px, env(safe-area-inset-bottom));
  border-top: 1px solid var(--border-color);
  flex-shrink: 0;
}

.toolbar-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tool-btn {
  width: 42px;
  height: 42px;
  border: 1px solid var(--border-color);
  background: #ffffff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  transition: all 150ms ease;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.tool-btn:hover {
  background: var(--bg-hover);
  border-color: var(--primary);
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.15);
}

.tool-btn:hover svg path,
.tool-btn:hover svg circle,
.tool-btn:hover svg line,
.tool-btn:hover svg rect {
  stroke: var(--primary);
}

.input-wrap {
  flex: 1;
  min-width: 0;
  position: relative;
}

.input-wrap--focus .input-field {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(46, 125, 50, 0.1);
}

.input-field {
  width: 100%;
  border: 1px solid var(--border-color);
  background: var(--bg-input);
  resize: none;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
  max-height: 100px;
  font-family: inherit;
  color: var(--text-primary);
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  box-sizing: border-box;
  transition: all 150ms ease;
}

.input-field::placeholder {
  color: var(--text-muted);
}

.input-field:disabled {
  background: var(--bg-hover);
  color: var(--text-tertiary);
  cursor: not-allowed;
}

.send-btn {
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 150ms ease;
  background: var(--primary);
}

.send-btn:hover:not(:disabled) {
  background: var(--primary-dark);
}

.send-btn:disabled {
  background: var(--text-muted);
  cursor: not-allowed;
}

.send-spin {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

/* ============================================
   右侧：工作区面板 - 400px，白色背景
   ============================================ */
.workspace-panel {
  width: 400px;
  background: #ffffff;
  border-left: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.workspace-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  z-index: 300;
  background: #ffffff;
}

.workspace-tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color);
  background: #f8f9fa;
}

.tab-btn {
  flex: 1;
  padding: 12px 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-secondary);
  transition: all 150ms ease;
  position: relative;
}

.tab-btn:hover {
  color: var(--text-primary);
  background: var(--bg-hover);
}

.tab-btn.active {
  color: var(--primary);
  background: var(--bg-panel);
  font-weight: 500;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--primary);
}

.tab-btn--close {
  color: var(--text-tertiary);
  border-left: 1px solid var(--border-color);
  flex: 0 0 auto;
  padding: 12px 16px;
}

.tab-btn--close:hover {
  color: var(--danger);
  background: rgba(211, 47, 47, 0.05);
}

.tab-icon {
  font-size: 18px;
  color: currentColor;
}

.workspace-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #ffffff;
}

.panel-content {
  animation: fade-in 200ms ease-out;
  background: #ffffff;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--primary-bg);
  display: flex;
  align-items: center;
  gap: 10px;
}

.panel-title svg {
  color: var(--primary);
}

/* 通用表单样式 */
.section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 10px;
}

.required {
  color: var(--danger);
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: 14px;
  outline: none;
  transition: all 150ms ease;
  font-family: inherit;
  box-sizing: border-box;
  color: var(--text-primary);
  background: var(--bg-panel);
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(46, 125, 50, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 60px;
}

.form-select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238C8C8C' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 34px;
  cursor: pointer;
}

/* 按钮 - 统一规范 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 150ms ease;
  border: none;
  flex: 1;
}

.btn:disabled {
  background: var(--text-muted);
  cursor: not-allowed;
}

.btn-primary:disabled {
  background: var(--text-muted);
}

.btn-secondary:disabled {
  background: var(--bg-hover);
  color: var(--text-tertiary);
  border-color: var(--border-color);
}

.btn-danger:disabled {
  background: #FFCDD2;
}

.btn-primary {
  background: var(--primary);
  color: #ffffff;
}

.btn-primary:hover:not(:disabled) {
  background: var(--primary-dark);
}

.btn-secondary {
  background: var(--bg-panel);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover:not(:disabled) {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

.btn-danger {
  background: var(--danger);
  color: #ffffff;
}

.btn-danger:hover:not(:disabled) {
  background: #C62828;
}

.btn-text {
  padding: 4px 10px;
  border: none;
  background: transparent;
  color: var(--primary);
  font-size: 12px;
  cursor: pointer;
  transition: all 150ms ease;
  border-radius: var(--radius-sm);
}

.btn-text:hover {
  background: var(--primary-bg);
}

.btn-text-danger {
  color: var(--danger);
}

.btn-text-danger:hover {
  background: #FFEBEE;
}

.btn-add {
  width: 100%;
  padding: 12px;
  border: 1px dashed var(--border-dark);
  background: var(--bg-sidebar);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: all 150ms ease;
}

.btn-add:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

.panel-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
  margin-top: 20px;
}

/* 药品列表 */
.medicine-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.medicine-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
  background: var(--bg-sidebar);
  border-radius: var(--radius);
  border: 1px solid var(--border-color);
  transition: all 150ms ease;
}

.medicine-item.approved {
  border-color: var(--primary);
  background: var(--primary-bg);
}

.medicine-item.rejected {
  border-color: var(--danger);
  background: #FFEBEE;
}

.checkbox-wrap {
  position: relative;
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  cursor: pointer;
}

.checkbox-wrap input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  width: 20px;
  height: 20px;
  border: 2px solid var(--border-dark);
  border-radius: var(--radius-sm);
  transition: all 150ms ease;
}

.checkbox-wrap input:checked ~ .checkmark {
  background: var(--primary);
  border-color: var(--primary);
}

.checkmark::after {
  content: '';
  position: absolute;
  display: none;
  left: 5px;
  top: 1px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-wrap input:checked ~ .checkmark::after {
  display: block;
}

.med-info {
  flex: 1;
  min-width: 0;
}

.med-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.med-spec {
  font-size: 12px;
  color: var(--text-secondary);
}

.med-note {
  font-size: 11px;
  color: var(--text-tertiary);
  margin-top: 4px;
  font-style: italic;
}

.med-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  color: var(--text-muted);
  text-align: center;
}

.empty-state p {
  margin: 8px 0 0;
  font-size: 14px;
}

.empty-state .sub {
  font-size: 12px;
  margin-top: 4px;
}

/* 历史记录 */
.history-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.history-tabs button {
  flex: 1;
  padding: 8px;
  border: 1px solid var(--border-color);
  background: var(--bg-sidebar);
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 150ms ease;
}

.history-tabs button:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.history-tabs button.active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  padding: 12px;
  background: var(--bg-sidebar);
  border-radius: var(--radius);
  border: 1px solid var(--border-color);
  transition: all 150ms ease;
}

.history-item:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow);
}

.history-date {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-bottom: 6px;
}

.history-diagnosis {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.history-detail {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.med-line, .exam-line {
  margin-bottom: 2px;
}

/* 检查单复选框 */
.check-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.check-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: var(--bg-sidebar);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 13px;
  color: var(--text-secondary);
  transition: all 150ms ease;
  user-select: none;
}

.check-item:hover {
  border-color: var(--primary);
}

.check-item.checked {
  border-color: var(--primary);
  background: var(--primary-bg);
  color: var(--text-primary);
}

.check-box {
  width: 16px;
  height: 16px;
  border: 2px solid var(--border-dark);
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  position: relative;
  transition: all 150ms ease;
}

.check-item.checked .check-box {
  background: var(--primary);
  border-color: var(--primary);
}

.check-item.checked .check-box::after {
  content: '';
  position: absolute;
  left: 3px;
  top: 0;
  width: 4px;
  height: 8px;
  border: solid #fff;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

/* 检查建议页面优化样式 */
.exam-summary {
  background: var(--primary-bg);
  border: 1px solid var(--primary);
  border-radius: var(--radius);
  padding: 12px 16px;
  margin-bottom: 16px;
}

.summary-title {
  font-size: 13px;
  color: var(--primary);
  font-weight: 600;
  margin-bottom: 8px;
}

.summary-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.summary-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #fff;
  border: 1px solid var(--primary);
  border-radius: 12px;
  font-size: 12px;
  color: var(--text-primary);
}

.tag-remove {
  width: 16px;
  height: 16px;
  border: none;
  background: var(--text-tertiary);
  color: #fff;
  border-radius: 50%;
  font-size: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.tag-remove:hover {
  background: var(--danger);
}

.exam-categories {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.exam-category {
  background: var(--bg-sidebar);
  border-radius: var(--radius);
  padding: 12px;
}

.category-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.category-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.check-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: 16px;
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 150ms ease;
}

.check-chip:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.check-chip.checked {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}

.collapse-panel {
  background: var(--bg-sidebar);
  border-radius: var(--radius);
  margin-bottom: 16px;
  overflow: hidden;
}

.collapse-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  transition: background 150ms ease;
}

.collapse-header:hover {
  background: var(--bg-hover);
}

.collapse-header svg {
  transition: transform 200ms ease;
}

.collapse-header svg.rotate {
  transform: rotate(180deg);
}

.collapse-content {
  padding: 0 16px 16px;
}

.urgency-options {
  display: flex;
  gap: 12px;
}

.urgency-option {
  flex: 1;
  cursor: pointer;
}

.urgency-option span {
  display: block;
  padding: 10px 16px;
  text-align: center;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-secondary);
  transition: all 150ms ease;
}

.urgency-option span.normal {
  background: #fff;
}

.urgency-option span.urgent {
  background: #FFF3E0;
  color: #FF9800;
  border-color: #FF9800;
}

.urgency-option span.emergency {
  background: #FFEBEE;
  color: #D32F2F;
  border-color: #D32F2F;
}

.urgency-option.active span.normal {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}

.urgency-option.active span.urgent {
  background: #FF9800;
  color: #fff;
}

.urgency-option.active span.emergency {
  background: #D32F2F;
  color: #fff;
}

/* 药品卡片样式 */
.medicine-card {
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  margin-bottom: 12px;
  overflow: hidden;
}

.med-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: var(--primary-bg);
  border-bottom: 1px solid var(--border-color);
}

.med-index {
  width: 20px;
  height: 20px;
  background: var(--primary);
  color: #fff;
  border-radius: 50%;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.med-name {
  flex: 1;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.btn-remove {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 150ms ease;
}

.btn-remove:hover {
  background: var(--danger);
  color: #fff;
}

.med-card-body {
  padding: 12px 16px;
}

.med-info-row {
  display: flex;
  margin-bottom: 6px;
  font-size: 13px;
}

.med-info-row:last-child {
  margin-bottom: 0;
}

.med-label {
  color: var(--text-tertiary);
  width: 50px;
  flex-shrink: 0;
}

.med-value {
  color: var(--text-primary);
  flex: 1;
}

/* 药品表单样式 */
.medicine-form-section {
  background: var(--bg-sidebar);
}

.medicine-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.form-col {
  flex: 1;
}

.form-col label {
  display: block;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.quantity-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.qty-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-color);
  background: #fff;
  border-radius: var(--radius-sm);
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 150ms ease;
}

.qty-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.quantity-input input {
  width: 60px;
  text-align: center;
  padding: 6px 8px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: 14px;
}

.usage-section {
  background: #fff;
  border-radius: var(--radius);
  padding: 12px;
}

.usage-section label {
  display: block;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 10px;
}

.usage-builder {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.usage-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.usage-text {
  font-size: 14px;
  color: var(--text-primary);
}

.usage-select {
  padding: 6px 10px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: 13px;
  background: #fff;
  min-width: 60px;
}

.usage-input {
  width: 50px;
  padding: 6px 8px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: 13px;
  text-align: center;
}

.timing-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.timing-tag {
  padding: 4px 10px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 12px;
  color: var(--text-secondary);
  background: #fff;
  cursor: pointer;
  transition: all 150ms ease;
}

.timing-tag:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.timing-tag.checked {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}

.usage-preview {
  background: var(--primary-bg);
  border-radius: var(--radius-sm);
  padding: 10px 12px;
  font-size: 13px;
}

.preview-label {
  color: var(--text-tertiary);
}

.preview-text {
  color: var(--primary);
  font-weight: 500;
}

.btn-add-med {
  margin-top: 8px;
}

.btn-add-med:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.limit-warning {
  font-size: 12px;
  color: var(--warning);
  text-align: center;
  margin-top: 8px;
  margin-bottom: 0;
}

.limit-hint {
  font-size: 12px;
  color: var(--warning);
  font-weight: normal;
  margin-left: 8px;
}

.limit-reached {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  color: var(--text-tertiary);
  font-size: 14px;
}

.limit-reached svg {
  color: var(--warning);
}

/* 添加药品折叠按钮 */
.add-medicine-trigger {
  padding: 0;
  overflow: hidden;
}

.btn-add-medicine-fold {
  width: 100%;
  padding: 16px;
  border: none;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--primary);
  font-size: 14px;
  cursor: pointer;
  transition: background 150ms ease;
}

.btn-add-medicine-fold:hover {
  background: var(--primary-bg);
}

.btn-add-medicine-fold svg {
  color: var(--primary);
}

.count-badge {
  font-size: 11px;
  color: var(--text-tertiary);
  background: var(--bg-sidebar);
  padding: 2px 8px;
  border-radius: 10px;
}

/* 批量操作样式 */
.batch-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid var(--border-color);
  margin-top: 8px;
}

.limit-tip {
  font-size: 12px;
  color: var(--warning);
}

/* 患者药品状态样式 */
.patient-med {
  display: flex;
  flex-direction: column;
  padding: 12px 16px;
  border-left: 3px solid transparent;
}

.patient-med.pending {
  border-left-color: var(--warning);
}

.patient-med.approved {
  border-left-color: var(--success);
}

.patient-med.rejected {
  border-left-color: var(--danger);
  opacity: 0.7;
}

.patient-med.replaced {
  border-left-color: var(--info);
}

.patient-med-content {
  flex: 1;
}

.med-info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 6px;
}

.med-info-header .med-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
}

.med-status-icon {
  flex-shrink: 0;
}

.status-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.status-badge.pending {
  background: #FFF3E0;
  color: #FF9800;
}

.status-badge.success {
  background: #E8F5E9;
  color: #2E7D32;
}

.status-badge.danger {
  background: #FFEBEE;
  color: #D32F2F;
}

.status-badge.warning {
  background: #E3F2FD;
  color: #1976D2;
}

/* 按钮尺寸 */
.btn-sm {
  padding: 4px 12px;
  font-size: 12px;
}

/* 紧凑版操作按钮 */
.med-actions-compact {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid var(--border-light);
}

.med-actions-compact.single {
  justify-content: flex-end;
}

.btn-icon-text {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 150ms ease;
  background: #fff;
}

.btn-icon-text svg {
  flex-shrink: 0;
}

.btn-icon-text span {
  font-size: 12px;
}

.btn-icon-text:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-approve {
  color: var(--success);
  border-color: var(--success);
}

.btn-approve:hover:not(:disabled) {
  background: var(--success);
  color: #fff;
}

.btn-replace {
  color: var(--info);
  border-color: var(--info);
}

.btn-replace:hover:not(:disabled) {
  background: var(--info);
  color: #fff;
}

.btn-reject {
  color: var(--danger);
  border-color: var(--danger);
}

.btn-reject:hover {
  background: var(--danger);
  color: #fff;
}

.btn-text-small {
  padding: 4px 8px;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: 12px;
  cursor: pointer;
  transition: color 150ms ease;
}

.btn-text-small:hover {
  color: var(--text-primary);
}

.btn-success {
  background: var(--success);
  color: #fff;
}

.btn-success:hover {
  background: #1B5E20;
}

.btn-danger {
  background: var(--danger);
  color: #fff;
}

.btn-danger:hover {
  background: #B71C1C;
}

/* 简化版药品表单样式 */
.medicine-form-simple {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-line {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-line label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.form-line.compact {
  flex-direction: row;
  gap: 12px;
}

.form-group-half {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.med-name-input {
  font-size: 14px;
  padding: 10px 12px;
}

.qty-quick {
  display: flex;
  align-items: center;
  gap: 4px;
}

.qty-mini {
  width: 28px;
  height: 28px;
  border: 1px solid var(--border-color);
  background: #fff;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 150ms ease;
}

.qty-mini:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.qty-input-mini {
  width: 40px;
  height: 28px;
  text-align: center;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 13px;
  padding: 0 4px;
}

.unit-select-mini {
  height: 28px;
  padding: 0 8px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 12px;
  background: #fff;
  min-width: 50px;
}

.usage-quick-row {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.usage-select-mini {
  height: 32px;
  padding: 0 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 13px;
  background: #fff;
}

.usage-sep {
  font-size: 13px;
  color: var(--text-secondary);
}

.dose-input-mini {
  width: 50px;
  height: 32px;
  text-align: center;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 13px;
}

.timing-quick {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.timing-btn {
  padding: 4px 10px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 12px;
  color: var(--text-secondary);
  background: #fff;
  cursor: pointer;
  transition: all 150ms ease;
}

.timing-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.timing-btn.active {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 8px;
}

.usage-preview-mini {
  background: var(--primary-bg);
  border-radius: 4px;
  padding: 8px 12px;
  font-size: 13px;
}

.usage-preview-mini .preview-text {
  color: var(--primary);
  font-weight: 500;
}

.btn-add-quick {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  font-size: 14px;
}

.quick-fill-hint {
  font-size: 11px;
  color: var(--primary);
  font-weight: normal;
  margin-left: 8px;
  background: var(--primary-bg);
  padding: 2px 8px;
  border-radius: 10px;
}

.quick-fill-hint.warning {
  color: var(--warning);
  background: #FFF3E0;
}

/* 药品搜索联想 */
.med-search-wrap {
  position: relative;
}

.med-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid var(--border-color);
  border-top: none;
  border-radius: 0 0 var(--radius) var(--radius);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  z-index: 100;
  max-height: 200px;
  overflow-y: auto;
}

.med-suggestion-item {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid var(--border-light);
  transition: background 150ms ease;
}

.med-suggestion-item:last-child {
  border-bottom: none;
}

.med-suggestion-item:hover {
  background: var(--primary-bg);
}

.suggestion-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.suggestion-spec {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

.workspace-toggle {
  position: fixed;
  right: 16px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  border: none;
  background: var(--primary);
  color: #fff;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  font-size: 11px;
  cursor: pointer;
  box-shadow: var(--shadow-lg);
  z-index: 50;
  transition: all 150ms ease;
}

.workspace-toggle:hover {
  background: var(--primary-dark);
  transform: scale(1.05);
}

.workspace-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 100;
}

/* ============================================
   底部功能面板
   ============================================ */
.sheet-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 2000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.action-sheet {
  background: #ffffff;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  width: 100%;
  max-width: 520px;
  padding: 12px 20px 28px;
  animation: sheet-up 250ms ease-out both;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.15);
}

@keyframes sheet-up {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.sheet-handle {
  width: 40px;
  height: 3px;
  background: var(--border-color);
  border-radius: 2px;
  margin: 0 auto 16px;
}

.sheet-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px 8px;
}

.sheet-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  border: 1px solid var(--border-color);
  background: #ffffff;
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 150ms ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.sheet-item:hover {
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.15);
  transform: translateY(-2px);
}

/* 功能按钮图标 */
.sheet-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius);
  background: var(--primary-bg);
  color: var(--primary);
  margin-bottom: 4px;
  border: 1px solid rgba(46, 125, 50, 0.2);
}

.sheet-icon svg {
  width: 26px;
  height: 26px;
}

.sheet-label {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
}

/* ============================================
   弹窗样式
   ============================================ */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-mask.image-preview-mask {
  background: rgba(0, 0, 0, 0.9);
}

/* 语音录制弹窗 */
.voice-modal {
  background: var(--bg-panel);
  border-radius: var(--radius);
  padding: 28px 24px 20px;
  width: 320px;
  text-align: center;
  animation: modal-in 250ms ease-out both;
  box-shadow: var(--shadow-modal);
}

@keyframes modal-in {
  from { opacity: 0; transform: scale(0.94) translateY(16px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.voice-header {
  margin-bottom: 20px;
}

.voice-title {
  margin: 0 0 4px;
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.voice-subtitle {
  margin: 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.voice-visual {
  position: relative;
  height: 110px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.wave-rings {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
}

.wave-ring {
  position: absolute;
  border-radius: 50%;
  border: 2px solid var(--primary);
  opacity: 0;
  transition: all 350ms ease;
}

.wave-ring.active {
  opacity: 0.25;
  animation: ring-expand 2s ease-out infinite;
}

.wave-ring--1 { width: 64px; height: 64px; }
.wave-ring--1.active { animation-delay: 0s; }
.wave-ring--2 { width: 92px; height: 92px; }
.wave-ring--2.active { animation-delay: 0.4s; }
.wave-ring--3 { width: 120px; height: 120px; }
.wave-ring--3.active { animation-delay: 0.8s; }

@keyframes ring-expand {
  0% { transform: scale(0.85); opacity: 0.35; }
  100% { transform: scale(1.25); opacity: 0; }
}

.wave-bars {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 46px;
  z-index: 1;
}

.wave-bar {
  width: 5px;
  height: var(--height, 14px);
  background: var(--primary);
  border-radius: 2.5px;
  transition: height 140ms ease;
  opacity: 0.35;
}

.wave-bar.animating {
  animation: bar-wave 750ms ease-in-out infinite alternate;
  animation-delay: var(--delay, 0s);
  opacity: 1;
}

@keyframes bar-wave {
  0% { height: 10px; }
  100% { height: var(--height, 34px); }
}

.voice-timer {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.03em;
  margin-bottom: 20px;
}

.voice-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.voice-btn {
  padding: 9px 18px;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: all 150ms ease;
}

.voice-btn-inner {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.voice-btn--cancel {
  background: var(--bg-sidebar);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.voice-btn--cancel:hover {
  background: var(--bg-hover);
}

.voice-btn--record {
  background: var(--primary);
  color: #ffffff;
  padding: 11px 24px;
}

.voice-btn--record:hover {
  background: var(--primary-dark);
}

.voice-btn--recording {
  background: var(--danger);
  animation: recording-blink 1.5s ease-in-out infinite;
}

@keyframes recording-blink {
  0%, 100% { box-shadow: 0 0 0 0 rgba(211, 47, 47, 0.3); }
  50% { box-shadow: 0 0 0 8px rgba(211, 47, 47, 0.1); }
}

.voice-btn--send {
  background: var(--primary);
  color: #ffffff;
}

.voice-btn--send:hover {
  background: var(--primary-dark);
}

/* 图片预览 */
.image-preview {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.preview-close {
  position: absolute;
  top: -40px;
  right: 0;
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 24px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-images {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-img {
  max-width: 100%;
  max-height: 80vh;
  border-radius: var(--radius);
}

/* 驳回弹窗 */
.reject-modal {
  background: var(--bg-panel);
  border-radius: var(--radius);
  padding: 24px;
  width: 360px;
  max-width: 90vw;
  animation: modal-in 250ms ease-out both;
  box-shadow: var(--shadow-modal);
}

.reject-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px;
  color: var(--text-primary);
}

.reject-modal textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 80px;
  box-sizing: border-box;
  margin-bottom: 16px;
  transition: all 150ms ease;
}

.reject-modal textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(46, 125, 50, 0.1);
}

.reject-actions {
  display: flex;
  gap: 12px;
}

/* ============================================
   Toast通知
   ============================================ */
.toast-container {
  position: fixed;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 8px;
  pointer-events: none;
  width: max-content;
  max-width: calc(100vw - 32px);
}

.toast-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 18px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  box-shadow: var(--shadow-modal);
  pointer-events: auto;
  animation: toast-in 250ms ease-out both;
}

.toast-success { background: var(--primary); color: #ffffff; }
.toast-warning { background: var(--warning); color: #ffffff; }
.toast-error   { background: var(--danger); color: #ffffff; }
.toast-info    { background: var(--secondary); color: #ffffff; }

.toast-icon {
  font-size: 15px;
  width: 18px;
  text-align: center;
  flex-shrink: 0;
}

.toast-msg {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.toast-close {
  background: none;
  border: none;
  color: inherit;
  opacity: 0.7;
  cursor: pointer;
  font-size: 18px;
  padding: 0 4px;
  line-height: 1;
  transition: opacity 140ms ease;
  margin-left: 4px;
}

.toast-close:hover {
  opacity: 1;
}

@keyframes toast-in {
  from { opacity: 0; transform: translateY(-10px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.toast-anim-leave-active {
  transition: all 200ms ease-out;
}

.toast-anim-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.97);
}

/* ============================================
   过渡动画
   ============================================ */
.sheet-fade-enter-active { transition: opacity 200ms ease-out; }
.sheet-fade-leave-active { transition: opacity 150ms ease-in; }
.sheet-fade-enter-from, .sheet-fade-leave-to { opacity: 0; }

.modal-fade-enter-active { transition: opacity 220ms ease-out; }
.modal-fade-leave-active { transition: opacity 170ms ease-in; }
.modal-fade-enter-active .voice-modal,
.modal-fade-enter-active .reject-modal,
.modal-fade-enter-active .image-preview {
  transition: all 280ms cubic-bezier(0.32, 0.72, 0, 1);
}
.modal-fade-leave-active .voice-modal,
.modal-fade-leave-active .reject-modal,
.modal-fade-leave-active .image-preview {
  transition: all 200ms ease-in;
}
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .voice-modal,
.modal-fade-enter-from .reject-modal,
.modal-fade-enter-from .image-preview {
  transform: scale(0.94) translateY(16px);
}
.modal-fade-leave-to .voice-modal,
.modal-fade-leave-to .reject-modal,
.modal-fade-leave-to .image-preview {
  transform: scale(0.94) translateY(16px);
}

/* ============================================
   响应式适配
   ============================================ */
@media (max-width: 1023px) {
  .main-container {
    position: relative;
  }
  
  .patient-sidebar {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 50;
    transform: translateX(-100%);
    box-shadow: var(--shadow-lg);
  }
  
  .patient-sidebar.collapsed {
    transform: translateX(0);
  }
  
  .sidebar-toggle {
    display: block;
  }
  
  .workspace-panel {
    position: fixed;
    right: 0;
    top: 0;
    bottom: 0;
    z-index: 300;
    transform: translateX(100%);
    transition: transform 350ms cubic-bezier(0.32, 0.72, 0, 1);
    width: 85vw;
    max-width: 400px;
    box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
  }
  
  .workspace-panel.mobile-open {
    transform: translateX(0);
  }
  
  .workspace-mask {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 299;
  }
  
  .chat-area {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .top-nav {
    padding: 0 12px;
    height: 52px;
  }
  
  .nav-title {
    font-size: 16px;
  }
  
  .nav-end {
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .bubble-col {
    max-width: 80%;
  }
  
  .check-grid {
    grid-template-columns: 1fr;
  }
  
  .sheet-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 480px) {
  .msg-list {
    padding: 12px;
  }
  
  .bubble {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .avatar-circle {
    width: 36px;
    height: 36px;
  }
  
  .workspace-tabs .tab-label {
    display: none;
  }
  
  .tab-btn {
    padding: 12px;
  }
}

/* 减少动画偏好支持 */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* 高对比度模式支持 */
@media (forced-colors: active) {
  .consultation-layout { forced-color-adjust: auto; }
  .bubble, .btn, .form-group input, .check-item { border: 2px solid ButtonText; }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--border-dark);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--text-tertiary);
}
</style>

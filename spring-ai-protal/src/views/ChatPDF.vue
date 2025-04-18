<template>
  <div class="chat-pdf" :class="{ 'dark': isDark }">
    <div class="chat-container">
      <!-- 左侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <a href="#" class="logo-link" @click="handleLogoClick">
            <DocumentTextIcon class="logo" />
            <h1 class="title">ChatPDF</h1>
          </a>
        </div>

        <div class="history-list">
          <div class="history-header">
            <span>历史记录</span>
            <button class="new-chat-btn" @click="startNewChat">
              <PlusIcon class="icon" />
              新聊天
            </button>
          </div>
          <div 
            v-for="chat in chatHistory" 
            :key="chat.id"
            class="history-item"
            :class="{ 'active': currentChatId === chat.id }"
            @click="loadChat(chat.id)"
          >
            <DocumentTextIcon class="icon" />
            <span class="title">{{ chat.title || 'PDF对话' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 主要内容区域 -->
      <div class="chat-main">
        <!-- 未上传文件时显示上传界面 -->
        <div v-if="!currentPdfName" class="upload-welcome">
          <h1 class="main-title">
            与任何 <span class="highlight">PDF</span> 对话
          </h1>
          <div 
            class="drop-zone"
            @dragover.prevent="handleDragOver"
            @dragleave.prevent="handleDragLeave"
            @drop.prevent="handleDrop"
            :class="{ 
              'dragging': isDragging,
              'uploading': isUploading 
            }"
          >
            <div class="upload-content">
              <!-- 添加上传状态显示 -->
              <div v-if="isUploading" class="upload-status">
                <div class="spinner"></div>
                <div class="upload-progress">
                  <p class="status-text">正在上传文件...</p>
                  <p class="filename">{{ uploadingFileName }}</p>
                </div>
              </div>
              <template v-else>
                <DocumentArrowUpIcon class="upload-icon" />
                <p class="upload-text">点击上传，或将PDF拖拽到此处</p>
                <input 
                  type="file"
                  accept=".pdf"
                  @change="handleFileUpload"
                  :disabled="isUploading"
                  class="file-input"
                >
                <button 
                  class="upload-button"
                  :class="{ 'uploading': isUploading }"
                  @click="triggerFileInput"
                >
                  <ArrowUpTrayIcon class="icon" />
                  上传PDF
                </button>
              </template>
            </div>
          </div>
        </div>

        <!-- 已上传文件时显示分栏界面 -->
        <div v-else class="split-view">
          <!-- PDF 预览组件 -->
          <PDFViewer 
            :file="pdfFile"
            :fileName="currentPdfName"
          />

          <!-- 聊天区域 -->
          <div class="chat-view">
            <div class="messages" ref="messagesRef">
              <ChatMessage
                v-for="(message, index) in currentMessages"
                :key="index"
                :message="message"
                :is-stream="isStreaming && index === currentMessages.length - 1"
              />
            </div>
            
            <div class="input-area">
              <textarea
                v-model="userInput"
                @keydown.enter.prevent="sendMessage()"
                placeholder="请输入您的问题..."
                rows="1"
                ref="inputRef"
              ></textarea>
              <button 
                class="send-button" 
                @click="sendMessage()"
                :disabled="isStreaming || !userInput.trim()"
              >
                <PaperAirplaneIcon class="icon" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, onUnmounted } from 'vue'
import { useDark } from '@vueuse/core'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { 
  DocumentArrowUpIcon,
  DocumentTextIcon,
  PaperAirplaneIcon,
  ArrowUpTrayIcon,
  PlusIcon,
  ChevronLeftIcon,
  ChevronRightIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'
import { useRouter } from 'vue-router'
import PDFViewer from '../components/PDFViewer.vue'

const isDark = useDark()
const router = useRouter()
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const isUploading = ref(false)
const currentChatId = ref(null)
const currentMessages = ref([])
const chatHistory = ref([])
const currentPdfName = ref('')
const isDragging = ref(false)
const BASE_URL = 'http://localhost:8088'

// 配置 marked
marked.setOptions({
  breaks: true,
  gfm: true,
  sanitize: false
})

// 自动调整输入框高度
const adjustTextareaHeight = () => {
  const textarea = inputRef.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

// 添加下载状态
const isDownloadingPdf = ref(false)

// 添加 pdfFile ref
const pdfFile = ref(null)

// 修改资源清理函数
const cleanupResources = () => {
  currentPdfName.value = ''
  currentMessages.value = []
  pdfFile.value = null
  currentChatId.value = null
  isDownloadingPdf.value = false
  isUploading.value = false
  uploadingFileName.value = ''
  userInput.value = ''
  isStreaming.value = false
  
  // 重置输入框高度
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
  }
}

// 修改 logo 点击处理
const handleLogoClick = (event) => {
  event.preventDefault()
  cleanupResources()
  router.push('/')
}

// 修改 startNewChat 方法
const startNewChat = () => {
  try {
    // 清理所有状态
    cleanupResources()
    
    // 重置文件相关状态
    pdfFile.value = null
    currentPdfName.value = ''
    currentChatId.value = null
    
    // 重置消息
    currentMessages.value = []
    
    // 重置上传状态
    isUploading.value = false
    uploadingFileName.value = ''
    
    // 重置输入
    userInput.value = ''
    if (inputRef.value) {
      inputRef.value.style.height = 'auto'
    }
    
    // 重置滚动位置
    if (messagesRef.value) {
      messagesRef.value.scrollTop = 0
    }
  } catch (error) {
    console.error('开始新对话失败:', error)
  }
}

// 修改 loadChat 方法
const loadChat = async (chatId) => {
  if (!chatId) return
  
  cleanupResources()
  currentChatId.value = chatId
  
  try {
    // 加载消息历史
    const messages = await chatAPI.getChatMessages(chatId, 'pdf')
    currentMessages.value = messages.map(msg => ({
      ...msg,
      isMarkdown: msg.role === 'assistant'
    }))

    // 从服务器获取 PDF
    isDownloadingPdf.value = true
    const response = await fetch(`${BASE_URL}/ai/pdf/file/${chatId}`)
    if (!response.ok) throw new Error('获取 PDF 失败')
    
    // 获取文件名
    const contentDisposition = response.headers.get('content-disposition')
    let filename = 'document.pdf'
    if (contentDisposition) {
      const matches = contentDisposition.match(/filename=["']?([^"']+)["']?/)
      if (matches && matches[1]) {
        filename = decodeURIComponent(matches[1])
      }
    }
    
    // 更新当前文件名和历史记录中的标题
    currentPdfName.value = filename
    const chatIndex = chatHistory.value.findIndex(c => c.id === chatId)
    if (chatIndex !== -1) {
      chatHistory.value[chatIndex].title = filename
    }
    
    const blob = await response.blob()
    pdfFile.value = new File([blob], filename, { type: 'application/pdf' })
  } catch (error) {
    console.error('加载失败:', error)
    const errorMessage = {
      role: 'assistant',
      content: '加载失败，请重试。',
      timestamp: new Date(),
      isMarkdown: true
    }
    currentMessages.value.push(errorMessage)
  } finally {
    isDownloadingPdf.value = false
  }
}

// 加载聊天历史
const loadChatHistory = async () => {
  try {
    const history = await chatAPI.getChatHistory('pdf')
    chatHistory.value = history || []
    if (history && history.length > 0) {
      await loadChat(history[0].id)
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    chatHistory.value = []
  }
}

// 处理文件拖放
const handleDrop = async (event) => {
  event.preventDefault()
  isDragging.value = false
  
  const files = event.dataTransfer.files
  if (files.length === 0) return
  
  // 获取第一个文件
  const file = files[0]
  
  // 检查是否为 PDF 文件
  if (file.type !== 'application/pdf') {
    alert('请上传 PDF 文件')
    return
  }
  
  // 设置上传状态和文件名
  isUploading.value = true
  uploadingFileName.value = file.name
  
  try {
    // 创建 FormData
    const formData = new FormData()
    formData.append('file', file)
    
    // 生成临时 chatId 或使用现有的
    const uploadChatId = currentChatId.value || `pdf_${Date.now()}`
    
    // 发送上传请求，修正 API 路径
    const response = await fetch(`${BASE_URL}/ai/pdf/upload/${uploadChatId}`, {
      method: 'POST',
      body: formData
    })
    
    if (!response.ok) {
      throw new Error(`上传失败: ${response.status}`)
    }
    
    const data = await response.json()
    
    // 保存聊天 ID 和文件名
    currentChatId.value = data.chatId || uploadChatId
    currentPdfName.value = file.name
    pdfFile.value = file
    
    // 添加到聊天历史
    const newChat = {
      id: currentChatId.value,
      title: `PDF对话: ${file.name.slice(0, 20)}${file.name.length > 20 ? '...' : ''}`
    }
    
    // 更新聊天历史 - 避免重复添加
    if (!chatHistory.value.some(chat => chat.id === currentChatId.value)) {
      chatHistory.value = [newChat, ...chatHistory.value]
    }
    
    // 清空消息
    currentMessages.value = []
    
    // 添加系统消息
    currentMessages.value.push({
      role: 'assistant',
      content: `已上传 PDF 文件: ${file.name}。您可以开始提问了。`,
      timestamp: new Date(),
      isMarkdown: true
    })
    
  } catch (error) {
    console.error('上传失败:', error)
    alert('文件上传失败，请重试')
  } finally {
    isUploading.value = false
    uploadingFileName.value = ''
  }
}

// 处理拖拽悬停
const handleDragOver = (event) => {
  event.preventDefault()
  isDragging.value = true
}

// 处理拖拽离开
const handleDragLeave = (event) => {
  event.preventDefault()
  isDragging.value = false
}

const triggerFileInput = () => {
  const fileInput = document.querySelector('.file-input')
  fileInput.click()
}

// 添加上传文件名状态（如果还没有的话）
const uploadingFileName = ref('')

// 修改 sendMessage 方法
const sendMessage = async () => {
  if (!userInput.value.trim() || isStreaming.value) return
  
  // 添加用户消息到聊天记录
  const userMessage = {
    role: 'user',
    content: userInput.value,
    timestamp: new Date()
  }
  currentMessages.value.push(userMessage)
  
  // 清空输入框并调整高度
  const input = userInput.value
  userInput.value = ''
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
  }
  
  // 滚动到底部
  await scrollToBottom()
  
  // 添加一个空的助手消息作为流式响应的容器
  const assistantMessageIndex = currentMessages.value.length
  currentMessages.value.push({
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isMarkdown: true
  })
  
  try {
    isStreaming.value = true
    
    // 发送请求到服务器
    const reader = await chatAPI.sendPdfMessage(input, currentChatId.value)
    const decoder = new TextDecoder()
    let result = ''
    
    // 处理流式响应
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value, { stream: true })
      console.log('收到流式响应块:', chunk)
      result += chunk
      
      // 使用索引直接替换整个消息对象，强制触发响应式更新
      currentMessages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: result,
        timestamp: new Date(),
        isMarkdown: true
      }
      
      // 确保 DOM 更新并滚动到底部
      await nextTick()
      await scrollToBottom()
    }
    
  } catch (error) {
    console.error('发送消息失败:', error)
    currentMessages.value[assistantMessageIndex] = {
      role: 'assistant',
      content: '发送消息失败，请重试。',
      timestamp: new Date(),
      isMarkdown: true
    }
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 同样需要修改文件上传处理函数
const handleFileUpload = async (event) => {
  const files = event.target.files
  if (files.length === 0) return
  
  const file = files[0]
  
  // 检查是否为 PDF 文件
  if (file.type !== 'application/pdf') {
    alert('请上传 PDF 文件')
    return
  }
  
  // 设置上传状态和文件名
  isUploading.value = true
  uploadingFileName.value = file.name
  
  try {
    // 创建 FormData
    const formData = new FormData()
    formData.append('file', file)
    
    // 生成临时 chatId 或使用现有的
    const uploadChatId = currentChatId.value || `pdf_${Date.now()}`
    
    // 发送上传请求，修正 API 路径
    const response = await fetch(`${BASE_URL}/ai/pdf/upload/${uploadChatId}`, {
      method: 'POST',
      body: formData
    })
    
    if (!response.ok) {
      throw new Error(`上传失败: ${response.status}`)
    }
    
    const data = await response.json()
    
    // 保存聊天 ID 和文件名
    currentChatId.value = data.chatId || uploadChatId
    currentPdfName.value = file.name
    pdfFile.value = file
    
    // 添加到聊天历史
    const newChat = {
      id: currentChatId.value,
      title: `PDF对话: ${file.name.slice(0, 20)}${file.name.length > 20 ? '...' : ''}`
    }
    
    // 更新聊天历史 - 避免重复添加
    if (!chatHistory.value.some(chat => chat.id === currentChatId.value)) {
      chatHistory.value = [newChat, ...chatHistory.value]
    }
    
    // 清空消息
    currentMessages.value = []
    
    // 添加系统消息
    currentMessages.value.push({
      role: 'assistant',
      content: `已上传 PDF 文件: ${file.name}。您可以开始提问了。`,
      timestamp: new Date(),
      isMarkdown: true
    })
    
  } catch (error) {
    console.error('上传失败:', error)
    alert('文件上传失败，请重试')
  } finally {
    isUploading.value = false
    uploadingFileName.value = ''
    // 清空文件输入，允许重新选择同一文件
    event.target.value = ''
  }
}

// 监听清理事件
onMounted(() => {
  loadChatHistory()
  adjustTextareaHeight()
})

onUnmounted(() => {
  // 移除事件监听器
  window.removeEventListener('cleanupChatPDF', cleanupResources)
})
</script>

<style scoped lang="scss">
.chat-pdf {
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  background: var(--bg-color);
  overflow: hidden;

  .chat-container {
    flex: 1;
    display: flex;
    max-width: 1800px;
    width: 100%;
    margin: 0 auto;
    padding: 1.5rem 2rem;
    gap: 1.5rem;
    height: 100%;
    overflow: hidden;
  }

  .sidebar {
    width: 300px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 1rem;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    .sidebar-header {
      padding: 1.5rem;
      display: flex;
      align-items: center;
      gap: 0.75rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);

      .logo-link {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        text-decoration: none;
        color: inherit;
        transition: opacity 0.2s;
        
        &:hover {
          opacity: 0.8;
        }
      }

      .logo {
        width: 2rem;
        height: 2rem;
        color: #9333ea;
      }

      .title {
        font-size: 1.5rem;
        font-weight: bold;
        background: linear-gradient(120deg, #9333ea 0%, #c026d3 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }
    
    .history-list {
      flex: 1;
      overflow-y: auto;
      padding: 1rem 0;
      
      .history-header {
        padding: 0.5rem 1.5rem;
        display: flex;
        align-items: center;
        justify-content: space-between;
        
        span {
          font-size: 0.875rem;
          font-weight: 500;
          color: #666;
          text-transform: uppercase;
        }

        .new-chat-btn {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          padding: 0.5rem 0.75rem;
          border: none;
          border-radius: 0.5rem;
          background: #9333ea;
          color: white;
          font-size: 0.875rem;
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            background: #7e22ce;
          }

          .icon {
            width: 1rem;
            height: 1rem;
          }
        }
      }
      
      .history-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.75rem 1.5rem;
        cursor: pointer;
        transition: background-color 0.2s;
        
        &:hover {
          background: rgba(0, 0, 0, 0.05);
        }
        
        &.active {
          background: rgba(147, 51, 234, 0.1);
          
          .icon {
            color: #9333ea;
          }
          
          .title {
            color: #9333ea;
          }
        }
        
        .icon {
          width: 1.25rem;
          height: 1.25rem;
          color: #666;
        }
        
        .title {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: #333;
        }
      }
    }
  }

  .chat-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 1rem;
    overflow: hidden;

    .pdf-header {
      flex-shrink: 0;
      padding: 1rem 2rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      background: rgba(255, 255, 255, 0.98);
      display: flex;
      align-items: center;
      gap: 0.75rem;

      .icon {
        width: 1.5rem;
        height: 1.5rem;
        color: #666;
      }

      .filename {
        font-size: 1rem;
        color: #333;
      }
    }
    
    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 2rem;

      .empty-state {
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #999;
        gap: 1rem;

        .big-icon {
          width: 4rem;
          height: 4rem;
          color: #ccc;
        }
      }
    }
    
    .input-area {
      flex-shrink: 0;
      padding: 1.5rem 2rem;
      background: rgba(255, 255, 255, 0.98);
      border-top: 1px solid rgba(0, 0, 0, 0.05);
      display: flex;
      gap: 1rem;
      align-items: flex-end;
      
      textarea {
        flex: 1;
        resize: none;
        border: 1px solid rgba(0, 0, 0, 0.1);
        background: white;
        border-radius: 0.75rem;
        padding: 1rem;
        color: inherit;
        font-family: inherit;
        font-size: 1rem;
        line-height: 1.5;
        max-height: 150px;
        
        &:focus {
          outline: none;
          border-color: #333;
          box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
        }

        &:disabled {
          background: #f5f5f5;
          cursor: not-allowed;
        }
      }
      
      .send-button {
        background: #333;
        color: white;
        border: none;
        border-radius: 0.5rem;
        width: 2.5rem;
        height: 2.5rem;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: background-color 0.3s;
        
        &:hover:not(:disabled) {
          background: #000;
        }
        
        &:disabled {
          background: #ccc;
          cursor: not-allowed;
        }
        
        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }
  }
}

.dark {
  .sidebar {
    background: rgba(40, 40, 40, 0.95);

    .sidebar-header {
      border-bottom-color: rgba(255, 255, 255, 0.05);
    }

    .history-list {
      .history-header {
        span {
          color: #999;
        }

        .new-chat-btn {
          background: rgba(147, 51, 234, 0.8);

          &:hover {
            background: #9333ea;
          }
        }
      }

      .history-item {
        &:hover {
          background: rgba(255, 255, 255, 0.05);
        }

        &.active {
          background: rgba(147, 51, 234, 0.15);
        }

        .icon {
          color: #999;
        }

        .title {
          color: #fff;
        }
      }
    }
  }
  
  .chat-main {
    background: rgba(40, 40, 40, 0.95);
    
    .pdf-header {
      background: rgba(30, 30, 30, 0.98);
      border-bottom-color: rgba(255, 255, 255, 0.05);

      .icon {
        color: #999;
      }

      .filename {
        color: #fff;
      }
    }

    .messages {
      .empty-state {
        color: #666;

        .big-icon {
          color: #444;
        }
      }
    }

    .input-area {
      background: rgba(30, 30, 30, 0.98);
      border-top-color: rgba(255, 255, 255, 0.05);
      
      textarea {
        background: rgba(50, 50, 50, 0.95);
        border-color: rgba(255, 255, 255, 0.1);
        color: white;
        
        &:focus {
          border-color: #666;
          box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1);
        }

        &:disabled {
          background: rgba(30, 30, 30, 0.95);
        }
      }
    }
  }
}

.upload-welcome {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  gap: 3rem;

  .main-title {
    font-size: 3rem;
    font-weight: bold;
    text-align: center;

    .highlight {
      color: #9333ea;
      background: linear-gradient(120deg, #9333ea 0%, #c026d3 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }

  .drop-zone {
    width: 100%;
    max-width: 600px;
    min-height: 300px;
    border: 2px dashed #e5e7eb;
    border-radius: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.5);
    backdrop-filter: blur(10px);

    &.dragging {
      border-color: #9333ea;
      background: rgba(147, 51, 234, 0.05);
    }

    &.uploading {
      border-style: dashed;
      border-color: #007CF0;
      background: rgba(0, 124, 240, 0.05);
    }

    .upload-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
      padding: 2rem;

      .upload-status {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1.5rem;

        .spinner {
          width: 48px;
          height: 48px;
          border: 4px solid rgba(0, 124, 240, 0.1);
          border-left-color: #007CF0;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }

        .upload-progress {
          text-align: center;

          .status-text {
            font-size: 1.25rem;
            color: #007CF0;
            margin-bottom: 0.5rem;
          }

          .filename {
            font-size: 0.875rem;
            color: #666;
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }

      .upload-icon {
        width: 4rem;
        height: 4rem;
        color: #9333ea;
      }

      .upload-text {
        font-size: 1.25rem;
        color: #666;
      }

      .file-input {
        display: none;
      }

      .upload-button {
        background: #9333ea;
        color: white;
        border: none;
        padding: 0.75rem 2rem;
        border-radius: 0.5rem;
        font-size: 1rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: #7e22ce;
        }

        &.uploading {
          background: #9333ea80;
          cursor: not-allowed;
        }

        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }
  }
}

.dark {
  .upload-welcome {
    .drop-zone {
      border-color: #444;
      background: rgba(40, 40, 40, 0.5);

      &.dragging {
        border-color: #9333ea;
        background: rgba(147, 51, 234, 0.1);
      }

      &.uploading {
        border-color: #007CF0;
        background: rgba(0, 124, 240, 0.1);
      }

      .upload-content {
        .upload-status {
          .spinner {
            border-color: rgba(0, 124, 240, 0.2);
            border-left-color: #007CF0;
          }

          .upload-progress {
            .status-text {
              color: #007CF0;
            }

            .filename {
              color: #999;
            }
          }
        }
      }
    }
  }
}

.split-view {
  flex: 1;
  display: flex;
  overflow: hidden;

  .pdf-view {
    flex: 1;
    max-width: 50%;
    display: flex;
    flex-direction: column;
    border-right: 1px solid rgba(0, 0, 0, 0.1);
    background: #fff;

    .pdf-header {
      padding: 1rem 1.5rem;
      display: flex;
      align-items: center;
      gap: 0.75rem;
      background: #f8f9fa;
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);

      .icon {
        width: 1.5rem;
        height: 1.5rem;
        color: #666;
      }

      .filename {
        flex: 1;
        font-weight: 500;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .page-control {
        display: flex;
        align-items: center;
        gap: 0.5rem;

        .page-btn {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 2rem;
          height: 2rem;
          border: none;
          border-radius: 0.375rem;
          background: #fff;
          color: #666;
          cursor: pointer;
          transition: all 0.2s;

          &:hover:not(:disabled) {
            background: #f0f0f0;
            color: #333;
          }

          &:disabled {
            opacity: 0.5;
            cursor: not-allowed;
          }

          .icon {
            width: 1.25rem;
            height: 1.25rem;
          }
        }

        .page-info {
          font-size: 0.875rem;
          color: #666;
          min-width: 4rem;
          text-align: center;
        }
      }
    }

    .pdf-content {
      position: relative;  // 添加相对定位
      flex: 1;
      overflow-y: auto;
      background: #f1f1f1;
      padding: 1rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;

      .pdf-loading {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
        z-index: 10;  // 确保加载提示在最上层
        background: rgba(255, 255, 255, 0.9);  // 添加半透明背景
        padding: 2rem;
        border-radius: 1rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

        .loading-spinner {
          width: 48px;
          height: 48px;
          border: 4px solid rgba(0, 124, 240, 0.1);
          border-left-color: #007CF0;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }

        .loading-text {
          color: #666;
          font-size: 1rem;
          font-weight: 500;
        }
      }

      canvas {
        background: #fff;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        max-width: 100%;
        height: auto !important;
        flex-shrink: 0;
      }
    }
  }

  .chat-view {
    flex: 1;
    min-width: 400px;
    max-width: 50%;
    display: flex;
    flex-direction: column;
    background: #fff;

    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 1.5rem;
    }

    .input-area {
      flex-shrink: 0;
      padding: 1.5rem 2rem;
      background: rgba(255, 255, 255, 0.98);
      border-top: 1px solid rgba(0, 0, 0, 0.05);
      display: flex;
      gap: 1rem;
      align-items: flex-end;
      
      textarea {
        flex: 1;
        resize: none;
        border: 1px solid rgba(0, 0, 0, 0.1);
        background: white;
        border-radius: 0.75rem;
        padding: 1rem;
        color: inherit;
        font-family: inherit;
        font-size: 1rem;
        line-height: 1.5;
        max-height: 150px;
        
        &:focus {
          outline: none;
          border-color: #333;
          box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
        }

        &:disabled {
          background: #f5f5f5;
          cursor: not-allowed;
        }
      }
      
      .send-button {
        background: #333;
        color: white;
        border: none;
        border-radius: 0.5rem;
        width: 2.5rem;
        height: 2.5rem;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: background-color 0.3s;
        
        &:hover:not(:disabled) {
          background: #000;
        }
        
        &:disabled {
          background: #ccc;
          cursor: not-allowed;
        }
        
        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }
  }
}

.dark {
  .split-view {
    .pdf-view {
      border-right-color: rgba(255, 255, 255, 0.1);
      background: #1a1a1a;

      .pdf-header {
        background: rgba(30, 30, 30, 0.98);
        border-bottom-color: rgba(255, 255, 255, 0.1);

        .icon {
          color: #999;
        }

        .filename {
          color: #fff;
        }

        .page-control {
          .page-btn {
            background: rgba(255, 255, 255, 0.1);
            color: #999;

            &:hover:not(:disabled) {
              background: rgba(255, 255, 255, 0.2);
              color: #fff;
            }
          }

          .page-info {
            color: #999;
          }
        }
      }

      .pdf-content {
        background: #0d0d0d;

        .pdf-loading {
          .loading-spinner {
            border-color: rgba(0, 124, 240, 0.2);
            border-left-color: #007CF0;
          }

          .loading-text {
            color: #999;
          }
        }

        canvas {
          background: #1a1a1a;
        }
      }
    }

    .chat-view {
      background: #1a1a1a;
    }
  }
}

@media (max-width: 1024px) {
  .split-view {
    flex-direction: column;

    .pdf-view {
      height: 50vh;
      border-right: none;
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    }

    .chat-view {
      width: 100%;
      min-width: 0;
      height: 50vh;
    }
  }

  .dark .split-view .pdf-view {
    border-bottom-color: rgba(255, 255, 255, 0.1);
  }
}

@media (max-width: 768px) {
  .chat-pdf {
    .chat-container {
      padding: 0;
    }
    
    .sidebar {
      display: none;
    }
    
    .chat-main {
      border-radius: 0;
    }
  }
}

// 添加动画
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 
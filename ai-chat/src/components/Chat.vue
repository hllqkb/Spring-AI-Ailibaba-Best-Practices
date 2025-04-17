<script setup lang="ts">
import {ref, reactive, onMounted, onBeforeUnmount, nextTick} from 'vue'
import {fetchEventSource} from '@microsoft/fetch-event-source'

// 时间格式化函数
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp);
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
};

// 生成随机用户ID（示例：8位字母数字组合）
const generateUserId = () => {
  return Math.random().toString(36).substr(2, 8);
};

// 持久化存储用户ID
const userId = ref('');

enum MessageStatus {
  Streaming = 'streaming',
  Complete = 'complete',
  Error = 'error',
}

interface Message {
  id: string
  content: string
  isBot: boolean
  status?: MessageStatus
  timestamp: number
  retry?: () => Promise<void>
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const controller = ref<AbortController>()
const messageContainer = ref<HTMLElement>()
const inputRef = ref<HTMLInputElement>()

// 自动滚动控制
let autoScroll = true
let lastCharType: 'chinese' | 'english' | 'other' = 'other'

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value && autoScroll) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

const handleScroll = () => {
  if (!messageContainer.value) return
  const {scrollTop, scrollHeight, clientHeight} = messageContainer.value
  autoScroll = scrollHeight - (scrollTop + clientHeight) < 50
}

// 字符类型检测
const getCharType = (char: string): 'chinese' | 'english' | 'other' => {
  if (/[\u4e00-\u9fa5\u3000-\u303F\uFF00-\uFFEF]/.test(char)) {
    return 'chinese'
  }
  if (/[a-zA-Z]/.test(char)) {
    return 'english'
  }
  return 'other'
}

// 智能空格处理核心逻辑
const processContent = (prev: string, newData: string): string => {
  if (prev.length === 0) return newData

  const lastChar = prev.slice(-1)
  const newFirstChar = newData[0] || ''

  const prevType = getCharType(lastChar)
  const newType = getCharType(newFirstChar)

  let processed = newData

  // 需要添加空格的情况
  const shouldAddSpace =
      (prevType === 'english' && newType === 'english') || // 英文接英文
      (prevType === 'chinese' && newType === 'english') || // 中文接英文
      (prevType === 'english' && newType === 'chinese' && !/[!?,.]$/.test(lastChar)) // 英文接中文（非标点结尾）

  // 需要删除空格的情况
  const shouldRemoveSpace =
      (prevType === 'chinese' && newType === 'chinese') || // 中文接中文
      (prevType === 'other' && /^[\u4e00-\u9fa5]/.test(newData)) // 特殊符号接中文

  if (shouldAddSpace && !lastChar.match(/\s/) && !newFirstChar.match(/\s/)) {
    processed = ' ' + processed
  } else if (shouldRemoveSpace) {
    processed = processed.replace(/^\s+/, '')
  }

  return processed
}

const sendChatRequest = async (content: string, botMessage: Message) => {
  controller.value = new AbortController()

  await fetchEventSource('http://localhost:8088/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream',
      'X-Content-Lang': 'zh-CN'
    },
    body: JSON.stringify({message: content,userId:userId.value}),
    signal: controller.value?.signal,
    openWhenHidden: true,

    onopen: async response => {
      if (!response.ok) throw new Error(`HTTP error ${response.status}`)
    },

    onmessage: event => {
      if (event.data === '[DONE]') {
        botMessage.status = MessageStatus.Complete
        return
      }

      const processedData = processContent(botMessage.content, event.data)
      botMessage.content += processedData
      botMessage.timestamp = Date.now()

      // 更新最后字符类型
      const lastChar = processedData.slice(-1)
      lastCharType = getCharType(lastChar)

      scrollToBottom()
    },

    onerror: err => {
      throw err
    }
  })
}

// 错误处理
const handleRequestError = (botMessage: Message, error: unknown) => {
  const errorMessage = error instanceof Error
      ? navigator.onLine
          ? error.message
          : '网络连接不可用'
      : '请求发生未知错误'

  botMessage.status = MessageStatus.Error
  botMessage.content = errorMessage
  botMessage.retry = createRetryHandler(botMessage.content)
}

// 主发送逻辑
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userContent = inputMessage.value.trim()
  inputMessage.value = ''

  // 创建用户消息
  const userMessage = reactive<Message>({
    id: `user-${Date.now()}`,
    content: userContent,
    isBot: false,
    timestamp: Date.now()
  })
  messages.value.push(userMessage)

  // 创建机器人消息
  const botMessage = reactive<Message>({
    id: `bot-${Date.now()}`,
    content: '',
    isBot: true,
    status: MessageStatus.Streaming,
    timestamp: Date.now()
  })
  messages.value.push(botMessage)

  isLoading.value = true

  try {
    await sendChatRequest(userContent, botMessage)
  } catch (err) {
    handleRequestError(botMessage, err)
  } finally {
    isLoading.value = false
    nextTick(() => inputRef.value?.focus())
  }
}

// 停止生成
const stopGeneration = () => {
  controller.value?.abort()
  isLoading.value = false
}

// 生命周期
onMounted(() => {
  userId.value = localStorage.getItem('chatUserId') || generateUserId();
  localStorage.setItem('chatUserId', userId.value);
  messageContainer.value?.addEventListener('scroll', handleScroll)
  inputRef.value?.focus()
})

onBeforeUnmount(() => {
  messageContainer.value?.removeEventListener('scroll', handleScroll)
  controller.value?.abort()
})
</script>

<template>
  <div class="chat-container">
    <div class="header">
      <div class="header-title">AI 助手</div>
    </div>
    
    <div class="messages" ref="messageContainer">
      <div class="welcome-message" v-if="messages.length === 0">
        <div class="welcome-icon">👋</div>
        <div class="welcome-text">你好！我是AI助手，有什么可以帮助你的吗？</div>
      </div>
      
      <div v-for="message in messages" :key="message.id" class="message" :class="{ 'bot': message.isBot, 'user': !message.isBot }">
        <div class="avatar" v-if="message.isBot">🤖</div>
        <div class="avatar user-avatar" v-else>👤</div>
        
        <div class="message-bubble">
          <div class="content">{{ message.content }}</div>
          <div v-if="message.status === MessageStatus.Streaming" class="typing-indicator">
            <span></span><span></span><span></span>
          </div>
          <div class="timestamp" v-if="message.status !== MessageStatus.Streaming">
            {{ formatTime(message.timestamp) }}
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-area">
      <input
        v-model="inputMessage"
        @keyup.enter="sendMessage"
        ref="inputRef"
        placeholder="输入问题..."
        :disabled="isLoading"
      />
      <button @click="sendMessage" :disabled="isLoading || !inputMessage.trim()">
        <span v-if="!isLoading">发送</span>
        <span v-else class="sending-icon">↗</span>
      </button>
      <button v-if="isLoading" @click="stopGeneration" class="stop-button">
        停止
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 100%;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  background: #f0f2f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  color: #333;
}

.header {
  background: linear-gradient(135deg, #4a90e2, #6a5acd);
  color: white;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.welcome-message {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.welcome-text {
  font-size: 1.1rem;
}

.message {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  max-width: 85%;
}

.message.user {
  margin-left: auto;
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.user-avatar {
  background: #4a90e2;
  color: white;
}

.message-bubble {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  position: relative;
}

.message.user .message-bubble {
  background: #4a90e2;
  color: white;
}

.content {
  white-space: pre-wrap;
  word-break: break-word;
}

.timestamp {
  font-size: 0.8rem;
  color: #999;
  margin-top: 4px;
  text-align: right;
}

.message.user .timestamp {
  color: rgba(255, 255, 255, 0.8);
}

.typing-indicator {
  display: flex;
  gap: 4px;
  margin-top: 8px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #999;
  border-radius: 50%;
  animation: typing 1s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}

.input-area {
  padding: 16px;
  background: white;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 8px;
}

input {
  flex: 1;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s;
}

input:focus {
  border-color: #4a90e2;
}

button {
  padding: 12px 24px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover:not(:disabled) {
  background: #357abd;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.stop-button {
  background: #dc3545;
}

.stop-button:hover:not(:disabled) {
  background: #c82333;
}

.sending-icon {
  display: inline-block;
  animation: sending 1s infinite;
}

@keyframes sending {
  0% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(2px, -2px);
  }
  100% {
    transform: translate(0, 0);
  }
}
</style>
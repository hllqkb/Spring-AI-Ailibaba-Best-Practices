<template>
  <div class="customer-service" :class="{ 'dark': isDark }">
    <div class="chat-container">
      <div class="sidebar">
        <div class="history-header">
          <h2>咨询记录</h2>
          <button class="new-chat" @click="startNewChat">
            <PlusIcon class="icon" />
            新咨询
          </button>
        </div>
        <div class="history-list">
          <div 
            v-for="chat in chatHistory" 
            :key="chat.id"
            class="history-item"
            :class="{ 'active': currentChatId === chat.id }"
            @click="loadChat(chat.id)"
          >
            <ChatBubbleLeftRightIcon class="icon" />
            <span class="title">{{ chat.title || '新咨询' }}</span>
          </div>
        </div>
      </div>
      
      <div class="chat-main">
        <div class="service-header">
          <div class="service-info">
            <ComputerDesktopIcon class="avatar" />
            <div class="info">
              <h3>小黑</h3>
              <p>黑马程序员智能客服</p>
            </div>
          </div>
        </div>

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

    <!-- 预约成功弹窗 -->
    <div v-if="showBookingModal" class="booking-modal">
      <div class="modal-content">
        <h3>预约成功！</h3>
        <div class="booking-info" v-html="bookingInfo"></div>
        <button @click="showBookingModal = false">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { 
  ChatBubbleLeftRightIcon, 
  PaperAirplaneIcon,
  PlusIcon,
  ComputerDesktopIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'

const isDark = useDark()
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const currentChatId = ref(null)
const currentMessages = ref([])
const chatHistory = ref([])
const showBookingModal = ref(false)
const bookingInfo = ref('')

// 配置 marked
marked.setOptions({
  breaks: true,  // 支持换行
  gfm: true,     // 支持 GitHub Flavored Markdown
  sanitize: false // 允许 HTML
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

// 发送消息
const sendMessage = async (content) => {
  if (isStreaming.value || (!content && !userInput.value.trim())) return
  
  // 使用传入的 content 或用户输入框的内容
  const messageContent = content || userInput.value.trim()
  
  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: messageContent,
    timestamp: new Date()
  }
  currentMessages.value.push(userMessage)
  
  // 清空输入
  if (!content) {  // 只有在非传入内容时才清空输入框
    userInput.value = ''
    adjustTextareaHeight()
  }
  await scrollToBottom()
  
  // 添加助手消息占位
  const assistantMessage = {
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isMarkdown: true  // 添加标记表示这是 Markdown 内容
  }
  currentMessages.value.push(assistantMessage)
  isStreaming.value = true
  
  let accumulatedContent = ''
  
  try {
    const reader = await chatAPI.sendServiceMessage(messageContent, currentChatId.value)
    const decoder = new TextDecoder('utf-8')
    
    while (true) {
      try {
        const { value, done } = await reader.read()
        if (done) break
        
        // 累积新内容
        accumulatedContent += decoder.decode(value)
        
        await nextTick(() => {
          // 更新消息
          const updatedMessage = {
            ...assistantMessage,
            content: accumulatedContent,
            isMarkdown: true  // 保持 Markdown 标记
          }
          const lastIndex = currentMessages.value.length - 1
          currentMessages.value.splice(lastIndex, 1, updatedMessage)
        })
        await scrollToBottom()
      } catch (readError) {
        console.error('读取流错误:', readError)
        break
      }
    }

    // 检查是否包含预约信息
    if (accumulatedContent.includes('预约编号')) {
      const bookingMatch = accumulatedContent.match(/【(.*?)】/s)
      if (bookingMatch) {
        // 使用 marked 处理预约信息中的 Markdown
        bookingInfo.value = DOMPurify.sanitize(
          marked.parse(bookingMatch[1]),
          {
            ADD_TAGS: ['code', 'pre', 'span'],
            ADD_ATTR: ['class', 'language']
          }
        )
        showBookingModal.value = true
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 加载特定对话
const loadChat = async (chatId) => {
  currentChatId.value = chatId
  try {
    const messages = await chatAPI.getChatMessages(chatId, 'service')
    currentMessages.value = messages.map(msg => ({
      ...msg,
      isMarkdown: msg.role === 'assistant'  // 为助手消息添加 Markdown 标记
    }))
  } catch (error) {
    console.error('加载对话消息失败:', error)
    currentMessages.value = []
  }
}

// 加载聊天历史
const loadChatHistory = async () => {
  try {
    const history = await chatAPI.getChatHistory('service')
    chatHistory.value = history || []
    if (history && history.length > 0) {
      await loadChat(history[0].id)
    } else {
      await startNewChat()  // 等待 startNewChat 完成
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    chatHistory.value = []
    await startNewChat()  // 等待 startNewChat 完成
  }
}

// 开始新对话
const startNewChat = async () => {  // 添加 async
  const newChatId = Date.now().toString()
  currentChatId.value = newChatId
  currentMessages.value = []
  
  // 添加新对话到历史列表
  const newChat = {
    id: newChatId,
    title: `咨询 ${newChatId.slice(-6)}`
  }
  chatHistory.value = [newChat, ...chatHistory.value]

  // 发送初始问候语
  await sendMessage('你好')
}

onMounted(() => {
  loadChatHistory()
  adjustTextareaHeight()
})
</script>

<style scoped lang="scss">
.customer-service {
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
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 1rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    
    .history-header {
      flex-shrink: 0;
      padding: 1rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h2 {
        font-size: 1.25rem;
      }
      
      .new-chat {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        border-radius: 0.5rem;
        background: #333;
        color: white;
        border: none;
        cursor: pointer;
        transition: background-color 0.3s;
        
        &:hover {
          background: #000;
        }
        
        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }
    
    .history-list {
      flex: 1;
      overflow-y: auto;
      padding: 0 1rem 1rem;
      
      .history-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem;
        border-radius: 0.5rem;
        cursor: pointer;
        transition: background-color 0.3s;
        
        &:hover {
          background: rgba(0, 0, 0, 0.05);
        }
        
        &.active {
          background: rgba(0, 0, 0, 0.1);
        }
        
        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
        
        .title {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
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
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    overflow: hidden;

    .service-header {
      flex-shrink: 0;
      padding: 1rem 2rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      background: rgba(255, 255, 255, 0.98);

      .service-info {
        display: flex;
        align-items: center;
        gap: 1rem;

        .avatar {
          width: 48px;
          height: 48px;
          color: #333;
          padding: 6px;
          background: #f0f0f0;
          border-radius: 12px;
          transition: all 0.3s ease;
          
          &:hover {
            background: #e0e0e0;
            transform: scale(1.05);
          }
        }

        .info {
          h3 {
            font-size: 1.25rem;
            margin-bottom: 0.25rem;
          }

          p {
            font-size: 0.875rem;
            color: #666;
          }
        }
      }
    }
    
    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 2rem;
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

  .booking-modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;

    .modal-content {
      background: white;
      padding: 2rem;
      border-radius: 1rem;
      max-width: 500px;
      width: 90%;
      text-align: center;

      h3 {
        font-size: 1.5rem;
        margin-bottom: 1rem;
        color: #333;
      }

      .booking-info {
        margin: 1.5rem 0;
        text-align: left;
        line-height: 1.6;
        color: #666;
      }

      button {
        padding: 0.75rem 2rem;
        background: #333;
        color: white;
        border: none;
        border-radius: 0.5rem;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background: #000;
        }
      }
    }
  }
}

.dark {
  .sidebar {
    background: rgba(40, 40, 40, 0.95);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  }
  
  .chat-main {
    background: rgba(40, 40, 40, 0.95);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
    
    .service-header {
      background: rgba(30, 30, 30, 0.98);
      border-bottom: 1px solid rgba(255, 255, 255, 0.05);

      .service-info {
        .avatar {
          color: #fff;
          background: #444;
          
          &:hover {
            background: #555;
          }
        }

        .info p {
          color: #999;
        }
      }
    }

    .input-area {
      background: rgba(30, 30, 30, 0.98);
      border-top: 1px solid rgba(255, 255, 255, 0.05);
      
      textarea {
        background: rgba(50, 50, 50, 0.95);
        border-color: rgba(255, 255, 255, 0.1);
        color: white;
        
        &:focus {
          border-color: #666;
          box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1);
        }
      }
    }
  }

  .booking-modal .modal-content {
    background: #333;

    h3 {
      color: #fff;
    }

    .booking-info {
      color: #ccc;
    }

    button {
      background: #666;

      &:hover {
        background: #888;
      }
    }
  }
}

@media (max-width: 768px) {
  .customer-service {
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
</style> 
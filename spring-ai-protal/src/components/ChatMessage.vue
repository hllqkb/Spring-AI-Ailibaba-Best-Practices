<template>
  <div class="message" :class="{ 'message-user': isUser }">
    <div class="avatar">
      <UserCircleIcon v-if="isUser" class="icon" />
      <ComputerDesktopIcon v-else class="icon" :class="{ 'assistant': !isUser }" />
    </div>
    <div class="content">
      <div class="text-container">
        <button v-if="isUser" class="user-copy-button" @click="copyContent" :title="copyButtonTitle">
          <DocumentDuplicateIcon v-if="!copied" class="copy-icon" />
          <CheckIcon v-else class="copy-icon copied" />
        </button>
        <div class="text" ref="contentRef" v-if="isUser">
          {{ message.content }}
        </div>
        <div class="text markdown-content" ref="contentRef" v-else v-html="processedContent"></div>
      </div>
      <div class="message-footer" v-if="!isUser">
        <button class="copy-button" @click="copyContent" :title="copyButtonTitle">
          <DocumentDuplicateIcon v-if="!copied" class="copy-icon" />
          <CheckIcon v-else class="copy-icon copied" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, nextTick, ref, watch } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { UserCircleIcon, ComputerDesktopIcon, DocumentDuplicateIcon, CheckIcon } from '@heroicons/vue/24/outline'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const contentRef = ref(null)
const copied = ref(false)
const copyButtonTitle = computed(() => copied.value ? '已复制' : '复制内容')

// 配置 marked
marked.setOptions({
  breaks: true,
  gfm: true,
  sanitize: false
})

// 处理内容
const processContent = (content) => {
  if (!content) return ''

  // 分析内容中的 think 标签
  let result = ''
  let isInThinkBlock = false
  let currentBlock = ''

  // 逐字符分析，处理 think 标签
  for (let i = 0; i < content.length; i++) {
    if (content.slice(i, i + 7) === '<think>') {
      isInThinkBlock = true
      if (currentBlock) {
        // 将之前的普通内容转换为 HTML
        result += marked.parse(currentBlock)
      }
      currentBlock = ''
      i += 6 // 跳过 <think>
      continue
    }

    if (content.slice(i, i + 8) === '</think>') {
      isInThinkBlock = false
      // 将 think 块包装在特殊 div 中
      result += `<div class="think-block">${marked.parse(currentBlock)}</div>`
      currentBlock = ''
      i += 7 // 跳过 </think>
      continue
    }

    currentBlock += content[i]
  }

  // 处理剩余内容
  if (currentBlock) {
    if (isInThinkBlock) {
      result += `<div class="think-block">${marked.parse(currentBlock)}</div>`
    } else {
      result += marked.parse(currentBlock)
    }
  }

  // 净化处理后的 HTML
  const cleanHtml = DOMPurify.sanitize(result, {
    ADD_TAGS: ['think', 'code', 'pre', 'span'],
    ADD_ATTR: ['class', 'language']
  })
  
  // 在净化后的 HTML 中查找代码块并添加复制按钮
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = cleanHtml
  
  // 查找所有代码块
  const preElements = tempDiv.querySelectorAll('pre')
  preElements.forEach(pre => {
    const code = pre.querySelector('code')
    if (code) {
      // 创建包装器
      const wrapper = document.createElement('div')
      wrapper.className = 'code-block-wrapper'
      
      // 添加复制按钮
      const copyBtn = document.createElement('button')
      copyBtn.className = 'code-copy-button'
      copyBtn.title = '复制代码'
      copyBtn.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" class="code-copy-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
        </svg>
      `
      
      // 添加成功消息
      const successMsg = document.createElement('div')
      successMsg.className = 'copy-success-message'
      successMsg.textContent = '已复制!'
      
      // 组装结构
      wrapper.appendChild(copyBtn)
      wrapper.appendChild(pre.cloneNode(true))
      wrapper.appendChild(successMsg)
      
      // 替换原始的 pre 元素
      pre.parentNode.replaceChild(wrapper, pre)
    }
  })
  
  return tempDiv.innerHTML
}

// 修改计算属性
const processedContent = computed(() => {
  if (!props.message.content) return ''
  return processContent(props.message.content)
})

// 为代码块添加复制功能
const setupCodeBlockCopyButtons = () => {
  if (!contentRef.value) return;
  
  const codeBlocks = contentRef.value.querySelectorAll('.code-block-wrapper');
  codeBlocks.forEach(block => {
    const copyButton = block.querySelector('.code-copy-button');
    const codeElement = block.querySelector('code');
    const successMessage = block.querySelector('.copy-success-message');
    
    if (copyButton && codeElement) {
      // 移除旧的事件监听器
      const newCopyButton = copyButton.cloneNode(true);
      copyButton.parentNode.replaceChild(newCopyButton, copyButton);
      
      // 添加新的事件监听器
      newCopyButton.addEventListener('click', async (e) => {
        e.preventDefault();
        e.stopPropagation();
        try {
          const code = codeElement.textContent || '';
          await navigator.clipboard.writeText(code);
          
          // 显示成功消息
          if (successMessage) {
            successMessage.classList.add('visible');
            setTimeout(() => {
              successMessage.classList.remove('visible');
            }, 2000);
          }
        } catch (err) {
          console.error('复制代码失败:', err);
        }
      });
    }
  });
}

// 在内容更新后手动应用高亮和设置复制按钮
const highlightCode = async () => {
  await nextTick()
  if (contentRef.value) {
    contentRef.value.querySelectorAll('pre code').forEach((block) => {
      hljs.highlightElement(block)
    })
    
    // 设置代码块复制按钮
    setupCodeBlockCopyButtons()
  }
}

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const isUser = computed(() => props.message.role === 'user')

// 复制内容到剪贴板
const copyContent = async () => {
  try {
    // 获取纯文本内容
    let textToCopy = props.message.content;
    
    // 如果是AI回复，需要去除HTML标签
    if (!isUser.value && contentRef.value) {
      // 创建临时元素来获取纯文本
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = processedContent.value;
      textToCopy = tempDiv.textContent || tempDiv.innerText || '';
    }
    
    await navigator.clipboard.writeText(textToCopy);
    copied.value = true;
    
    // 3秒后重置复制状态
    setTimeout(() => {
      copied.value = false;
    }, 3000);
  } catch (err) {
    console.error('复制失败:', err);
  }
}

// 监听内容变化
watch(() => props.message.content, () => {
  if (!isUser.value) {
    highlightCode()
  }
})

// 初始化时也执行一次
onMounted(() => {
  if (!isUser.value) {
    highlightCode()
  }
})

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleTimeString()
}
</script>

<style scoped lang="scss">
.message {
  display: flex;
  margin-bottom: 1.5rem;
  gap: 1rem;

  &.message-user {
    flex-direction: row-reverse;

    .content {
      align-items: flex-end;
      
      .text-container {
        position: relative;
        
        .text {
          background: #f0f7ff; // 浅色背景
          color: #333;
          border-radius: 1rem 1rem 0 1rem;
        }
        
        .user-copy-button {
          position: absolute;
          left: -30px;
          top: 50%;
          transform: translateY(-50%);
          background: transparent;
          border: none;
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          opacity: 0;
          transition: opacity 0.2s;
          
          .copy-icon {
            width: 16px;
            height: 16px;
            color: #666;
            
            &.copied {
              color: #4ade80;
            }
          }
        }
        
        &:hover .user-copy-button {
          opacity: 1;
        }
      }
      
      .message-footer {
        flex-direction: row-reverse;
      }
    }
  }

  .avatar {
    width: 40px;
    height: 40px;
    flex-shrink: 0;

    .icon {
      width: 100%;
      height: 100%;
      color: #666;
      padding: 4px;
      border-radius: 8px;
      transition: all 0.3s ease;

      &.assistant {
        color: #333;
        background: #f0f0f0;

        &:hover {
          background: #e0e0e0;
          transform: scale(1.05);
        }
      }
    }
  }

  .content {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
    max-width: 80%;
    
    .text-container {
      position: relative;
    }
    
    .message-footer {
      display: flex;
      align-items: center;
      margin-top: 0.25rem;
      
      .time {
        font-size: 0.75rem;
        color: #666;
      }
      
      .copy-button {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        background: transparent;
        border: none;
        font-size: 0.75rem;
        color: #666;
        padding: 0.25rem 0.5rem;
        border-radius: 4px;
        cursor: pointer;
        margin-right: auto;
        transition: background-color 0.2s;
        
        &:hover {
          background-color: rgba(0, 0, 0, 0.05);
        }
        
        .copy-icon {
          width: 14px;
          height: 14px;
          
          &.copied {
            color: #4ade80;
          }
        }
        
        .copy-text {
          font-size: 0.75rem;
        }
      }
    }

    .text {
      padding: 1rem;
      border-radius: 1rem 1rem 1rem 0;
      line-height: 1.5;
      white-space: pre-wrap;
      color: var(--text-color);

      .cursor {
        animation: blink 1s infinite;
      }

      :deep(.think-block) {
        position: relative;
        padding: 0.75rem 1rem 0.75rem 1.5rem;
        margin: 0.5rem 0;
        color: #666;
        font-style: italic;
        border-left: 4px solid #ddd;
        background-color: rgba(0, 0, 0, 0.03);
        border-radius: 0 0.5rem 0.5rem 0;

        // 添加平滑过渡效果
        opacity: 1;
        transform: translateX(0);
        transition: opacity 0.3s ease, transform 0.3s ease;

        &::before {
          content: '思考';
          position: absolute;
          top: -0.75rem;
          left: 1rem;
          padding: 0 0.5rem;
          font-size: 0.75rem;
          background: #f5f5f5;
          border-radius: 0.25rem;
          color: #999;
          font-style: normal;
        }

        // 添加进入动画
        &:not(:first-child) {
          animation: slideIn 0.3s ease forwards;
        }
      }

      :deep(pre) {
        background: #f6f8fa;
        padding: 1rem;
        border-radius: 0.5rem;
        overflow-x: auto;
        margin: 0.5rem 0;
        border: 1px solid #e1e4e8;

        code {
          background: transparent;
          padding: 0;
          font-family: ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, Liberation Mono, monospace;
          font-size: 0.9rem;
          line-height: 1.5;
          tab-size: 2;
        }
      }

      :deep(.hljs) {
        color: #24292e;
        background: transparent;
      }

      :deep(.hljs-keyword) {
        color: #d73a49;
      }

      :deep(.hljs-built_in) {
        color: #005cc5;
      }

      :deep(.hljs-type) {
        color: #6f42c1;
      }

      :deep(.hljs-literal) {
        color: #005cc5;
      }

      :deep(.hljs-number) {
        color: #005cc5;
      }

      :deep(.hljs-regexp) {
        color: #032f62;
      }

      :deep(.hljs-string) {
        color: #032f62;
      }

      :deep(.hljs-subst) {
        color: #24292e;
      }

      :deep(.hljs-symbol) {
        color: #e36209;
      }

      :deep(.hljs-class) {
        color: #6f42c1;
      }

      :deep(.hljs-function) {
        color: #6f42c1;
      }

      :deep(.hljs-title) {
        color: #6f42c1;
      }

      :deep(.hljs-params) {
        color: #24292e;
      }

      :deep(.hljs-comment) {
        color: #6a737d;
      }

      :deep(.hljs-doctag) {
        color: #d73a49;
      }

      :deep(.hljs-meta) {
        color: #6a737d;
      }

      :deep(.hljs-section) {
        color: #005cc5;
      }

      :deep(.hljs-name) {
        color: #22863a;
      }

      :deep(.hljs-attribute) {
        color: #005cc5;
      }

      :deep(.hljs-variable) {
        color: #e36209;
      }
    }
  }
}

@keyframes blink {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.dark {
  .message {
    .avatar .icon {
      &.assistant {
        color: #fff;
        background: #444;

        &:hover {
          background: #555;
        }
      }
    }

    &.message-user {
      .content .text-container {
        .text {
          background: #1a365d; // 暗色模式下的浅蓝色背景
          color: #fff;
        }
        
        .user-copy-button {
          .copy-icon {
            color: #999;
            
            &.copied {
              color: #4ade80;
            }
          }
        }
      }
    }

    .content {
      .message-footer {
        .time {
          color: #999;
        }
        
        .copy-button {
          color: #999;
          
          &:hover {
            background-color: rgba(255, 255, 255, 0.1);
          }
        }
      }

      .text {
        :deep(.think-block) {
          background-color: rgba(255, 255, 255, 0.03);
          border-left-color: #666;
          color: #999;

          &::before {
            background: #2a2a2a;
            color: #888;
          }
        }

        :deep(pre) {
          background: #161b22;
          border-color: #30363d;

          code {
            color: #c9d1d9;
          }
        }

        :deep(.hljs) {
          color: #c9d1d9;
          background: transparent;
        }

        :deep(.hljs-keyword) {
          color: #ff7b72;
        }

        :deep(.hljs-built_in) {
          color: #79c0ff;
        }

        :deep(.hljs-type) {
          color: #ff7b72;
        }

        :deep(.hljs-literal) {
          color: #79c0ff;
        }

        :deep(.hljs-number) {
          color: #79c0ff;
        }

        :deep(.hljs-regexp) {
          color: #a5d6ff;
        }

        :deep(.hljs-string) {
          color: #a5d6ff;
        }

        :deep(.hljs-subst) {
          color: #c9d1d9;
        }

        :deep(.hljs-symbol) {
          color: #ffa657;
        }

        :deep(.hljs-class) {
          color: #f2cc60;
        }

        :deep(.hljs-function) {
          color: #d2a8ff;
        }

        :deep(.hljs-title) {
          color: #d2a8ff;
        }

        :deep(.hljs-params) {
          color: #c9d1d9;
        }

        :deep(.hljs-comment) {
          color: #8b949e;
        }

        :deep(.hljs-doctag) {
          color: #ff7b72;
        }

        :deep(.hljs-meta) {
          color: #8b949e;
        }

        :deep(.hljs-section) {
          color: #79c0ff;
        }

        :deep(.hljs-name) {
          color: #7ee787;
        }

        :deep(.hljs-attribute) {
          color: #79c0ff;
        }

        :deep(.hljs-variable) {
          color: #ffa657;
        }
      }

      &.message-user .content .text {
        background: #0066cc;
        color: white;
      }
    }
  }
}

.markdown-content {
  :deep(p) {
    margin: 0.5rem 0;

    &:first-child {
      margin-top: 0;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  :deep(ul),
  :deep(ol) {
    margin: 0.5rem 0;

    padding-left: 1.5rem;
  }

  :deep(li) {
    margin: 0.25rem 0;

  }

  :deep(code) {
    background: rgba(0, 0, 0, 0.05);
    padding: 0.2em 0.4em;
    border-radius: 3px;
    font-size: 0.9em;
    font-family: ui-monospace, monospace;
  }

  :deep(pre code) {
    background: transparent;
    padding: 0;
  }

  :deep(table) {
    border-collapse: collapse;
    margin: 0.5rem 0;
    width: 100%;
  }

  :deep(th),
  :deep(td) {
    border: 1px solid #ddd;
    padding: 0.5rem;
    text-align: left;
  }

  :deep(th) {
    background: rgba(0, 0, 0, 0.05);
  }

  :deep(blockquote) {
    margin: 0.5rem 0;
    padding-left: 1rem;
    border-left: 4px solid #ddd;
    color: #666;
  }

  :deep(.code-block-wrapper) {
    position: relative;
    margin: 1rem 0;
    border-radius: 6px;
    overflow: hidden;
    
    .code-copy-button {
      position: absolute;
      top: 0.5rem;
      right: 0.5rem;
      background: rgba(255, 255, 255, 0.1);
      border: none;
      color: #e6e6e6;
      cursor: pointer;
      padding: 0.25rem;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.2s, background-color 0.2s;
      z-index: 10;
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
      }
      
      .code-copy-icon {
        width: 16px;
        height: 16px;
      }
    }
    
    &:hover .code-copy-button {
      opacity: 0.8;
    }
    
    pre {
      margin: 0;
      padding: 1rem;
      background: #1e1e1e;
      overflow-x: auto;
      
      code {
        background: transparent;
        padding: 0;
        font-family: ui-monospace, monospace;
      }
    }
    
    .copy-success-message {
      position: absolute;
      top: 0.5rem;
      right: 0.5rem;
      background: rgba(74, 222, 128, 0.9);
      color: white;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      opacity: 0;
      transform: translateY(-10px);
      transition: opacity 0.3s, transform 0.3s;
      pointer-events: none;
      z-index: 20;
      
      &.visible {
        opacity: 1;
        transform: translateY(0);
      }
    }
  }
}

.dark {
  .markdown-content {
    :deep(.code-block-wrapper) {
      .code-copy-button {
        background: rgba(255, 255, 255, 0.05);
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.1);
        }
      }
      
      pre {
        background: #0d0d0d;
      }
    }
    
    :deep(code) {
      background: rgba(255, 255, 255, 0.1);
    }

    :deep(th),
    :deep(td) {
      border-color: #444;
    }

    :deep(th) {
      background: rgba(255, 255, 255, 0.1);
    }

    :deep(blockquote) {
      border-left-color: #444;
      color: #999;
    }
  }
}
</style>
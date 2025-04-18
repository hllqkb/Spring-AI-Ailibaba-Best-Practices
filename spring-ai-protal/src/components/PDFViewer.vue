<template>
  <div class="pdf-view">
    <div class="pdf-header">
      <DocumentTextIcon class="icon" />
      <span class="filename">{{ fileName }}</span>
    </div>
    <div class="pdf-content">
      <div v-if="isLoading" class="pdf-loading">
        <div class="loading-spinner"></div>
        <p class="loading-text">正在加载 PDF...</p>
      </div>
      <div class="pdf-container" ref="viewerRef"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import { DocumentTextIcon } from '@heroicons/vue/24/outline'
import { useDark } from '@vueuse/core'

const isDark = useDark()
const props = defineProps({
  file: {
    type: [File, null],
    default: null
  },
  fileName: {
    type: String,
    default: ''
  }
})

const isLoading = ref(false)
const viewerRef = ref(null)
let instance = null

// 使用简单的 PDF.js 实现
onMounted(async () => {
  if (viewerRef.value && props.file) {
    try {
      isLoading.value = true
      
      // 创建 iframe 元素
      const iframe = document.createElement('iframe')
      iframe.style.width = '100%'
      iframe.style.height = '100%'
      iframe.style.border = 'none'
      
      // 创建 Blob URL
      const url = URL.createObjectURL(props.file)
      iframe.src = url
      
      // 清空容器并添加 iframe
      viewerRef.value.innerHTML = ''
      viewerRef.value.appendChild(iframe)
      
      // 监听 iframe 加载完成
      iframe.onload = () => {
        isLoading.value = false
      }
      
      // 保存 URL 以便清理
      instance = { url }
      
    } catch (error) {
      console.error('PDF 查看器初始化失败:', error)
      isLoading.value = false
    }
  }
})

// 创建 iframe 并设置主题
const createIframe = (file) => {
  const iframe = document.createElement('iframe')
  iframe.style.width = '100%'
  iframe.style.height = '100%'
  iframe.style.border = 'none'
  
  // 创建 Blob URL
  const url = URL.createObjectURL(file)
  
  // 根据当前主题设置 iframe 的背景色
  if (isDark.value) {
    iframe.style.backgroundColor = '#1a1a1a'
  } else {
    iframe.style.backgroundColor = '#ffffff'
  }
  
  iframe.src = url
  return { iframe, url }
}

// 监听文件变化
watch(() => props.file, (newFile) => {
  if (newFile) {
    // 重新挂载组件
    if (instance?.url) {
      URL.revokeObjectURL(instance.url)
    }
    
    try {
      isLoading.value = true
      
      const { iframe, url } = createIframe(newFile)
      
      // 清空容器并添加 iframe
      if (viewerRef.value) {
        viewerRef.value.innerHTML = ''
        viewerRef.value.appendChild(iframe)
      }
      
      // 监听 iframe 加载完成
      iframe.onload = () => {
        isLoading.value = false
      }
      
      // 保存 URL 以便清理
      instance = { url, iframe }
      
    } catch (error) {
      console.error('加载 PDF 失败:', error)
      isLoading.value = false
    }
  }
})

// 监听主题变化
watch(() => isDark.value, (newIsDark) => {
  if (instance?.iframe) {
    if (newIsDark) {
      instance.iframe.style.backgroundColor = '#1a1a1a'
    } else {
      instance.iframe.style.backgroundColor = '#ffffff'
    }
  }
})

onUnmounted(() => {
  if (instance?.url) {
    URL.revokeObjectURL(instance.url)
  }
})
</script>

<style scoped lang="scss">
.pdf-view {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  background: #fff;

  .pdf-header {
    padding: 1rem;
    display: flex;
    align-items: center;
    gap: 1rem;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.98);
    z-index: 1;

    .icon {
      width: 1.5rem;
      height: 1.5rem;
      color: #666;
    }

    .filename {
      flex: 1;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .pdf-content {
    flex: 1;
    position: relative;
    overflow: hidden;

    .pdf-container {
      width: 100%;
      height: 100%;
    }

    .pdf-loading {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
      background: rgba(255, 255, 255, 0.9);
      padding: 2rem;
      border-radius: 1rem;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      z-index: 2;

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
  }
}

// 暗色模式支持
.dark {
  .pdf-view {
    background: #1a1a1a;
    border-right-color: rgba(255, 255, 255, 0.1);

    .pdf-header {
      background: rgba(30, 30, 30, 0.98);
      border-bottom-color: rgba(255, 255, 255, 0.1);

      .icon {
        color: #999;
      }

      .filename {
        color: #fff;
      }
    }

    .pdf-content {
      background: #0d0d0d;

      .pdf-loading {
        background: rgba(30, 30, 30, 0.9);

        .loading-spinner {
          border-color: rgba(0, 124, 240, 0.2);
          border-left-color: #007CF0;
        }

        .loading-text {
          color: #999;
        }
      }
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 
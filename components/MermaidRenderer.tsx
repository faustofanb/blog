// components/MermaidRenderer.tsx (新文件)
'use client'

import { useEffect, useRef } from 'react'
import mermaid from 'mermaid'
import { useTheme } from 'next-themes'

export default function MermaidRenderer() {
  const { theme, resolvedTheme } = useTheme()
  // 使用 useRef 来跟踪哪些 pre 元素已经被处理过，避免重复处理
  const processedPres = useRef(new Set<HTMLPreElement>())

  useEffect(() => {
    // 延迟执行以尝试避开 React Hydration
    const timer = setTimeout(() => {
      const currentTheme = theme === 'system' ? resolvedTheme : theme
      const isDark = currentTheme === 'dark'
      // 查找所有尚未处理的 mermaid 代码块
      const mermaidBlocks = document.querySelectorAll('pre code.language-mermaid')

      if (mermaidBlocks.length > 0) {
        try {
          mermaid.initialize({
            startOnLoad: false,
            theme: 'dark',
          })

          mermaidBlocks.forEach((block, index) => {
            const codeElement = block as HTMLElement // 类型断言
            const preElement = codeElement.closest('pre')

            // 检查 preElement 是否存在且未被处理
            if (!preElement || processedPres.current.has(preElement)) {
              return // 跳过已处理或无效的块
            }

            const code = codeElement.textContent || ''
            const id = `mermaid-graph-${Date.now()}-${index}`
            // 创建一个新的 div 用于容纳 SVG
            const mermaidContainer = document.createElement('div')
            mermaidContainer.className = 'mermaid-container flex justify-center'
            // 添加一个 data 属性标记，方便识别和清理
            mermaidContainer.dataset.mermaidProcessed = 'true'

            if (code) {
              mermaid
                .render(id, code)
                .then(({ svg, bindFunctions }) => {
                  // 再次检查 preElement 是否还存在（以防万一）
                  if (document.body.contains(preElement)) {
                    // 隐藏原始的 code 元素
                    codeElement.style.display = 'none'
                    // 将 SVG 容器追加到 pre 元素内部
                    mermaidContainer.innerHTML = svg
                    preElement.appendChild(mermaidContainer)
                    if (bindFunctions) {
                      bindFunctions(mermaidContainer)
                    }
                    // 标记此 pre 元素已处理
                    processedPres.current.add(preElement)
                  }
                })
                .catch((error) => {
                  console.error('Mermaid rendering failed for block:', error, code)
                  if (document.body.contains(preElement)) {
                    preElement.innerHTML = `<code class="text-red-600 dark:text-red-400">Mermaid Error: ${error.message || error}</code>`
                  }
                })
            } else {
              console.warn('Mermaid: Code content is empty for a block.')
            }
          })
        } catch (initError) {
          console.error('Failed to initialize Mermaid:', initError)
        }
      }
    }, 0) // 0ms 延迟

    // --- 清理函数 ---
    // 当组件卸载或依赖项变化导致 effect 重新运行时执行
    return () => {
      clearTimeout(timer) // 清除可能未执行的 timeout

      // 查找所有被我们处理过的容器并恢复原始状态
      processedPres.current.forEach((preElement) => {
        // 检查 preElement 是否还在 DOM 中
        if (document.body.contains(preElement)) {
          const codeElement = preElement.querySelector(
            'code.language-mermaid'
          ) as HTMLElement | null
          const mermaidContainer = preElement.querySelector('div[data-mermaid-processed="true"]')

          // 移除添加的 SVG 容器
          if (mermaidContainer) {
            preElement.removeChild(mermaidContainer)
          }
          // 恢复原始 code 元素的显示
          if (codeElement) {
            codeElement.style.display = '' // 恢复默认显示
          }
        }
      })
      // 清空已处理集合，以便下次 effect 运行时能重新处理
      processedPres.current.clear()
    }
    // --- 清理函数结束 ---
  }, [theme, resolvedTheme]) // 依赖项：主题变化时重新渲染

  return null // 组件本身不渲染 UI
}

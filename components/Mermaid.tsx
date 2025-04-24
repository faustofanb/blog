// components/Mermaid.tsx
'use client' // 标记这是一个客户端组件

import React, { useEffect, useRef } from 'react'
import mermaid from 'mermaid'
import { useTheme } from 'next-themes' // 导入 useTheme 以获取当前主题

const Mermaid = ({ chart }: { chart: string }) => {
  const containerRef = useRef<HTMLDivElement>(null)
  const { theme, resolvedTheme } = useTheme() // 获取当前主题设置
  // 确定实际生效的主题（考虑 'system' 设置）
  const currentTheme = theme === 'system' ? resolvedTheme : theme
  const isDark = currentTheme === 'dark'

  useEffect(() => {
    if (containerRef.current) {
      // 初始化 Mermaid
      mermaid.initialize({
        startOnLoad: false, // 我们将手动触发渲染
        theme: 'dark', // 使用类型安全的主题
        // 如果需要，可以在这里添加更多 Mermaid 配置选项
        // securityLevel: 'loose', // 如果需要处理用户输入，请考虑安全级别
      })

      // 渲染传入的图表代码
      mermaid
        .render('mermaid-graph-' + Date.now(), chart) // 使用唯一 ID 生成 SVG
        .then(({ svg, bindFunctions }) => {
          if (containerRef.current) {
            // 将生成的 SVG 放入容器
            containerRef.current.innerHTML = svg
            // 绑定交互事件（如果图表有的话）
            if (bindFunctions) {
              bindFunctions(containerRef.current)
            }
          }
        })
        .catch((error) => {
          console.error('Mermaid 渲染失败:', error)
          if (containerRef.current) {
            // 在容器中显示错误信息，而不是图表
            containerRef.current.innerHTML = `<pre class="text-red-600 dark:text-red-400">Mermaid 错误:\n${chart}\n${error.message || error}</pre>`
          }
        })
    }
    // 依赖项：当图表代码或主题变化时，重新运行 effect
  }, [chart, isDark, theme, resolvedTheme])

  // 返回一个 div 容器，Mermaid SVG 将被注入其中
  return <div ref={containerRef} className="mermaid-container flex justify-center"></div>
}

export default Mermaid

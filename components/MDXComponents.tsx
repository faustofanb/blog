import TOCInline from 'pliny/ui/TOCInline'
import Pre from 'pliny/ui/Pre'
import BlogNewsletterForm from 'pliny/ui/BlogNewsletterForm'
import type { MDXComponents } from 'mdx/types'
import Image from './Image'
import CustomLink from './Link'
import TableWrapper from './TableWrapper'
//
// console.log('🚀 MDXComponents file loaded and executing!') // <--- 添加日志
// // 创建一个自定义的 Pre 组件来处理 Mermaid
// const CustomPre = (props: React.ComponentProps<'pre'>) => {
//   const child = props.children as React.ReactElement<React.ComponentProps<'code'>>
//
//   if (child && child.props && child.props.className?.includes('language-mermaid')) {
//     console.log('✅ Mermaid block identified by CustomPre!') // <-- 添加日志
//     let codeContent = ''
//     // --- 修改开始 ---
//     // 健壮地获取代码内容：先检查类型是否为 string
//     if (typeof child.props.children === 'string') {
//       codeContent = child.props.children.trim()
//     } else {
//       // 如果不是字符串，可以打印一个警告，或者尝试其他处理方式
//       // 但对于代码块，最常见的情况应该是字符串或 null/undefined
//       console.warn('Mermaid code block content is not a string:', child.props.children)
//       // 在这种情况下，我们可能无法渲染 Mermaid 图，可以选择返回原始 pre 或显示错误
//       // 这里我们选择让 codeContent 保持为空字符串，下方逻辑会处理
//     }
//     // --- 修改结束 ---
//
//     // 仅当成功提取到非空代码时才渲染 Mermaid 组件
//     if (codeContent) {
//       return <Mermaid chart={codeContent} />
//     } else {
//       // 如果代码内容为空或无法提取，可以选择渲染原始 pre 或提示信息
//       console.warn('Could not extract valid code for Mermaid diagram. Rendering original pre tag.')
//       return <pre {...props} /> // 回退到渲染原始 pre 标签
//     }
//   }
//
//   // 如果不是 Mermaid 代码块，则使用 pliny 提供的默认 Pre 组件
//   // 确保 children 属性被正确传递
//   return <Pre {...props}>{props.children}</Pre>
// }

export const components: MDXComponents = {
  Image,
  TOCInline,
  a: CustomLink,
  pre: Pre,
  table: TableWrapper,
  BlogNewsletterForm,
}

/**
 * 图谱导出相关
 */

// svg 元素转为 png 图像
export const svgToPng = (svg, pngWidth, pngHeight) => {
  return new Promise((resolve, reject) => {
    const serializer = new XMLSerializer()
    const source = `<?xml version="1.0" standalone="no"?>${serializer.serializeToString(
      svg.node()
    )}`
    const image = new Image() // 准备图片
    // 准备画布
    const canvas = document.createElement('canvas')
    canvas.width = pngWidth
    canvas.height = pngHeight
    const context = canvas.getContext('2d')
    context.fillStyle = '#ffffff' //设置保存后的PNG 是白色的
    context.fillRect(0, 0, 10000, 10000)
    // 回调
    image.onload = () => {
      context.drawImage(image, 0, 0)
      const dataUrl = canvas.toDataURL('image/png')
      resolve(dataUrl)
    }
    image.onerror = err => {
      reject(err)
    }
    image.src = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(source)}`
  })
}

export const download = (name, dataUrl) => {
  const a = document.createElement('a')
  a.download = name
  a.href = dataUrl
  a.click()
}

// 导出 xml 文件
export const xmlDownload = (content, filename) => {
  // 创建隐藏的可下载链接
  const eleLink = document.createElement('a')
  eleLink.download = filename
  eleLink.style.display = 'none'
  // 字符内容转变成blob地址
  const blob = new Blob([content], { type: 'text/xml' })
  eleLink.href = URL.createObjectURL(blob)
  // 触发点击
  document.body.appendChild(eleLink)
  eleLink.click()
  // 然后移除
  document.body.removeChild(eleLink)
}

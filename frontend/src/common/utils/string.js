// 首字母大写
export const capitalize = s => {
  if (!s) return ''
  const c = s.charAt(0).toUpperCase()
  return `${c}${s.substring(1)}`
}
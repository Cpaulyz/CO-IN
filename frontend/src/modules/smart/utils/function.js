export const linebreakFormat = text => {
  if (text.indexOf('\\n') !== -1) {
    return String(text)
      .replace(/\r\\n/g, '<br/>') // windows
      .replace(/\\n/g, '<br/>') // linux
  }
  return text
}

export const adapter = adaptee => {
  const target = []
  adaptee.forEach(value => {
    target.push({ value })
  })
  return target
}

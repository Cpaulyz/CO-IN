export const buttonAutoBlur = e => {
  let target = e.target
  if (target.nodeName == 'SPAN' || target.nodeName == 'I') {
    target = e.target.parentNode
  }
  target.blur()
}

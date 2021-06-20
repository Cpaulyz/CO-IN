export const debounceAndThrottle = (fn, ms, ms2) => {
  ms2 = ms2 || ms
  let timer = null
  let valid = true
  return (...args) => {
    if (!valid) return
    clearTimeout(timer)
    timer = setTimeout(() => {
      valid = false
      fn(...args)
      setTimeout(() => {
        valid = true
      }, ms)
      timer = null
    }, ms)
  }
}

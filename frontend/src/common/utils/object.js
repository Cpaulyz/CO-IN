// 深拷贝
export const deepCopy = obj => {
  if (obj !== null && typeof obj === 'object') {
    const newObj = obj.constructor()
    for (const prop in obj) {
      newObj[prop] = deepCopy(obj[prop])
    }
    return newObj
  } else {
    return obj
  }
}


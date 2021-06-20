import JSEncrypt from 'jsencrypt'

export const objectToHttpQuery = data => {
  let ret = ''
  for (const key in data) {
    const value = data[key]
    ret += `${encodeURIComponent(key)}=${encodeURIComponent(value)}&`
  }
  return ret.substring(0, ret.length - 1)
}

export const encrypt = (plainString, publicKey) => {
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(publicKey)
  return encryptor.encrypt(plainString)
}

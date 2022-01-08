import axios from 'axios'
import { baseURL } from './config'
import { consoleGroup } from '@/common/utils/'
import router, { setRecentRoute } from '@/router'
import { $message } from '../common/utils'

const instance = axios.create({
  baseURL,
  withCredentials: true
})

const apiConsole = true

instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('coin-token')
    if (token) {
      config.headers['coin-token'] = token
    }
    // config.headers['coin-token'] = expiredToken
    if (apiConsole) {
      consoleGroup(`[axios.request] ${config.url}`, () => {
        console.log(config)
      })
    }
    return config
  },
  err => {
    return err
  }
)

instance.interceptors.response.use(
  response => {
    const config = response.config
    if (apiConsole) {
      consoleGroup(`[axios.response] ${config.url}`, () => {
        console.log(response)
      })
    }
    return response
  },
  err => {
    const response = err.response
    const config = response.config
    consoleGroup(
      `[axios.response.error] ${
        config ? config.url : response.request.responseURL
      }`,
      () => {
        console.log(response)
      }
    )
    if (response.status === 403) {
      // 未登入 or token 过期
      setRecentRoute()
      $message('登入已过期，请重新登入', 'error')
      router.push('/user')
    }
    return response
  }
)

export default instance

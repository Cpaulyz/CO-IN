export default {
  prefix: '/user',
  configs: {
    login(userInfo) {
      return { path: '/login', method: 'POST', data: userInfo }
    },
    logout() {
      return { path: '/logout', method: 'GET' }
    },
    userInfo() {
      return { path: '/info' }
    },
    register(userInfo) {
      return { path: '/register', method: 'POST', data: userInfo }
    },
    getPublicKey() {
      return { path: '/pubKey', method: 'GET' }
    }
  }
}

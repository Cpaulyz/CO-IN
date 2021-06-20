// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import vuex from 'vuex'
import store from './store'

Vue.use(vuex)

// element-ui
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI)

// d3
import * as d3 from 'd3'
Vue.prototype.$d3 = d3

// axios
import axios from './api/axios'
Vue.prototype.$axios = axios
Vue.config.productionTip = false

// echarts
import * as echarts from 'echarts'
Vue.prototype.$echarts = echarts

import Chat from 'jwchat'
Vue.use(Chat)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})

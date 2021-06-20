import Vue from 'vue'
import Vuex from 'vuex'
import graph from '@/modules/graph/store'
import home from '@/modules/home/store'
import user from '@/modules/user/store'
import smart from '@/modules/smart/store'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    graph,
    home,
    user,
    smart,
  }
})

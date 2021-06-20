import Vue from 'vue'
import Router from 'vue-router'

import Home from '@/views/Home.vue'
import { $message } from '../common/utils'
import store from '../store'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/user',
      component: () => import('@/views/User.vue'),
      children: [
        {
          path: '',
          name: 'Login',
          component: () => import('@/modules/user/components/Login.vue')
        },
        {
          path: 'register',
          name: 'Register',
          component: () => import('@/modules/user/components/Register.vue')
        }
      ]
    },
    {
      path: '/',
      name: 'Home',
      component: Home,
      meta: {
        requireLogin: true
      },
      children: [
        {
          path: '',
          name: 'HomeProjects',
          component: () => import('@/modules/home/components/HomeProjects.vue')
        },
        {
          path: 'square',
          name: 'HomeSquare',
          component: () => import('@/modules/home/components/HomeSquare.vue')
        },
        {
          path: 'chat',
          name: 'Chat',
          component: () => import('@/modules/home/components/Chat.vue')
        },
        {
          path: 'tutorial',
          name: 'Tutorial',
          component: () => import('@/modules/home/components/Tutorial.vue')
        },
        {
          path: 'systemdesign',
          name: 'SystemDesign',
          component: () => import('@/modules/home/components/SystemDesign.vue')
        },
      ]
    },
    {
      path: '/graph/:projectId',
      name: 'Graph',
      component: () => import('@/views/Graph.vue'),
      meta: {
        requireLogin: true
      }
    },
    // {
    //   path: '/chat/:projectId',
    //   name: 'Chat',
    //   component: () => import('@/views/Chat.vue'),
    //   meta: {
    //     requireLogin: true
    //   }
    // },
    {
      path: '/smarthelper/:projectId',
      name: 'Smarthelper',
      component: () => import('@/views/Smarthelper.vue'),
      meta: {
        requireLogin: true
      }
    },
    {
      path: '/notfound',
      name: 'Notfound',
      component: () => import('@/views/Notfound.vue')
    },
    {
      path: '*',
      redirect: '/notfound'
    }
  ]
})

let recentRoute = null

export const setRecentRoute = to => {
  recentRoute = to || router.currentRoute
  console.log('set recentRoute', recentRoute)
}

const setRecentAndGoLogin = (to, next, msg) => {
  setRecentRoute(to)
  $message(msg, 'error')
  next('/user')
}

const goNextWithCheck = next => {
  if (recentRoute) {
    const target = recentRoute
    recentRoute = null
    next(target)
  } else {
    next()
  }
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('coin-token')
  if (to.meta.requireLogin) {
    // need login
    if (token) {
      // has token(login before)
      if (!store.getters.userId) {
        // repull userInfo
        store.dispatch('getUserInfo').then(res => {
          if (res) {
            goNextWithCheck(next)
          } else {
            setRecentAndGoLogin(to, next, 'token 过期，请重新登入')
          }
        })
      } else {
        goNextWithCheck(next)
      }
    } else {
      // no token
      setRecentAndGoLogin(to, next, '请先完成登陆')
    }
  } else {
    // no need login
    next()
  }
})

const originalPush = Router.prototype.push
Router.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject)
    return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(failure => {
    console.log(failure)
    return failure
  })
}

export default router

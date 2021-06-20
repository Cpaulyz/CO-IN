<template>
  <div class="register">
    <div class="register-title">
      <span class="backLogin">
        <el-button
          icon="el-icon-arrow-left"
          style="border: none"
          circle
          @click="gotoLogin()"
        >
        </el-button>
      </span>
      Register
    </div>
    <el-form
      :model="registerForm"
      :rules="registerRules"
      ref="registerForm"
      label-width="80px"
      style="margin-top: 40px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          type="username"
          v-model="registerForm.username"
          autocomplete="off"
        >
        </el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input type="email" v-model="registerForm.email" autocomplete="off">
        </el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          type="password"
          v-model="registerForm.password"
          autocomplete="off"
        >
        </el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="checkPassword">
        <el-input
          type="password"
          v-model="registerForm.checkPassword"
          autocomplete="off"
        >
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="register()">注册</el-button>
        <el-button @click="resetForm('registerForm')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapMutations, mapActions } from 'vuex'
import { $message } from '@/common/utils'

export default {
  name: 'Register',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入用户名'))
      } else {
        callback()
      }
    }
    const validateEmail = (rule, value, callback) => {
      function emailFormat(email) {
        if (email != '') {
          let reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/
          let isok = reg.test(email)
          if (isok) {
            return true
          } else {
            return false
          }
        }
      }
      if (value === '') {
        callback(new Error('请输入邮箱'))
      } else if (!emailFormat(value)) {
        callback(new Error('邮箱格式有误'))
      } else {
        callback()
      }
    }
    const validateRegisterPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.registerForm.checkPassword !== '') {
          this.$refs.registerForm.validateField('password')
        }
        callback()
      }
    }

    const validatePasswordSame = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        email: '',
        password: '',
        checkPassword: ''
      },
      registerRules: {
        username: [{ validator: validateUsername, trigger: 'blur' }],
        email: [{ validator: validateEmail, trigger: 'blur' }],
        password: [{ validator: validateRegisterPassword, trigger: 'blur' }],
        checkPassword: [{ validator: validatePasswordSame, trigger: 'blur' }]
      }
    }
  },
  methods: {
    ...mapMutations(['setShowLogin']),
    ...mapActions(['userRegister']),
    register() {
      const userInfo = { ...this.registerForm }
      this.userRegister(userInfo).then(success => {
        if (success) {
          this.$router.push('/user')
        }
      })
    },
    gotoLogin() {
      this.$router.back()
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>

<style>
.register {
  width: 40vw;
  position: fixed;
  left: 29%;
  top: 20%;
  border-radius: 20px;
  box-shadow: 1px 1px 1px 1px slategray;
  background-color: #ffffff;
  padding: 10px;
}

.register > .register-title {
  text-align: center;
  font-size: 25px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px outset royalblue;
}

.box > .register > .register-title > .backLogin {
  position: absolute;
  left: 10px;
}

.el-input {
  width: 90%;
}
</style>

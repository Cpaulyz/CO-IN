<template>
  <div class="box">
    <div class="logo">
      <Logo />
    </div>
    <router-view></router-view>
  </div>
</template>

<script>
import Logo from '../components/Logo.vue'

export default {
  name: 'User',
  components: { Logo }
}
</script>

<style>
.box {
  width: 100vw;
  height: 100vh;
}

.logo {
  width: 100%;
  text-align: center;
  border-bottom: 2px groove;
}
</style>

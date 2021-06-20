<template>
  <div class="box">
    <div class="logo">
      <img
        src="../assets/coin.png"
        alt="coin logo"
        width="200px"
      />
    </div>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'User'
}
</script>

<style>
.logo {
  width: 100vw;
  text-align: center;
  border-bottom: 2px groove;
}

.box {
  width: 100vw;
  height: 100vh;
}
</style>

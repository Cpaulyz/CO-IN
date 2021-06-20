<template>
  <div class="notfound">
    <img src="../assets/notfound.svg" alt="404" />
    <div style="color: #456; font-size: 30px; margin-top: 30px">
      Page Not Found
    </div>
    <div style="color: #666; font-size: 15px; margin-top: 20px">
      Make sure the address is correct and the page hasn't moved.
    </div>
  </div>
</template>

<script>
export default {
  name: 'Notfound',
}
</script>

<style>
.notfound {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fdfcf8;
}
</style>

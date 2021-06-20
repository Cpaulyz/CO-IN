<template>
  <keep-alive>
    <div class="document">
      <iframe :src="src" frameborder="0" class="iframeSystemDesign"></iframe>
    </div>
  </keep-alive>
</template>

<script>
export default {
  name: 'SystemDesign',
  data() {
    return {
      src: '../../static/systemdesign.html'
    }
  }
}
</script>

<style scoped>
.document {
  width: 100%;
  height: 100%;
}

.iframeSystemDesign {
  width: 100%;
  height: 100%;
}
</style>

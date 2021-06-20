<template>
  <keep-alive>
    <div class="document">
      <iframe :src="src" frameborder="0" class="iframeTutorial"></iframe>
    </div>
  </keep-alive>
</template>

<script>
export default {
  name: 'Tutorial',
  data() {
    return {
      src: '../../static/guide.html'
    }
  }
}
</script>

<style scoped>
.document {
  width: 100%;
  height: 100%;
}

.iframeTutorial {
  width: 100%;
  height: 100%;
}
</style>

<template>
  <div class="list">
    <el-button
      v-for="project in allProjects"
      :key="project.projectId"
      type="primary"
      style="width: 100%; margin: 0 0 15px 0"
      round
      @click="gotoSmarthelper(project.projectId)"
    >
      项目：{{ project.name }}
    </el-button>
    <el-pagination
      layout="prev, pager, next"
      :total="allListCount"
      @current-change="switchPageAll"
      :current-page="allPageNo"
    >
    </el-pagination>
  </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
export default {
  name: 'HomeSquare',
  computed: {
    ...mapGetters(['allProjects', 'allPageNo', 'allListCount']),
  },
  methods: {
    ...mapMutations(['setAllPageNo']),
    ...mapActions(['getAllListByPageNo']),
    gotoSmarthelper(id) {
      this.$router.push(`/smarthelper/${id}`)
    },
    switchPageAll(currPageNo) {
      // console.log('switchPage', currPageNo)
      this.setAllPageNo(currPageNo)
      this.getAllListByPageNo(currPageNo)
    },
  },
}
</script>

<style scoped>
.list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 500px;
}
</style>

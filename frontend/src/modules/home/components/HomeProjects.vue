<template>
  <div class="list">
    <el-button
      v-for="project in ownProjects"
      :key="project.projectId"
      type="primary"
      style="width: 100%; margin: 0 0 15px 0"
      round
      @click="gotoProject(project.projectId)"
    >
      项目：{{ project.name }}
    </el-button>
    <el-pagination
      layout="prev, pager, next"
      :total="ownListCount"
      @current-change="switchPageOwn"
      :current-page="ownPageNo"
    >
    </el-pagination>
  </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
export default {
  name: 'HomeProjects',
  computed: {
    ...mapGetters(['ownProjects', 'ownListCount', 'ownPageNo'])
  },
  methods: {
    ...mapMutations(['setOwnPageNo']),
    ...mapActions(['getOwnListByPageNo']),
    gotoProject(id) {
      this.$router.push(`/graph/${id}`)
    },
    switchPageOwn(currPageNo) {
      this.setOwnPageNo(currPageNo)
      this.getOwnListByPageNo({
        userId: this.userId,
        pageNo: this.ownPageNo
      })
    }
  }
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

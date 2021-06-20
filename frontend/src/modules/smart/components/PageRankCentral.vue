<template>
  <div class="pagerankC">
    <el-collapse v-model="activeNames">
      <el-collapse-item
        v-for="item in pageRankCentralData"
        :key="item.group"
        :title="item.group"
      >
        <el-table :data="item.items" style="width: 100%">
          <el-table-column prop="rank" label="排名"></el-table-column>
          <el-table-column prop="nodeId" label="nodeId"></el-table-column>
          <el-table-column prop="name" label="名称"></el-table-column>
          <el-table-column prop="score" label="得分"></el-table-column>
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
// import { mockPageRankCentral } from '../utils/data'

export default {
  name: 'PageRankCentral',
  data() {
    return {
      activeNames: '1',
      pageRankCentralData: null,
    }
  },
  async mounted() {
    const projectId = Number(this.$route.params.projectId)
    await this.getCentralityData(projectId)
    this.pageRankCentralData = this.centralityTableData
  },
  computed: {
    ...mapGetters(['centralityTableData']),
  },
  methods: {
    ...mapActions(['getCentralityData']),
  },
}
</script>

<style>
.pagerankC {
  width: 100%;
  height: 100%;
  overflow: scroll;
}
</style>

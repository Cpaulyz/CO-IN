<template>
  <div class="smartbox">
    <el-button
      circle
      icon="el-icon-arrow-left"
      style="margin: 0 10px 15px 0"
      @click="goback()"
    ></el-button>
    <span>项目：{{ projectInfo.name }}</span>
    <el-button round type="text" icon="el-icon-info" @click="showDescription()"
      >关于</el-button
    >
    <el-tabs
      :tab-position="tabPosition"
      :before-leave="tabClick"
      :value="graphDialogueName"
      type="border-card"
      style="height: 100%"
    >
      <el-tab-pane label="图谱问答" :name="graphDialogueName">
        <GraphDialogue v-if="show.graphDialogue" />
      </el-tab-pane>
      <el-tab-pane label="实体查询" :name="entityQueryName">
        <EntityQuery v-if="show.entityQuery" />
      </el-tab-pane>
      <el-tab-pane label="关系查询" :name="relationQueryName">
        <RelationQuery v-if="show.relationQuery" />
      </el-tab-pane>
      <el-tab-pane label="中心识别" :name="pageRankCentralName">
        <PageRankCentral v-if="show.pageRankCentral" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import GraphDialogue from '../modules/smart/components/GraphDialogue.vue'
import EntityQuery from '../modules/smart/components/EntityQuery.vue'
import RelationQuery from '../modules/smart/components/RelationQuery.vue'
import PageRankCentral from '../modules/smart/components/PageRankCentral.vue'
import { mapActions } from 'vuex'

export default {
  components: {
    GraphDialogue,
    EntityQuery,
    RelationQuery,
    PageRankCentral,
  },
  name: 'Smarthelper',
  data() {
    return {
      tabPosition: 'left',
      graphDialogueName: 'graphDialogue',
      entityQueryName: 'entityQuery',
      relationQueryName: 'relationQuery',
      pageRankCentralName: 'pageRankCentral',
      show: {
        graphDialogue: true,
        entityQuery: false,
        relationQuery: false,
        pageRankCentral: false,
      },
      projectInfo: {},
    }
  },
  async mounted() {
    const projectId = Number(this.$route.params.projectId)
    this.projectInfo = await this.getProjectInfo(projectId)
    this.initGraph()
  },
  methods: {
    ...mapActions(['initiateGraph', 'getProjectInfo']),
    goback() {
      this.$router.back()
    },
    tabClick(activeName, oldActiveName) {
      // console.log(activeName, oldActiveName)
      this.show[activeName] = true
      this.show[oldActiveName] = false
    },
    initGraph() {
      const projectId = Number(this.$route.params.projectId)
      this.initiateGraph(projectId).then((res) => {
        const { success, msg } = res.data
        if (success) {
          this.$message.success(msg)
        } else {
          this.$message.error(msg)
        }
      })
    },
    showDescription() {
      this.$notify({
        title: `关于 ${this.projectInfo.name}`,
        message: this.projectInfo.description,
        duration: 5000,
        type: 'info',
        position: 'bottom-left',
      })
    },
  },
}
</script>

<style>
.smartbox {
  width: calc(100vw - 40px);
  height: calc(90vh - 40px);
  padding: 20px;
  background-color: #fdfcf8;
}
.el-tabs--border-card > .el-tabs__content {
  height: calc(100% - 30px);
}
.el-tabs--border-card > .el-tabs__content > .el-tab-pane {
  height: 100%;
}
</style>

<template>
  <div class="entityq">
    <div class="ask">
      <span class="header">
        <i class="el-icon-share" style="font-size: large; margin-right: 10px" />
        实体查询
      </span>
      <div class="input">
        <el-input
          placeholder="请输入实体名称"
          v-model="entityQueryinput"
          clearable
          style="width: 80%; margin-right: 20px"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="entityQuerySearch()"
          ></el-button>
        </el-input>
        <el-switch
          v-model="fuzzyQuery"
          active-color="#13ce66"
          inactive-color="#ff4949"
          active-text="模糊查询"
        ></el-switch>
      </div>
    </div>
    <div class="graph">
      <span class="header">关系图</span>
      <div class="show">
        <GraphBoard ref="entityQueryBoard" />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapMutations, mapActions } from 'vuex'
import GraphBoard from '../../graph/components/GraphBoard'

export default {
  name: 'EntityQuery',
  components: {
    GraphBoard,
  },
  data() {
    return {
      entityQueryinput: '',
      graphData: null,
      fuzzyQuery: true,
    }
  },
  mounted() {
    this.entityQueryinput = this.entityQueryQues
    if (!this.entityQueryGraphData) {
      return
    } else {
      this.renderGraph()
    }
  },
  computed: {
    ...mapGetters(['entityQueryQues', 'entityQueryGraphData']),
  },
  methods: {
    ...mapMutations(['setEntityQueryQues']),
    ...mapActions(['getProjectInfo', 'smartEntityQuery']),
    renderGraph() {
      this.graphData = this.entityQueryGraphData
      const entityQueryBoard = this.$refs.entityQueryBoard
      entityQueryBoard.mountGraphData(this.graphData)
    },
    entityQuerySearch() {
      this.setEntityQueryQues(this.entityQueryinput)
      const projectId = Number(this.$route.params.projectId)
      const question = {
        entityName: this.entityQueryinput,
        fuzzyQuery: this.fuzzyQuery,
        projectId,
      }
      this.smartEntityQuery(question).then((res) => {
        if (res.nodes.length === 0) {
          this.$message.warning('查无结果！')
        } else {
          this.renderGraph()
        }
      })
    },
  },
}
</script>

<style>
.entityq {
  width: 100%;
  height: 100%;
}
.entityq > .ask {
  height: 18%;
  box-shadow: 1px 1px 1px 1px slategray;
  margin-bottom: 25px;
}
.header {
  display: flex;
  align-items: center;
  height: 35px;
  background-color: #f6f8fa;
  font-size: small;
  padding: 0 10px;
}
.entityq > .ask > .input {
  padding: 10px;
  height: calc(65% - 20px);
}
.entityq > .graph {
  width: 100%;
  height: 75%;
  box-shadow: 1px 1px 1px 1px slategray;
}
.entityq > .graph > .show {
  height: calc(100% - 35px);
}
</style>

<template>
  <div class="graphd">
    <div class="ask">
      <span class="header">
        <i
          class="el-icon-question"
          style="font-size: large; margin-right: 10px"
        />
        图谱问答
      </span>
      <div class="input">
        <el-autocomplete
          placeholder="请输入问题"
          v-model="graphDialogueinput"
          clearable
          :fetch-suggestions="recommendQues"
          style="width: 100%"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="graphDialogueSearch()"
          ></el-button>
        </el-autocomplete>
      </div>
    </div>
    <div class="answer">
      <span class="header">答案</span>
      <div class="text" v-html="this.dialogueAnswer">
        {{ this.dialogueAnswer }}
      </div>
    </div>
    <div class="graph">
      <span class="header">图谱演示</span>
      <div class="show">
        <GraphBoard ref="graphDialogueBoard" />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapMutations, mapActions } from 'vuex'
import GraphBoard from '../../graph/components/GraphBoard'
import { _graphData } from '../../graph/utils/data'

export default {
  name: 'GraphDialogue',
  components: {
    GraphBoard,
  },
  data() {
    return {
      graphDialogueinput: '',
      graphData: null,
      recommendHotQues: [],
    }
  },
  mounted() {
    const projectId = Number(this.$route.params.projectId)
    this.getHotQuestionList(projectId)
    this.graphDialogueinput = this.graphDialogueQues
    if (!this.dialogueQueryGraphData) {
      return
    } else {
      this.renderGraph()
    }
  },
  computed: {
    ...mapGetters([
      'graphDialogueQues',
      'dialogueQueryGraphData',
      'dialogueAnswer',
      'hotQuestions',
    ]),
  },
  methods: {
    ...mapMutations(['setGraphDialogueQues']),
    ...mapActions([
      'getProjectInfo',
      'smartDialogueQuery',
      'getHotQuestionList',
    ]),
    renderGraph() {
      this.graphData = this.dialogueQueryGraphData
      const graphDialogueBoard = this.$refs.graphDialogueBoard
      graphDialogueBoard.mountGraphData(this.graphData)
    },
    graphDialogueSearch() {
      this.setGraphDialogueQues(this.graphDialogueinput)
      const projectId = Number(this.$route.params.projectId)
      const question = {
        text: this.graphDialogueinput,
        projectId,
      }
      this.smartDialogueQuery(question).then((res) => {
        if (res.nodes.length === 0) {
          this.$message.warning('查无结果！')
        } else {
          this.renderGraph()
        }
      })
    },
    recommendQues(queryString, cb) {
      this.recommendHotQues = this.hotQuestions
      cb(this.recommendHotQues)
    },
  },
}
</script>

<style>
.graphd {
  width: 100%;
  height: 100%;
}
.graphd > .ask {
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
.graphd > .ask > .input {
  padding: 10px;
  height: calc(65% - 20px);
}
.graphd > .answer {
  width: 30%;
  height: 75%;
  float: left;
  box-shadow: 1px 1px 1px 1px slategray;
  margin-right: 30px;
}
.graphd > .graph {
  width: calc(70% - 30px);
  height: 75%;
  float: left;
  box-shadow: 1px 1px 1px 1px slategray;
}
.graphd > .graph > .show {
  height: calc(100% - 35px);
}
.graphd > .answer > .text {
  width: calc(100% - 20px);
  height: calc(100% - 55px);
  padding: 10px;
  font-family: serif;
  font-size: 15px;
  overflow: scroll;
}
/* .el-input-group {
  height: 100%;
}
.el-input__inner {
  height: 100%;
} */
</style>

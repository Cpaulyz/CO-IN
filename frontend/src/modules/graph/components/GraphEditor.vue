<template>
  <div class="editor">
    <!-- 图谱搜索框 -->
    <!-- <graph-editor-search></graph-editor-search> -->

    <!-- 选取查看组别 -->
    <GraphEditorGroup :groups="nodeGroups" @graph-action="passingGraphAction" />

    <el-divider></el-divider>
    <!-- 图谱内容查看/修改 -->
    <GraphEditorItem
      ref="item"
      :projectId="graphData && graphData.projectId"
      :nodeOptions="nodeOptions"
      :nodeGroups="nodeGroups"
      @graph-action="passingGraphAction"
    >
      <GraphEditorSearch
        :projectId="graphData && graphData.projectId"
        :nodes="safeNodes"
        :links="safeLinks"
        @editor-action="dispatchEditorAction"
        @graph-action="passingGraphAction"
      />
    </GraphEditorItem>

    <el-divider></el-divider>
    <!-- 图谱统计数据 -->
    <GraphEditorStatistics :statisticsData="statisticsData" />
  </div>
</template>

<script>
import GraphEditorSearch from './GraphEditorSearch'
import GraphEditorGroup from './GraphEditorGroup'
import GraphEditorItem from './GraphEditorItem'
import GraphEditorStatistics from './GraphEditorStatistics'

export default {
  name: 'GraphEditor',
  components: {
    GraphEditorSearch,
    GraphEditorGroup,
    GraphEditorItem,
    GraphEditorStatistics
  },
  props: {
    graphData: {
      type: Object
    },
    nodesScale: {
      type: Function
    },
    nodeOptions: {
      type: Array
    },
    nodeGroups: {
      type: Array
    }
  },
  data() {
    return {
      searchNodeName: ''
    }
  },
  computed: {
    safeNodes() {
      return this.graphData ? this.graphData.nodes : []
    },
    safeLinks() {
      return this.graphData ? this.graphData.links : []
    },
    statisticsData() {
      const nodes = this.graphData && this.graphData.nodes
      const nodesScale = this.nodesScale
      if (!nodes || !nodesScale) return []

      const dataMapper = {}
      nodes.forEach(({ group }) => {
        if (!Reflect.has(dataMapper, group)) {
          dataMapper[group] = 0
        }
        dataMapper[group]++
      })
      const data = Reflect.ownKeys(dataMapper).map(group => ({
        name: group,
        value: dataMapper[group],
        itemStyle: {
          color: nodesScale(group)
        }
      }))
      data.sort(({ name: x }, { name: y }) => (x < y ? -1 : x === y ? 0 : 1))
      return data
    }
  },
  methods: {
    dispatchEditorAction(name, ...args) {
      this.$refs.item[name](...args)
    },
    passingGraphAction(...args) {
      this.$emit('graph-action', ...args)
    }
  }
}
</script>

<style scoped>
.editor {
  padding: 16px;
}
</style>

<template>
  <div class="editor">
    <!-- 图谱内容查看/修改 -->
    <GraphEditorItem
      v-if="mode === EDITOR_MODE.Item"
      ref="item"
      :projectId="graphData && graphData.projectId"
      :nodeOptions="nodeOptions"
      :nodeGroups="nodeGroups"
      @graph-action="passingGraphAction"
    >
      <div>
        <!-- 选取查看组别 -->
        <GraphEditorGroup
          :groups="nodeGroups"
          @graph-action="passingGraphAction"
        />

        <el-divider></el-divider>
        <GraphEditorSearch
          :projectId="graphData && graphData.projectId"
          :nodes="safeNodes"
          :links="safeLinks"
          @editor-action="dispatchEditorAction"
          @graph-action="passingGraphAction"
        />
      </div>
    </GraphEditorItem>

    <!-- <el-divider></el-divider> -->
    <!-- 图谱统计数据 -->
    <GraphEditorStatistics
      v-else-if="mode === EDITOR_MODE.Statistics"
      :statisticsData="statisticsData"
      @exit="switchMode(EDITOR_MODE.Item)"
    />
  </div>
</template>

<script>
import GraphEditorSearch from './GraphEditorSearch';
import GraphEditorGroup from './GraphEditorGroup';
import GraphEditorItem from './GraphEditorItem';
import GraphEditorStatistics from './GraphEditorStatistics';

const EDITOR_MODE = {
  Item: '__editor_item',
  Statistics: '__editor_statistics',
};

export default {
  name: 'GraphEditor',
  components: {
    GraphEditorSearch,
    GraphEditorGroup,
    GraphEditorItem,
    GraphEditorStatistics,
  },
  props: {
    graphData: {
      type: Object,
    },
    nodesScale: {
      type: Function,
    },
    nodeOptions: {
      type: Array,
    },
    nodeGroups: {
      type: Array,
    },
  },
  data() {
    return {
      EDITOR_MODE,
      searchNodeName: '',
      mode: EDITOR_MODE.Item,
    };
  },
  computed: {
    safeNodes() {
      return this.graphData ? this.graphData.nodes : [];
    },
    safeLinks() {
      return this.graphData ? this.graphData.links : [];
    },
    statisticsData() {
      const nodes = this.graphData && this.graphData.nodes;
      const nodesScale = this.nodesScale;
      if (!nodes || !nodesScale) return [];

      const dataMapper = {};
      nodes.forEach(({ group }) => {
        if (!Reflect.has(dataMapper, group)) {
          dataMapper[group] = 0;
        }
        dataMapper[group]++;
      });
      const data = Reflect.ownKeys(dataMapper).map(group => ({
        name: group,
        value: dataMapper[group],
        itemStyle: {
          color: nodesScale(group),
        },
      }));
      data.sort(({ name: x }, { name: y }) => (x < y ? -1 : x === y ? 0 : 1));
      return data;
    },
  },
  methods: {
    dispatchEditorAction(name, ...args) {
      if (name === 'mode') {
        const [fn, ...others] = args;
        this[fn](...others);
      } else {
        this.dispatchItemAction(name, ...args);
      }
    },
    passingGraphAction(...args) {
      this.$emit('graph-action', ...args);
    },
    dispatchItemAction(fn, ...args) {
      this.switchMode(EDITOR_MODE.Item, () => {
        this.$refs.item[fn](...args);
      });
    },
    showStatistics() {
      if (this.mode !== EDITOR_MODE.Statistics) {
        this.switchMode(
          EDITOR_MODE.Statistics,
          () => {
            this.$refs.item.selectNone();
          },
          true,
        );
      }
    },
    switchMode(mode, next, before = false) {
      if (this.mode !== mode) {
        before && next && next();
        this.mode = mode;
        !before && next && this.$nextTick(next);
      } else {
        next && next();
      }
    },
  },
};
</script>

<style scoped></style>

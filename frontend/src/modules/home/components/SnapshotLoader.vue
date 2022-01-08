<template>
  <div class="loader-container">
    <GraphBoard ref="loader_graph_board" />
  </div>
</template>

<script>
import { mapActions } from 'vuex';

import GraphBoard from '../../graph/components/GraphBoard.vue';

export default {
  components: { GraphBoard },
  name: 'SnapshotLoader',
  methods: {
    ...mapActions(['getProjectInfo', 'getProjectGraphData']),
    async load(projectId) {
      return;
      // console.info(`[SnapshotLoader] load snapshot for ${projectId}`);
      // const projectInfo = await this.getProjectInfo(projectId);
      // const graphData = await this.getProjectGraphData(projectId);
      // console.log(
      //   `[SnapshotLoader] (projectId = ${projectId}).graphData`,
      //   graphData,
      // );

      // if (graphData) {
      //   const board = this.$refs.loader_graph_board;
      //   if (!board) {
      //     console.error(`[SnapshotLoader] loader_graph_board unmounted`);
      //     return;
      //   }
      //   await board.mountGraphData(graphData, projectInfo);
      //   const graphLoaded = board.flags.loaded;
      //   console.log(`[SnapshotLoader] board.flags.loaded = ${graphLoaded}`);

      //   if (!graphLoaded) {
      //     console.warn(
      //       `[SnapshotLoader] try catch snapshot when board unloaded`,
      //     );
      //   }

      //   const snapshot = await board.getSnapshot();
      //   console.log(`[SnapshotLoader] snapshot.length = ${snapshot.length}`);
      //   return snapshot;
      // }
    },
  },
};
</script>

<style scoped>
.loader-container {
  position: fixed;
  top: -100vh;
  width: 600px;
  height: 400px;
}
</style>

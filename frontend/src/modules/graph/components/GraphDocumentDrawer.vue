<template>
  <el-drawer title="co$in 用户手册" :visible.sync="showDrawer" size="70%">
    <div class="body">
      <Document ref="graph_document_drawer_document" :toc="false" />
    </div>
  </el-drawer>
</template>

<script>
import Document from '../../home/components/Document.vue';

export default {
  components: { Document },
  name: 'GraphDocumentDrawer',
  data() {
    return {
      showDrawer: false,
    };
  },
  methods: {
    check(id) {
      console.log(`[GraphDocumentDrawer] check document(id = ${id})`);
      this.showDrawer = true;
      if (id) {
        this.$nextTick(() => {
          const _d = this.$refs.graph_document_drawer_document;
          _d.onReady(() => {
            console.log('Document onReady', _d);
            _d.checkId(id);
          });
        });
      }
    },
  },
  mounted() {
    this.showDrawer = true;
    this.showDrawer = false;
  },
};
</script>

<style scoped>
.body {
  height: 100%;
}
</style>

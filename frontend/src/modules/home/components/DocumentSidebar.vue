<template>
  <div class="document-sidebar">
    <DocumentSidebarHeader
      v-for="h in headers"
      :key="h.id"
      :header="h"
      :setAnchor="setAnchor"
    />
  </div>
</template>

<script>
import DocumentSidebarHeader from './DocumentSidebarHeader.vue';
export default {
  components: { DocumentSidebarHeader },
  name: 'DocumentSidebar',
  props: ['headers', 'setAnchor'],
  watch: {
    headers(headers) {
      console.log('headers', headers);
    },
  },
};
</script>

<style scoped>
.document-sidebar {
  width: 300px;
  height: 100%;
  text-align: left;
  overflow: auto;
}
.document-sidebar >>> h1 {
  font-size: 25px;
}
.document-sidebar >>> h2 {
  font-size: 22px;
}
.document-sidebar >>> h3 {
  font-size: 20px;
}
.document-sidebar >>> h4 {
  font-size: 18px;
}
.document-sidebar >>> h5 {
  font-size: 15px;
}
.document-sidebar >>> h6 {
  font-size: 12px;
}
</style>

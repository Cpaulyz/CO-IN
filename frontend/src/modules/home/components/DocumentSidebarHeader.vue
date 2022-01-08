<template>
  <div class="document-sidebar-header" @click="setAnchor">
    <div :is="header.h.tagName" :id="header.id">{{ header.text }}</div>
    <DocumentSidebarHeader
      v-for="node in header.sub"
      :key="node.id"
      :header="node"
      :setAnchor="setAnchor"
    />
  </div>
</template>

<script>
export default {
  name: 'DocumentSidebarHeader',
  props: ['header', 'setAnchor'],
};
</script>

<style scoped>
.document-sidebar-header {
  cursor: pointer;
}
</style>

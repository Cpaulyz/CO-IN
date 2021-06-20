<template>
  <div>
    <h4>查看实体类型</h4>
    <el-checkbox
      :indeterminate="isIndeterminate"
      :value="selectAll"
      @change="selectAllChange"
      >全选</el-checkbox
    >
    <el-checkbox-group :value="selectedGroups">
      <el-checkbox
        v-for="group in sortedGroups"
        :key="group"
        :label="group"
        @change="toggleGroup(group)"
        >{{ group }}</el-checkbox
      >
    </el-checkbox-group>
  </div>
</template>

<script>
export default {
  name: 'GraphEditorGroup',
  props: {
    groups: {
      type: Array
    }
  },
  data() {
    return {
      selectedGroups: [],
      selectAll: false
    }
  },
  computed: {
    sortedGroups() {
      // console.log('groups update', this.groups)
      const newGroups = this.groups.sort((g1, g2) =>
        g1 < g2 ? -1 : g1 === g2 ? 0 : 1
      )
      this.selectedGroups = this.groups.slice()
      this.selectAll = true
      return newGroups
    },
    isIndeterminate() {
      const { selectedGroups, selectAll } = this
      return selectedGroups.length > 0 && !selectAll
    }
  },
  watch: {
    selectedGroups(groups) {
      this.$emit('graph-action', 'selectGroups', groups.slice())
    }
  },
  methods: {
    toggleGroup(group) {
      const { selectedGroups, sortedGroups } = this
      if (selectedGroups.includes(group)) {
        selectedGroups.splice(selectedGroups.indexOf(group), 1)
      } else {
        selectedGroups.push(group)
      }
      this.selectAll = selectedGroups.length === sortedGroups.length
    },
    selectAllChange(bool) {
      this.selectedGroups = bool ? this.groups.slice() : []
      this.selectAll = bool
    }
  }
}
</script>

<style scoped>
h4 {
  display: inline-block;
  margin-right: 10px;
}
</style>

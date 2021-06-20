<template>
  <div class="container">
    <el-button
      v-for="action in actions"
      :key="action.label"
      :type="action.type"
      :plain="action.mode && layoutMode !== action.mode"
      size="medium"
      @click="buttonHandlerWrapper($event, action.handler)"
      style="margin-bottom: 10px; width: 100%"
      >{{ action.label }}</el-button
    >
  </div>
</template>

<script>
import { buttonAutoBlur } from '@/common/utils'

export default {
  name: 'GraphLayout',
  props: {
    layoutMode: {
      type: String
    }
  },
  data() {
    return {
      actions: [
        {
          label: '力导图模式',
          mode: 'FORCE',
          type: 'primary',
          handler: () => this.$emit('graph-action', 'switchLayout', 'FORCE')
        },
        {
          label: '排版模式',
          mode: 'GRID',
          type: 'danger',
          handler: () => this.$emit('graph-action', 'switchLayout', 'GRID')
        },
        {
          label: '定点模式',
          mode: 'FIXED',
          type: 'warning',
          handler: () => this.$emit('graph-action', 'switchLayout', 'FIXED')
        },
        {
          label: '保存布局',
          type: 'info',
          handler: () => this.$emit('graph-action', 'saveLayout')
        },
        {
          label: '恢复布局',
          type: 'info',
          handler: () => this.$emit('graph-action', 'restoreLayout')
        }
      ]
    }
  },
  methods: {
    buttonHandlerWrapper(e, handler) {
      buttonAutoBlur(e)
      handler()
    }
  }
}
</script>

<style scoped>
.container {
  width: 100%;
  height: 100%;
}
/* .el-button {
  margin: unset;
  margin-bottom: 10px;
  width: 100%;
} */
.el-button+.el-button[style] {
  margin: 0;
}
</style>

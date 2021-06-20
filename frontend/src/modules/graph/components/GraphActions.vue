<template>
  <div class="container">
    <el-button
      v-for="option in options"
      size="medium"
      :key="option.label"
      :type="option.type"
      @click="buttonHandlerWrapper($event, option.handler)"
      style="margin-bottom: 10px; width: 100%"
      >{{ option.label }}</el-button
    >
  </div>
</template>

<script>
import { buttonAutoBlur } from '@/common/utils'
import { debounceAndThrottle } from '../utils/function'

export default {
  name: 'GraphActions',
  data() {
    return {
      options: [
        {
          label: '新增实体',
          type: 'primary',
          handler: () => this.$emit('editor-action', 'createNode')
        },
        {
          label: '新增关系',
          type: 'primary',
          handler: () => this.$emit('editor-action', 'createLink')
        },
        {
          label: '重置缩放',
          type: 'danger',
          handler: debounceAndThrottle(
            () => this.$emit('graph-action', 'resetZoom'),
            200
          )
        },
        {
          label: '随机分布',
          type: 'danger',
          handler: debounceAndThrottle(
            () => this.$emit('graph-action', 'randomDisorder'),
            200
          )
        },
        {
          label: '保存为 png',
          type: 'warning',
          handler: () => this.$emit('graph-action', 'exportPng')
        },
        {
          label: '保存为 xml',
          type: 'warning',
          handler: () => this.$emit('graph-action', 'exportXml')
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

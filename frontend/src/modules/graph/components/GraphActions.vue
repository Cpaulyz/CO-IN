<template>
  <div class="container">
    <el-button
      v-for="option in options"
      class="option"
      size="medium"
      :key="option.label"
      :type="option.type"
      @click="buttonHandlerWrapper($event, option.handler)"
      style="margin-bottom: 10px; width: 100%"
    >
      {{ option.label }}
      <!-- 详细说明 -->
      <el-popover
        v-if="option.desc"
        placement="right"
        trigger="hover"
        :title="option.desc.title"
      >
        <!-- 外部样式 -->
        <i class="el-icon-info" slot="reference"></i>
        <!-- 弹出样式 -->
        <span>{{ option.desc.content }}</span>
        <el-link
          v-if="option.desc.id"
          type="primary"
          @click="moreDescription(option.desc.id)"
          >了解更多</el-link
        >
      </el-popover>
    </el-button>
  </div>
</template>

<script>
import { buttonAutoBlur } from '@/common/utils';
import { debounceAndThrottle } from '../utils/function';

export default {
  name: 'GraphActions',
  data() {
    return {
      options: [
        {
          label: '新增实体',
          type: 'primary',
          handler: () => this.$emit('editor-action', 'createNode'),
          desc: {
            title: '新增实体',
            content: '向图谱添加新的实体节点',
            id: '1311-实体操作',
          },
        },
        {
          label: '新增关系',
          type: 'primary',
          handler: () => this.$emit('editor-action', 'createLink'),
          desc: {
            title: '新增关系',
            content: '向图谱添加新的关系边',
            id: '1312-关系操作',
          },
        },
        {
          label: '重置缩放',
          type: 'danger',
          handler: debounceAndThrottle(
            () => this.$emit('graph-action', 'resetZoom'),
            200,
          ),
          desc: {
            title: '重置缩放',
            content: '将图谱缩放比例重置成适当的大小',
          },
        },
        {
          label: '随机分布',
          type: 'danger',
          handler: debounceAndThrottle(
            () => this.$emit('graph-action', 'randomDisorder'),
            200,
          ),
          desc: {
            title: '随机分布',
            content: '将图谱缩放比例重置成适当的大小',
          },
        },
        {
          label: '保存为 png',
          type: 'warning',
          handler: () => this.$emit('graph-action', 'exportPng'),
          desc: {
            title: '保存为 png',
            content: '导出 png 图片',
            id: '161-保存为png',
          },
        },
        {
          label: '保存为 xml',
          type: 'warning',
          handler: () => this.$emit('graph-action', 'exportXml'),
          desc: {
            title: '保存为 xml',
            content: '导出 xml 文件',
            id: '162-保存为xml',
          },
        },
      ],
    };
  },
  methods: {
    buttonHandlerWrapper(e, handler) {
      buttonAutoBlur(e);
      handler();
    },
    moreDescription(id) {
      this.$emit('check-document', id);
    },
  },
};
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
.el-button + .el-button[style] {
  margin: 0;
}
.option >>> span {
  display: flex;
  justify-content: space-between;
}
</style>

<template>
  <div class="container">
    <div class="layout-option" v-for="action in actions" :key="action.label">
      <el-button
        class="option"
        :type="action.type"
        :plain="action.mode && layoutMode !== action.mode"
        size="medium"
        @click="buttonHandlerWrapper($event, action.handler)"
      >
        {{ action.label }}
        <!-- 详细说明 -->
        <el-popover
          v-if="action.desc"
          placement="right"
          trigger="hover"
          :title="action.desc.title"
        >
          <!-- 外部样式 -->
          <i class="el-icon-info" slot="reference"></i>
          <!-- 弹出样式 -->
          <span>{{ action.desc.content }}</span>
          <el-link
            v-if="action.desc.id"
            type="primary"
            @click="moreDescription(action.desc.id)"
            >了解更多</el-link
          >
        </el-popover>
      </el-button>
    </div>
  </div>
</template>

<script>
import { buttonAutoBlur } from '@/common/utils';

export default {
  name: 'GraphLayout',
  props: {
    layoutMode: {
      type: String,
    },
  },
  data() {
    return {
      actions: [
        {
          label: '力导图模式',
          mode: 'FORCE',
          type: 'primary',
          handler: () => this.$emit('graph-action', 'switchLayout', 'FORCE'),
          desc: {
            title: '力导图布局模式',
            content: '用来呈现复杂关系网络',
            id: '132-布局内容',
          },
        },
        {
          label: '排版模式',
          mode: 'GRID',
          type: 'danger',
          handler: () => this.$emit('graph-action', 'switchLayout', 'GRID'),
          desc: {
            title: '排版布局模式',
            content: '实体按类别分列展示',
            id: '132-布局内容',
          },
        },
        {
          label: '定点模式',
          mode: 'FIXED',
          type: 'warning',
          handler: () => this.$emit('graph-action', 'switchLayout', 'FIXED'),
          desc: {
            title: '定点布局模式',
            content: '可自由操作任意节点',
            id: '132-布局内容',
          },
        },
        {
          label: '保存布局',
          type: 'info',
          handler: () => this.$emit('graph-action', 'saveLayout'),
          desc: {
            title: '保存布局',
            content: '保存当前布局模式快照',
          },
        },
        {
          label: '恢复布局',
          type: 'info',
          handler: () => this.$emit('graph-action', 'restoreLayout'),
          desc: {
            title: '恢复布局',
            content: '恢复成当前布局模式上次保存的快照',
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
.layout-option {
  display: flex;
  align-items: center;
}
.layout-option .el-button {
  flex: 1;
  margin: 0;
}
.layout-option + .layout-option {
  margin-top: 10px;
}
.container i {
  margin: 0 0 0 5px;
  /* color: #e3e3e3; */
  color: #fff;
  cursor: pointer;
}
.option >>> span {
  display: flex;
  justify-content: space-between;
}
</style>

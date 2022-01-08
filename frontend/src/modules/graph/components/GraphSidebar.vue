<template>
  <el-menu
    @open="handleOpen"
    @close="handleClose"
    :unique-opened="true"
    class="sidemenu"
  >
    <!-- 布局操作 -->
    <el-submenu index="1">
      <template slot="title">
        <i class="el-icon-s-tools"></i>
        <span>布局设置</span>
      </template>
      <el-menu-item-group style="text-align: center">
        <graph-layout
          :layoutMode="layoutMode"
          @graph-action="passingGraphAction"
          @check-document="passingCheckDocument"
        />
      </el-menu-item-group>
    </el-submenu>
    <!-- 图谱操作 -->
    <el-submenu index="2">
      <template slot="title">
        <i class="el-icon-edit"></i>
        <span>图谱操作</span>
      </template>
      <el-menu-item-group style="text-align: center">
        <graph-actions
          @graph-action="passingGraphAction"
          @editor-action="passingEditorAction"
          @check-document="passingCheckDocument"
        />
      </el-menu-item-group>
    </el-submenu>
    <!-- 智能服务 -->
    <el-submenu index="3">
      <template slot="title">
        <i class="el-icon-magic-stick"></i>
        <span>智能服务</span>
      </template>
      <el-menu-item-group style="text-align: center">
        <el-button
          v-for="smartOption in smartOptions"
          :key="smartOption.label"
          class="option"
          type="success"
          style="margin-bottom: 10px; width: 100%"
          @click="smartOption.handler"
        >
          {{ smartOption.label }}
          <!-- 详细说明 -->
          <el-popover
            v-if="smartOption.desc"
            placement="right"
            trigger="hover"
            :title="smartOption.desc.title"
          >
            <!-- 外部样式 -->
            <i class="el-icon-info" slot="reference"></i>
            <!-- 弹出样式 -->
            <span>{{ smartOption.desc.content }}</span>
            <el-link
              v-if="smartOption.desc.id"
              type="primary"
              @click="passingCheckDocument(smartOption.desc.id)"
              >了解更多</el-link
            >
          </el-popover>
        </el-button>
      </el-menu-item-group>
    </el-submenu>
    <!-- 项目设置 -->
    <el-submenu index="4">
      <template slot="title">
        <i class="el-icon-setting"></i>
        <span>项目设置</span>
      </template>
      <el-menu-item-group style="text-align: center">
        <el-button
          v-for="settingOption in settingOptions"
          :key="settingOption.label"
          class="option"
          type="info"
          style="margin-bottom: 10px; width: 100%"
          @click="settingOption.handler"
        >
          {{ settingOption.label }}
          <!-- 详细说明 -->
          <el-popover
            v-if="settingOption.desc"
            placement="right"
            trigger="hover"
            :title="settingOption.desc.title"
          >
            <!-- 外部样式 -->
            <i class="el-icon-info" slot="reference"></i>
            <!-- 弹出样式 -->
            <span>{{ settingOption.desc.content }}</span>
            <el-link
              v-if="settingOption.desc.id"
              type="primary"
              @click="passingCheckDocument(settingOption.desc.id)"
              >了解更多</el-link
            >
          </el-popover>
        </el-button>
      </el-menu-item-group>
    </el-submenu>
    <div style="text-align: center; margin: 10px 0">
      <el-link type="primary" @click="passingCheckDocument()">
        查看操作手册
        <i class="el-icon-right"></i>
      </el-link>
    </div>
  </el-menu>
</template>

<script>
import { buttonAutoBlur } from '@/common/utils';
import { mapGetters, mapActions, mapMutations } from 'vuex';
import GraphLayout from './GraphLayout';
import GraphActions from './GraphActions';

export default {
  name: 'GraphSideBar',
  props: {
    layoutMode: {
      type: String,
      default: 'FORCE',
    },
  },
  components: {
    GraphLayout,
    GraphActions,
  },
  data() {
    return {
      // flag: true, // mock verifyInitiate
      smartOptions: [
        // {
        //   label: '初始化图谱',
        //   handler: () => this.initGraph(),
        // },
        // {
        //   label: '智能小助手 PC 端',
        //   handler: () => this.gotoSmarthelper('/chat'),
        // },
        {
          label: '智能小助手',
          handler: () => this.gotoSmarthelper('/smarthelper'),
          desc: {
            title: '智能小助手',
            content: '提供图谱问答、实体查询、关系查询、中心识别等功能',
            id: '22-图谱问答',
          },
        },
        {
          label: '实体类型统计',
          handler: () => this.$emit('editor-action', 'mode', 'showStatistics'),
          desc: {
            title: '实体类型统计',
            content: '图谱实体数据统计',
            id: '14-图谱统计',
          },
        },
      ],
      settingOptions: [
        {
          label: '修改项目信息',
          handler: () => this.changeProjectInfo(),
          desc: {
            title: '修改项目信息',
            id: '12-编辑项目',
          },
        },
        // {
        //   label: '修改项目名称',
        //   handler: () => this.changeName(),
        //   desc: {
        //     title: '修改项目名称',
        //     id: '12-编辑项目',
        //   },
        // },
        // {
        //   label: '修改项目描述',
        //   handler: () => this.changeDesc(),
        //   desc: {
        //     title: '修改项目描述',
        //     id: '12-编辑项目',
        //   },
        // },
        // {
        //   label: '修改项目权限',
        //   handler: () => this.changeStatus(),
        //   desc: {
        //     title: '修改项目权限',
        //     id: '12-编辑项目',
        //   },
        // },
      ],
    };
  },
  computed: {
    ...mapGetters(['graphBoardMode', 'projectId']),
  },
  methods: {
    ...mapMutations([
      'setShowModifyNameModal',
      'setShowModifyDescModal',
      'setShowModifyStatusModal',
      'setShowModifyProjectInfoModal',
    ]),
    ...mapActions(['switchLayoutMode', 'verifyInitiate', 'initiateGraph']),
    passingGraphAction(...args) {
      this.passingEmit('graph-action', ...args);
    },
    passingEditorAction(...args) {
      this.passingEmit('editor-action', ...args);
    },
    passingCheckDocument(id) {
      this.passingEmit('check-document', id);
    },
    passingEmit(eventName, ...args) {
      this.$emit(eventName, ...args);
    },
    handleOpen(key, keyPath) {
      // console.log(key, keyPath)
    },
    handleClose(key, keyPath) {
      // console.log(key, keyPath)
    },
    gotoSmarthelper(path) {
      const projectId = Number(this.$route.params.projectId);
      this.initiateGraph(projectId);
      this.verifyInitiate(projectId).then((res) => {
        if (res) {
          this.$router.push(`${path}/${projectId}`);
        } else {
          this.$router.push(`/graph/${projectId}`);
          this.$message.error('请先初始化图谱!');
        }
      });
    },
    initGraph() {
      const projectId = Number(this.$route.params.projectId);
      this.initiateGraph(projectId).then((res) => {
        const { success, msg } = res.data;
        if (success) {
          this.$message.success(msg);
        } else {
          this.$message.error(msg);
        }
      });
    },
    handlerDispatcher(e, handler) {
      buttonAutoBlur(e);
      handler();
    },
    changeName() {
      this.setShowModifyNameModal(true);
    },
    changeDesc() {
      this.setShowModifyDescModal(true);
    },
    changeStatus() {
      this.setShowModifyStatusModal(true);
    },
    changeProjectInfo() {
      this.setShowModifyProjectInfoModal(true);
    },
  },
};
</script>

<style scoped>
.graph > .main > .sidemenu {
  width: 150px;
  position: fixed;
  left: 0;
  top: 100px;
  z-index: 1000;
}
.el-button + .el-button[style] {
  margin: 0;
}
.option >>> span {
  display: flex;
  justify-content: space-between;
}
.option >>> span i {
  margin: 0;
}
</style>

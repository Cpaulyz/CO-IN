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
          type="success"
          style="margin-bottom: 10px; width: 100%"
          @click="smartOption.handler"
        >
          {{ smartOption.label }}
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
          type="info"
          style="margin-bottom: 10px; width: 100%"
          @click="settingOption.handler"
        >
          {{ settingOption.label }}
        </el-button>
      </el-menu-item-group>
    </el-submenu>
  </el-menu>
</template>

<script>
import { buttonAutoBlur } from '@/common/utils'
import { mapGetters, mapActions, mapMutations } from 'vuex'
import GraphLayout from './GraphLayout'
import GraphActions from './GraphActions'

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
        {
          label: '初始化图谱',
          handler: () => this.initGraph(),
        },
        // {
        //   label: '智能小助手 PC 端',
        //   handler: () => this.gotoSmarthelper('/chat'),
        // },
        {
          label: '智能小助手',
          handler: () => this.gotoSmarthelper('/smarthelper'),
        },
      ],
      settingOptions: [
        {
          label: '修改项目名称',
          handler: () => this.changeName(),
        },
        {
          label: '修改项目描述',
          handler: () => this.changeDesc(),
        },
        {
          label: '修改项目权限',
          handler: () => this.changeStatus(),
        },
      ],
    }
  },
  computed: {
    ...mapGetters(['graphBoardMode', 'projectId']),
  },
  methods: {
    ...mapMutations([
      'setShowModifyNameModal',
      'setShowModifyDescModal',
      'setShowModifyStatusModal',
    ]),
    ...mapActions(['switchLayoutMode', 'verifyInitiate', 'initiateGraph']),
    passingGraphAction(...args) {
      this.passingEmit('graph-action', ...args)
    },
    passingEditorAction(...args) {
      this.passingEmit('editor-action', ...args)
    },
    passingEmit(eventName, ...args) {
      this.$emit(eventName, ...args)
    },
    handleOpen(key, keyPath) {
      // console.log(key, keyPath)
    },
    handleClose(key, keyPath) {
      // console.log(key, keyPath)
    },
    gotoSmarthelper(path) {
      const projectId = Number(this.$route.params.projectId)
      this.initiateGraph(projectId)
      this.verifyInitiate(projectId).then((res) => {
        if (res) {
          this.$router.push(`${path}/${projectId}`)
        } else {
          this.$router.push(`/graph/${projectId}`)
          this.$message.error('请先初始化图谱!')
        }
      })
    },
    initGraph() {
      const projectId = Number(this.$route.params.projectId)
      this.initiateGraph(projectId).then((res) => {
        const { success, msg } = res.data
        if (success) {
          this.$message.success(msg)
        } else {
          this.$message.error(msg)
        }
      })
    },
    handlerDispatcher(e, handler) {
      buttonAutoBlur(e)
      handler()
    },
    changeName() {
      this.setShowModifyNameModal(true)
    },
    changeDesc() {
      this.setShowModifyDescModal(true)
    },
    changeStatus() {
      this.setShowModifyStatusModal(true)
    },
  },
}
</script>

<style scoped>
.graph > .main > .sidemenu {
  width: 150px;
  position: fixed;
  left: 0;
  top: 100px;
}
.el-button + .el-button[style] {
  margin: 0;
}
</style>

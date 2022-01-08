<template>
  <el-dialog
    title="设定 - 项目权限"
    :visible="showModifyStatusModal"
    width="30%"
    @close="disablePanel()"
  >
    <el-form label-width="200px" label-position="top">
      <el-form-item label="项目权限">
        <el-input
          :placeholder="projectInfo.status === 'PUBLIC' ? '公开' : '私密'"
          :disabled="true"
        ></el-input>
      </el-form-item>
      <el-form-item label="修改项目权限" prop="desc">
        <el-input
          :placeholder="projectInfo.status !== 'PUBLIC' ? '公开' : '私密'"
          :disabled="true"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          style="width: 100%"
          round
          @click="commitUpdateStatus()"
          >提交</el-button
        >
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex';

export default {
  name: 'GraphModifyStatusModal',
  data() {
    return {
      newStatus: '',
      currentStatus: '',
    };
  },
  computed: {
    ...mapGetters(['showModifyStatusModal', 'projectInfo']),
  },
  methods: {
    ...mapMutations(['setShowModifyStatusModal']),
    ...mapActions(['updateProjectStatus']),
    disablePanel() {
      this.setShowModifyStatusModal(false);
    },
    commitUpdateStatus() {
      const projectId = Number(this.$route.params.projectId);
      const data = {
        status: this.projectInfo.status === 'PUBLIC' ? 'PRIVATE' : 'PUBLIC',
        projectId,
      };
      this.updateProjectStatus(data).then(res => {
        const { success, msg } = res;
        if (success) {
          this.setShowModifyStatusModal(false);
          this.$message.success(msg);
          this.$router.go(0);
        } else {
          this.$message.error(msg);
        }
      });
    },
  },
};
</script>

<style></style>

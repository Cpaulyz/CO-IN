<template>
  <el-dialog
    title="设定 - 项目名称"
    :visible="showModifyNameModal"
    width="30%"
    @close="disablePanel()"
  >
    <el-form label-width="200px" label-position="top">
      <el-form-item label="项目名称">
        <el-input :placeholder="projectInfo.name" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="修改项目名称" prop="name">
        <el-input v-model="newName" placeholder="新项目名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          style="width: 100%"
          round
          @click="commitUpdateName()"
          >提交</el-button
        >
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex';

export default {
  name: 'GraphModifyNameModal',
  data() {
    return {
      newName: '',
    };
  },
  computed: {
    ...mapGetters(['showModifyNameModal', 'projectInfo']),
  },
  methods: {
    ...mapMutations(['setShowModifyNameModal']),
    ...mapActions(['updateProjectName']),
    disablePanel() {
      this.setShowModifyNameModal(false);
    },
    commitUpdateName() {
      const projectId = Number(this.$route.params.projectId);
      const data = {
        name: this.newName,
        projectId,
      };
      this.updateProjectName(data).then(res => {
        const { success, msg } = res;
        if (success) {
          this.setShowModifyNameModal(false);
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

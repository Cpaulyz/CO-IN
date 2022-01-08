<template>
  <el-dialog
    title="设定 - 项目描述"
    :visible="showModifyDescModal"
    width="30%"
    @close="disablePanel()"
  >
    <el-form label-width="200px" label-position="top">
      <el-form-item label="项目描述">
        <el-input
          :placeholder="projectInfo.description"
          :disabled="true"
        ></el-input>
      </el-form-item>
      <el-form-item label="修改项目描述" prop="desc">
        <el-input v-model="newDesc" placeholder="新项目描述"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          style="width: 100%"
          round
          @click="commitUpdateDesc()"
          >提交</el-button
        >
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex';

export default {
  name: 'GraphModifyDescModal',
  data() {
    return {
      newDesc: '',
    };
  },
  computed: {
    ...mapGetters(['showModifyDescModal', 'projectInfo']),
  },
  methods: {
    ...mapMutations(['setShowModifyDescModal']),
    ...mapActions(['updateProjectDesc']),
    disablePanel() {
      this.setShowModifyDescModal(false);
    },
    commitUpdateDesc() {
      const projectId = Number(this.$route.params.projectId);
      const data = {
        description: this.newDesc,
        projectId,
      };
      this.updateProjectDesc(data).then(res => {
        const { success, msg } = res;
        if (success) {
          this.setShowModifyDescModal(false);
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
